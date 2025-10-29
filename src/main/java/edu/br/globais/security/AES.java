package edu.br.globais.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    private final SecretKey chave;
    private IvParameterSpec vi;

    public AES(SecretKey chave){
        this.chave = chave;
    }

    public AES(SecretKey chave, byte[] ivBytes) {
        this.chave = chave;
        this.vi = new IvParameterSpec(ivBytes);
    }

    public static IvParameterSpec gerarVI() {
        byte[] vi = new byte[16];
        new SecureRandom().nextBytes(vi);
        return new IvParameterSpec(vi);

    }

    public byte[] cifrar(String textoAberto) throws Exception
    {
        byte[] bytesMensagemCifrada;
        Cipher cifrador;

        // Encriptar mensagem
        cifrador = Cipher.getInstance("AES/CBC/PKCS5Padding");
        vi = gerarVI();
        cifrador.init(Cipher.ENCRYPT_MODE, chave, vi);
        bytesMensagemCifrada =
                cifrador.doFinal(textoAberto.getBytes());

        return bytesMensagemCifrada;
    }

    public byte[] decifrar(byte[] textoCifrado) throws Exception
    {
        Cipher decriptador;

        decriptador = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decriptador.init(Cipher.DECRYPT_MODE, chave, vi);

        return decriptador.doFinal(textoCifrado);
    }

    public byte[] getVi() {
        return vi.getIV();
    }
}
