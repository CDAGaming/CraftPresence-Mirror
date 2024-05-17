package xyz.wagyourtail.replace_str;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Stream;

public class ProcessClasses {
    public static boolean isClassFile(byte[] byteArr) {
        // check magic
        return byteArr[0] == (byte) 0xCA && byteArr[1] == (byte) 0xFE && byteArr[2] == (byte) 0xBA && byteArr[3] == (byte) 0xBE;
    }

    public static void process(Path input, Map<String, String> replaceTokens) throws IOException {
        final Stream<Path> entries = Files.walk(input);
        entries.forEach(file -> {
            if (Files.isDirectory(file)) return;
            try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file))) {
                is.mark(9);
                byte[] byteArr = new byte[8];
                if (is.read(byteArr) < 8)
                    return;
                if (!isClassFile(byteArr)) return;
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
