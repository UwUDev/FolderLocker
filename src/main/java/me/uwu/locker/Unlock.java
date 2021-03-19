package me.uwu.locker;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Unlock {
    public static void main(String[] args) throws IOException {
        Crypto crypto = new Crypto(Lock.pass + "");
        List<File> result = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        boolean success = true;

        Files.copy(new File("folder.lock").toPath(), Paths.get("folder.lock.temp"));
        crypto.decrypt(new File("folder.lock.temp"));

        try {
            new ZipFile("folder.lock.temp", Lock.pass.toCharArray()).extractAll(uuid.toString() + "/");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Invalid password");
            success = false;
            new File("folder.lock.temp").delete();
            FileUtils.deleteDirectory(new File(uuid.toString()));
        }

        if (success) {
            FileUtils.moveDirectory(new File(uuid.toString() + "/lock me daddy/"), new File("lock me daddy/"));
            FileUtils.deleteDirectory(new File(uuid.toString()));
            new File("folder.lock.temp").delete();
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
}
