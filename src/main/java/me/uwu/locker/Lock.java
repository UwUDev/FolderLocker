package me.uwu.locker;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
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
        Crypto cryptoZip = crypto;
        cryptoZip.seedKey(3);
        crypto = cryptoZip;
        crypto.seedKey(2);

        try (Stream<Path> walk = Files.walk(Paths.get("lock me daddy/"))) {
            walk.forEach(f -> {
                if (!f.toFile().isDirectory()) result.add(f.toFile());
            });

            result.forEach(System.out::println);

            Crypto finalCrypto = crypto;
            result.forEach(f -> {
                try {
                    finalCrypto.crypt(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);



        ZipFile zipFile = new ZipFile("folder.lock", pass.toCharArray());
        zipFile.addFolder(new File("lock me daddy/"), zipParameters);

        cryptoZip.crypt(new File("folder.lock"));

        FileUtils.deleteDirectory(new File("lock me daddy/"));
    }
}
