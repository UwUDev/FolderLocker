package me.uwu.locker;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crypto {

    private String key;
    private String baseKey;
    private boolean print = false;

    private final String seedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#$%&'()*-+/;,?.§<>{}=£µùéè\\@çç^¨º¹²³[]|`~¡°¿¶¢¥©®±";

    public Crypto(String key) {
        this.key = key;
        this.baseKey = key;
    }

    public void printDebug(boolean b){
        this.print = b;
    }

    public byte[] getKeyBytes(){
        return this.key.getBytes(StandardCharsets.US_ASCII);
    }

    public byte[] decrypt(byte[] message){

        byte[] keyBytes = this.key.getBytes(StandardCharsets.US_ASCII);

        ArrayList<Byte> byteArray = new ArrayList<>();

        int state = 0;
        decoder(keyBytes, message, byteArray, state);

        return getBytes(byteArray);
    }

    public byte[] decrypt(File file) throws IOException {

        byte[] keyBytes = this.key.getBytes(StandardCharsets.US_ASCII);
        byte[] bytes = FileUtils.readFileToByteArray(file);

        ArrayList<Byte> byteArray = new ArrayList<>();

        int state = 0;
        decoder(keyBytes, bytes, byteArray, state);
        FileUtils.writeByteArrayToFile(file, getBytes(byteArray));

        return getBytes(byteArray);
    }

    public byte[] crypt(String message){
        byte[] keyBytes = this.key.getBytes(StandardCharsets.US_ASCII);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);

        ArrayList<Byte> byteArray = new ArrayList<>();

        int state = 0;
        coder(keyBytes, bytes, byteArray, state);

        return getBytes(byteArray);
    }

    public byte[] crypt(File file) throws IOException {
        byte[] keyBytes = this.key.getBytes(StandardCharsets.US_ASCII);
        byte[] bytes = FileUtils.readFileToByteArray(file);


        ArrayList<Byte> byteArray = new ArrayList<>();

        int state = 0;
        coder(keyBytes, bytes, byteArray, state);
        FileUtils.writeByteArrayToFile(file, getBytes(byteArray));

        return getBytes(byteArray);
    }

    private byte[] getBytes(ArrayList<Byte> byteArray) {
        if(this.print) {
            System.out.println("\n\n");

            for (byte b : byteArray) {
                System.out.println(b);
            }
        }

        byte[] result = new byte[byteArray.size()];
        for(int i = 0; i < byteArray.size(); i++) {
            result[i] = byteArray.get(i);
        }

        return result;
    }

    public void setKey(String key) {
        this.key = key;
        this.baseKey = key;
    }

    public String getKey() {
        return key;
    }

    public static String decodeUTF8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] encodeUTF8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public void saveKeyBytesToFile(File file) throws IOException {
        if (file.createNewFile())
            System.out.println("File created: " + file.getName());
        else System.out.println("File already exists, this will erase old key...");


        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            for (byte b : key.getBytes())
                sb.append("" + b + ",");
            out.write(sb.substring(0, sb.length()-1));
            System.out.println("Successfully saved key bytes to " + file.getName());
        } catch (IOException e) {
            System.out.println("Unable to save key");
            e.printStackTrace();
        }
    }

    public void saveKeyBytesToFileWithPwd(File file, String pwd) throws IOException {
        Crypto c = new Crypto(pwd);
        saveKeyBytesToFile(file);
        c.crypt(file);
    }

    public void loadKeyBytesToFile(File file){
        StringBuilder sb = new StringBuilder();
        try {
            List<String> list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : list)
                sb.append(line);
            String[] strBytes = sb.toString().split(",");
            byte[] bytes = new byte[strBytes.length];
            for (int i = 0; i < strBytes.length; i++) {
                bytes[i] = Byte.parseByte(strBytes[i]);
            }
            this.key = decodeUTF8(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKeyBytesToFileWithPwd(File file, String pwd) throws IOException {
        Crypto c = new Crypto(pwd);
        c.decrypt(file);
        loadKeyBytesToFile(file);
        c.crypt(file);
    }

    private void decoder(byte[] keyBytes, byte[] bytes, ArrayList<Byte> byteArray, int state) {
        for (byte b : bytes) {
            if(state >= keyBytes.length)
                state = 0;
            if(this.print)
                System.out.println(b);
            byte oof = (byte) (b - keyBytes[state]);
            byteArray.add(oof);
            state++;
        }
    }

    private void coder(byte[] keyBytes, byte[] bytes, ArrayList<Byte> byteArray, int state) {
        for (byte b : bytes) {
            if(state >= keyBytes.length)
                state = 0;
            if(this.print)
                System.out.println(b);
            byte oof = (byte) (b + keyBytes[state]);
            byteArray.add(oof);
            state++;
        }
    }

    public String seedKey(int length){
        return seedKeyWithCustomChars(length, seedChars);
    }

    public String seedKeyWithCustomChars(int length, String chars){
        long seed = 0;
        for (byte b : this.getKeyBytes())
            seed += b;
        Random random = new Random(seed);

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i<= this.getKeyBytes().length * length; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        this.key = sb.toString();
        System.out.println("Key has been seed: " + this.key);
        return sb.toString();
    }

    public void unseedKey(){
        this.key = this.baseKey;
    }
}
