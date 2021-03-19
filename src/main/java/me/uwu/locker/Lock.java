package me.uwu.locker;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Lock {
    public static String pass = "hello";

    public static void main(String[] args) throws IOException {
        List<File> result = new ArrayList<>();
        Crypto crypto = new Crypto(pass);

        try (Stream<Path> walk = Files.walk(Paths.get("lock me daddy/"))) {
            walk.forEach(f -> {
                if (!f.toFile().isDirectory()) result.add(f.toFile());
            });

            result.forEach(System.out::println);

            result.forEach(f -> {
                try {
                    crypto.crypt(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ZipFile("folder.lock").addFolder(new File("lock me daddy/"));
        crypto.crypt(new File("folder.lock"));

        FileUtils.deleteDirectory(new File("lock me daddy/"));
    }
}
