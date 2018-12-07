import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by ningyu on 2018/7/16.
 */
public class Crypto {
    public static void main(String[] args) {
        try {
            String init = "This is a test";
            byte[] result = encryptString(init, "1234567812345678");
            String decryptedResult = decryptString(result, "1234567812345678");
            System.out.println(decryptedResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // input a String, return a encrypted byte[]
    public static byte[] encryptString(String inputString, String key) {
        try {
            String initVector = "1234567812345678";

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(inputString.getBytes());

            return encrypted;

        } catch (Exception e) {
            e.printStackTrace();
            return "".getBytes();
        }
    }

    // input a encrypted byte[], return a decrypted String
    public static String decryptString(byte[] inputString, String key) {
        try {
            String initVector = "1234567812345678";

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            StringBuilder result = new StringBuilder();
            byte[] decrypted = cipher.doFinal(inputString);
            for (byte i: decrypted) {
                result.append((char) i);
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    // inputFile and outputFile must be absolute path
    // key must be 128 bits (16 bytes)
    public static boolean encrypt(String inputFile, String outputFile, String key) {
        try {
            FileInputStream fis = new FileInputStream(new File(inputFile));
            FileOutputStream fos = new FileOutputStream(new File(outputFile));
            byte[] buffer = new byte[1024];
            int size = -1;

            // AES part
            // init vector must be 16 bytes
            String initVector = "1234567812345678";
            // key must be 128, 192, 256 bits, I use 128 bits here
            if (key.length() != 16) {
                return false;
            }

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            // write to the encrypted file
            while ((size = fis.read(buffer, 0, 1024)) != -1) {
                byte[] encrypted = cipher.doFinal(buffer);
                fos.write(encrypted, 0, encrypted.length);
            }
            fis.close();
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decrypt(String inputFile, String outputFile, String key) {
        try {
            FileInputStream fis = new FileInputStream(new File(inputFile));
            FileOutputStream fos = new FileOutputStream(new File(outputFile));
            int size = -1;

            // AES part
            // init vector must be 16 bytes
            String initVector = "1234567812345678";
            // key must be 128, 192, 256 bits, I use 128 bits here
            if (key.length() != 16) {
                return false;
            }

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            // write to the decrypted file
            byte[] decrypted_buffer = new byte[1040];
            while ((size = fis.read(decrypted_buffer, 0, 1040)) != -1) {
                byte[] decrypted = cipher.doFinal(decrypted_buffer);
                fos.write(decrypted, 0, decrypted.length);
            }
            fis.close();
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}