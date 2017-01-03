package my.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class DigestHelper {

    public static void main(String[] args)throws Exception {
        System.out.print("Enter input string: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        System.out.println("Digest:" + digest(input));
    }


    public static String digest(final String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] mdbytes = md.digest(input.getBytes("UTF-8"));

        //convert the byte to hex format
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        return hexString.toString();
    }
}
