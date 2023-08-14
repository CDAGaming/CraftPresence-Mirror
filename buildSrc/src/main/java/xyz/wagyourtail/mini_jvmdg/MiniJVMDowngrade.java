package xyz.wagyourtail.mini_jvmdg;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class MiniJVMDowngrade {

    public static FileSystem openZipFileSystem(Path path, Map<String, Object> options) throws IOException {
        if (options.containsKey("create")) {
            if (options.get("create") == Boolean.TRUE) {
                options.put("create", "true");
            }
        }
        return FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), options, null);
    }

    public static void downgradeZip(Path zip, Set<Path> classpath) throws IOException {
        try (FileSystem fs = openZipFileSystem(zip, new HashMap<>())) {
            downgradeDirectory(fs.getPath("/"), classpath);
        }
    }

    public static void downgradeDirectory(Path root, Set<Path> classpath) throws IOException {
        try (URLClassLoader dummy = new URLClassLoader(classpath.stream().map(e -> {
            try {
                return e.toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }).toArray(URL[]::new))) {
            Map<String, ClassNode> toWrite = new HashMap<>();
            Map<String, String> skippedSupers = new HashMap<>();
            Map<String, Map<String, Boolean>> toMove = new HashMap<>();
            try (Stream<Path> entries = Files.walk(root)) {
                entries.forEach(file -> {
                    if (Files.isDirectory(file)) return;
                    try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file))) {
                        is.mark(9);
                        byte[] byteArr = new byte[8];
                        if (is.read(byteArr) < 8)
                            return;
                        if (!isClassFile(byteArr)) return;
                        var version = ((byteArr[6] & 0xFF) << 8) | (byteArr[7] & 0xFF);
                        if (version > Opcodes.V1_8) throw new IllegalStateException("should be java 8");
                        is.reset();
                        byte[] clazz = is.readAllBytes();
                        if (version <= Opcodes.V1_7) {
                            ClassReader cr = new ClassReader(clazz);
                            skippedSupers.put(cr.getClassName(), cr.getSuperName());
                            return;
                        }
                        ClassNode classNode = new ClassNode();
                        ClassReader cr = new ClassReader(clazz);
                        cr.accept(classNode, 0);
                        classNode.version = Opcodes.V1_7;
                        // check if it is an interface
                        if ((classNode.access & Opcodes.ACC_INTERFACE) != 0) {
                            ClassNode defaults = constructDefaults(classNode, toMove);
                            if (!defaults.methods.isEmpty()) {
                                toWrite.put(defaults.name, defaults);
                            }
                        }
                        toWrite.put(classNode.name, classNode);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }

            for (Map.Entry<String, ClassNode> entry : toWrite.entrySet()) {
                ClassNode fixedNode = fixMethodInsns(entry.getValue(), toMove, toWrite);
                try {
                    ClassWriter cw = new ASMClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, (s) -> {
                        try {
                            if (toWrite.containsKey(s)) {
                                return toWrite.get(s).superName;
                            } else if (skippedSupers.containsKey(s)) {
                                return skippedSupers.get(s);
                            } else {
                                URL url = dummy.getResource(s + ".class");
                                if (url != null) {
                                    try (InputStream is = url.openStream()) {
                                        ClassReader cr = new ClassReader(is.readAllBytes());
                                        return cr.getSuperName();
                                    }
                                }
                            }
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    fixedNode.accept(cw);
                    Files.write(root.resolve(entry.getKey() + ".class"), cw.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                } catch (Exception e) {
                    System.out.println("Writing " + entry.getKey() + " failed!!!!!");
                    throw e;
                }
            }
        }
    }

    public static boolean isClassFile(byte[] byteArr) {
        // check magic
        return byteArr[0] == (byte) 0xCA && byteArr[1] == (byte) 0xFE && byteArr[2] == (byte) 0xBA && byteArr[3] == (byte) 0xBE;
    }

    private static ClassNode constructDefaults(ClassNode inputClass, Map<String, Map<String, Boolean>> toMove) {
        ClassNode defaults = new ClassNode();
        defaults.version = Opcodes.V1_7;
        defaults.access = Opcodes.ACC_PUBLIC;
        defaults.name = inputClass.name + "$jvmdg$DefaultsAndStatics";
        defaults.superName = "java/lang/Object";
        for (MethodNode method : new ArrayList<>(inputClass.methods)) {
            if ((method.access & Opcodes.ACC_STATIC) != 0) {
                // is static
                inputClass.methods.remove(method);
                // add method to defaults
                defaults.methods.add(method);
                toMove.computeIfAbsent(inputClass.name, (e) -> new HashMap<>())
                        .put(method.name + method.desc, false);
            } else if ((method.access & Opcodes.ACC_ABSTRACT) == 0) {
                // is default
                inputClass.methods.remove(method);
                // create an abstract version on the original
                inputClass.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT, method.name, method.desc, null, null).visitEnd();
                toMove.computeIfAbsent(inputClass.name, (e) -> new HashMap<>())
                        .put(method.name + method.desc, true);
                // change desc to have this arg first...
                Type mType = Type.getMethodType(method.desc);
                Type[] args = mType.getArgumentTypes();
                Type[] fixedArgs = new Type[args.length + 1];
                Type returnType = mType.getReturnType();
                System.arraycopy(args, 0, fixedArgs, 1, args.length);
                fixedArgs[0] = Type.getType("L" + inputClass.name + ";");
                method.desc = Type.getMethodDescriptor(returnType, fixedArgs);
                // make static
                method.access |= Opcodes.ACC_STATIC;
                defaults.methods.add(method);
                // create a handler on defaults that will try catch abstract
                MethodVisitor mv = defaults.visitMethod(method.access, method.name + "$jvmdg$handler", method.desc, method.signature, method.exceptions.toArray(new String[0]));
                Label tri = new Label();
                Label cat = new Label();
                Label handle = new Label();
                mv.visitTryCatchBlock(tri, cat, handle, "java/lang/AbstractMethodError");
                mv.visitLabel(tri);
                // grab all args from stack
                int i = 0;
                for (Type arg : fixedArgs) {
                    mv.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), i);
                    i += arg.getSize();
                }
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, inputClass.name, method.name, Type.getMethodDescriptor(returnType, args), true);
                // if return isn't null
                if (returnType.getSize() != 0) {
                    mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
                } else {
                    mv.visitInsn(Opcodes.RETURN);
                }
                mv.visitLabel(cat);
                mv.visitLabel(handle);
                // grab all args from stack
                i = 0;
                for (Type arg : fixedArgs) {
                    mv.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), i);
                    i += arg.getSize();
                }
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, defaults.name, method.name, method.desc, false);
                // if return isn't null
                if (returnType.getSize() != 0) {
                    mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
                } else {
                    mv.visitInsn(Opcodes.RETURN);
                }
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        }
        return defaults;
    }

    private static ClassNode fixMethodInsns(ClassNode original, Map<String, Map<String, Boolean>> toMove, Map<String, ClassNode> toWrite) {
        ClassNode fixedNode = new ClassNode();
        original.accept(new ClassVisitor(Opcodes.ASM9, fixedNode) {
            @Override
            public MethodVisitor visitMethod(int access, String mName, String mDescriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, mName, mDescriptor, signature, exceptions)) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if (mName.contains("$jvmdg$handler")) {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            return;
                        }
                        // check if abstract
                        if (!toMove.containsKey(owner)) {
                            // check if method on owner
                            ClassNode ownerNode = toWrite.get(owner);
                            while (ownerNode != null) {
                                // check if owner has any interfaces that match
                                for (String anInterface : ownerNode.interfaces) {
                                    if (toMove.containsKey(anInterface) && toMove.get(anInterface).containsKey(name + descriptor)) {
                                        owner = anInterface;
                                        break;
                                    }
                                }
                                ownerNode = toWrite.get(ownerNode.superName);
                            }
                        }
                        if (toMove.containsKey(owner) && toMove.get(owner).containsKey(name + descriptor)) {
                            boolean isDefault = toMove.get(owner).containsKey(name + descriptor);
                            String newClass = owner + "$jvmdg$DefaultsAndStatics";
                            if (isDefault) {
                                Type mType = Type.getMethodType(descriptor);
                                Type[] args = mType.getArgumentTypes();
                                Type[] fixedArgs = new Type[args.length + 1];
                                Type returnType = mType.getReturnType();
                                System.arraycopy(args, 0, fixedArgs, 1, args.length);
                                fixedArgs[0] = Type.getType("L" + owner + ";");
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, owner + "$jvmdg$DefaultsAndStatics", name + "$jvmdg$handler", Type.getMethodDescriptor(returnType, fixedArgs), false);
                            } else {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, newClass, name, descriptor, false);
                            }
                        } else {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        }
                    }
                };
            }
        });
        return fixedNode;
    }
}
