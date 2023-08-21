package xyz.wagyourtail.replace_str;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import xyz.wagyourtail.mini_jvmdg.MiniJVMDowngrade;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Stream;

public class ProcessClasses {
    public static void process(Path input, Map<String, String> replaceTokens) throws IOException {
        final Stream<Path> entries = Files.walk(input);
        entries.forEach(file -> {
            if (Files.isDirectory(file)) return;
            try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file))) {
                is.mark(9);
                byte[] byteArr = new byte[8];
                if (is.read(byteArr) < 8)
                    return;
                if (!MiniJVMDowngrade.isClassFile(byteArr)) return;
                is.reset();
                byte[] clazz = is.readAllBytes();
                ClassReader cr = new ClassReader(clazz);
                ClassWriter cw = new ClassWriter(0);
                cr.accept(new ReplaceStringClassVisitor(cw, replaceTokens), 0);
                Files.write(file, cw.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        entries.close();
    }
}
