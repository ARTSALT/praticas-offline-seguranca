package edu.br.client.entities.thread;

import edu.br.globais.security.Cipherer;
import edu.br.globais.security.HMAC;
import edu.br.globais.security.entity.CipheredMessage;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class InputHandler implements Runnable{

    private Socket socket;
    private Boolean connection = true;
    private Cipherer cipherer;

    private String keyHMAC;

    public static ObjectInputStream in;

    public InputHandler(Socket socket, Cipherer cipherer) {
        this.socket = socket;
        this.cipherer = cipherer;

        this.keyHMAC = cipherer.getKeyHMAC();
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());

            // Leitura contínua
            while (connection) {
                if (socket.isClosed()) {
                    connection = false;
                    in.close();
                    System.out.println("Conexão encerrada.");

                    break;
                } else {
                    System.out.println("Aguardando mensagens...");

                    Object receivedObject = in.readObject();
                    CipheredMessage cipheredMessage = (CipheredMessage) receivedObject;

                    // Autenticar a tag HMAC
                    byte[] vi = cipheredMessage.getIv();
                    byte[] textoCifrado = cipheredMessage.getCipheredText();
                    byte[] tagMkr = new byte[vi.length + textoCifrado.length];
                    System.arraycopy(vi, 0, tagMkr, 0, vi.length);
                    System.arraycopy(textoCifrado, 0, tagMkr, vi.length, textoCifrado.length);
                    byte[] tagHMACCalculada = HMAC.hMac(keyHMAC, tagMkr);

                    if (MessageDigest.isEqual(tagHMACCalculada, cipheredMessage.getTag())) {

                        String message = cipherer.decifrar(cipheredMessage);

                        System.out.println(cipheredMessage.getName() + ": " + message);
                    } else {
                        System.out.println("Falha na autenticação da mensagem recebida.");
                        System.out.println("Tag HMAC inválida.");
                        System.out.println("Mensagem descartada.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
