package me.uwu.locker;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Unlock {
    public static void main(String[] args) throws IOException {
        Crypto crypto = new Crypto(Lock.pass);
        List<File> result = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        crypto.decrypt(new File("folder.lock"));

        new ZipFile("folder.lock").extractAll(uuid.toString() + "/");

        FileUtils.moveDirectory(new File(uuid.toString() + "/lock me daddy/"), new File("lock me daddy/"));
        FileUtils.deleteDirectory(new File(uuid.toString()));
        new File("folder.lock").delete();

        try (Stream<Path> walk = Files.walk(Paths.get("lock me daddy/"))) {
            walk.forEach(f -> {
                if (!f.toFile().isDirectory()) result.add(f.toFile());
            });

            result.forEach(System.out::println);

            result.forEach(f -> {
                try {
                    crypto.decrypt(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
