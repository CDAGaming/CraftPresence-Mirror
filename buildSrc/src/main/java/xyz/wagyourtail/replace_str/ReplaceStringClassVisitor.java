package xyz.wagyourtail.replace_str;

import org.objectweb.asm.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceStringClassVisitor extends ClassVisitor {
    public static final Pattern replacePattern = Pattern.compile("@(.+?)@");
    Map<String, String> replaceTokens;

    public ReplaceStringClassVisitor(ClassVisitor delegate, Map<String, String> replaceTokens) {
        super(Opcodes.ASM9, delegate);
        this.replaceTokens = replaceTokens;
    }

    public static String replaceMatching(String value, Map<String, String> replaceTokens) {
        Matcher m = replacePattern.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            m.appendReplacement(sb, replaceTokens.getOrDefault(m.group(1), m.group(1)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
            @Override
            public void visitLdcInsn(Object value) {
                if (value instanceof String) {
                    super.visitLdcInsn(replaceMatching((String) value, replaceTokens));
                    return;
                }
                super.visitLdcInsn(value);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitParameterAnnotation(parameter, descriptor, visible), replaceTokens);
            }

            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitInsnAnnotation(typeRef, typePath, descriptor, visible), replaceTokens);
            }

            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible), replaceTokens);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitTypeAnnotation(typeRef, typePath, descriptor, visible), replaceTokens);
            }
        };
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (value instanceof String) {
            return new FieldVisitor(api, super.visitField(access, name, descriptor, signature, replaceMatching((String) value, replaceTokens))) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
                }

                @Override
                public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                    return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
                }
            };
        }
        return new FieldVisitor(api, super.visitField(access, name, descriptor, signature, value)) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
                return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
            }
        };
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new ReplaceStringAnnotationVisitor(super.visitAnnotation(descriptor, visible), replaceTokens);
    }

    public static class ReplaceStringAnnotationVisitor extends AnnotationVisitor {
        Map<String, String> replaceTokens;

        public ReplaceStringAnnotationVisitor(AnnotationVisitor delegate, Map<String, String> replaceTokens) {
            super(Opcodes.ASM9, delegate);
            this.replaceTokens = replaceTokens;
        }

        @Override
        public void visit(String name, Object value) {
            if (value instanceof String) {
                super.visit(name, replaceMatching((String) value, replaceTokens));
                return;
            }
            super.visit(name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ReplaceStringAnnotationVisitor(super.visitArray(name), replaceTokens);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            return new ReplaceStringAnnotationVisitor(super.visitAnnotation(name, descriptor), replaceTokens);
        }
    }
}
