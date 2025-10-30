package edu.br.globais.security;

import edu.br.globais.KeyManager;
import edu.br.globais.security.entity.CipheredMessage;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cipherer {
    private String keyHMAC;
    private SecretKey keyAES;
    private KeyManager km;

    public Cipherer(String code) {
        km = new KeyManager();
        keyAES = KeyManager.getKeyAES(code);
        System.out.println(keyAES);
        keyHMAC = KeyManager.getKeyHMAC(code);
        System.out.println(keyHMAC);
    }

    public CipheredMessage cifrar(String mensagem) throws Exception {
        AES aes = new AES(keyAES);
        byte[] textoCifrado = aes.cifrar(mensagem);

        byte[] tagHMAC = gerarTagHMAC(aes.getVi(),  textoCifrado);

        return new CipheredMessage(
                textoCifrado,
                aes.getVi(),
                tagHMAC
        );
    }

    public String decifrar(CipheredMessage cipheredMessage) throws Exception {
        AES aes = new AES(keyAES, cipheredMessage.getIv());
        byte[] textoDecifrado = aes.decifrar(cipheredMessage.getCipheredText());

        return new String(textoDecifrado);
    }

    public byte[] gerarTagHMAC(byte[] vi, byte[] cipheredText) throws Exception {
        byte[] tagMkr = new byte[vi.length + cipheredText.length];
        System.arraycopy(vi, 0, tagMkr, 0, vi.length);
        System.arraycopy(cipheredText, 0, tagMkr, vi.length, cipheredText.length);
        return HMAC.hMac(keyHMAC, tagMkr);
    }

    public boolean autenticarHMAC(byte[] vi, byte[] cipheredText, byte[] tagHMAC) throws Exception {
        byte[] textoCifrado = cipheredText;
        byte[] tagMkr = new byte[vi.length + textoCifrado.length];
        System.arraycopy(vi, 0, tagMkr, 0, vi.length);
        System.arraycopy(textoCifrado, 0, tagMkr, vi.length, textoCifrado.length);
        byte[] tagHMACCalculada = HMAC.hMac(keyHMAC, tagMkr);

        return MessageDigest.isEqual(tagHMAC, tagHMACCalculada);
    }

    private static SecretKey gerarChave(int t, String alg) throws NoSuchAlgorithmException {
        KeyGenerator geradorDeChaves = KeyGenerator.getInstance(alg);
        geradorDeChaves.init(t);
        return geradorDeChaves.generateKey();
    }

    public String getKeyHMAC() {
        return keyHMAC;
    }

    public SecretKey getKeyAES() {
        return keyAES;
    }
}