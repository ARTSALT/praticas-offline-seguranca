package edu.br.globais.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {
    public static final String ALG = "HmacSHA256";

    public static byte[] hMac(String chave, byte[] mensagem) throws Exception {

        Mac shaHMAC = Mac.getInstance(ALG);
        SecretKeySpec chaveMAC = new SecretKeySpec(chave.getBytes("UTF-8"), ALG);

        shaHMAC.init(chaveMAC);
        byte[] bytesHMAC = shaHMAC.doFinal(mensagem);

        return bytesHMAC;
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
