package edu.br;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String a = "192.168.0.0";
        if (Pattern.matches("192\\.168\\.0\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])", a)) {
            System.out.println("ok");
        } else {
            System.out.println("nok");
        }
    }

    private static SecretKey gerarChave(int t, String alg) throws NoSuchAlgorithmException {
        KeyGenerator geradorDeChaves = KeyGenerator.getInstance(alg);
        geradorDeChaves.init(t);
        return geradorDeChaves.generateKey();
    }

    public static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] hex2Byte(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}