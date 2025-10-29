package edu.br.mapServer.entities.thread;

import edu.br.globais.security.Cipherer;
import edu.br.globais.security.HMAC;
import edu.br.globais.security.entity.CipheredMessage;
import edu.br.mapServer.MapServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
    private Boolean connection = true;

    public Cipherer cipherer;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    public final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        MapServer.requisitantes.add(this);
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            while (connection) {
                if (clientSocket.isClosed()) {
                    connection = false;
                    System.out.println("Conexão encerrada com o cliente: " + clientSocket.getInetAddress());
                    MapServer.requisitantes.remove(this);
                    break;
                } else {
                    System.out.println("Aguardando mensagens do cliente: " + clientSocket.getInetAddress());

                    // Lê a mensagem cifrada do cliente
                    Object receivedObject = in.readObject();
                    CipheredMessage cipheredMessage = (CipheredMessage) receivedObject;
                    cipherer = new Cipherer(cipheredMessage.getName());

                    if (analisarMensagem(cipheredMessage, cipherer.getKeyHMAC())) {
                        System.out.println("Mensagem autenticada com sucesso.");
                        System.out.println("Tag HMAC válida.");

                        // Decifra a mensagem
                        String message = cipherer.decifrar(cipheredMessage);

                        System.out.println("Mensagem recebida: " + message);

                        // Processa a mensagem recebida
                        handleMessage(message, cipherer);
                    } else {
                        System.out.println("Falha na autenticação da mensagem recebida.");
                        System.out.println("Tag HMAC inválida.");
                        System.out.println("Mensagem descartada.");
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private boolean analisarMensagem(CipheredMessage cipheredMessage, String keyHMAC) throws Exception {
        // Autenticar a tag HMAC
        byte[] vi = cipheredMessage.getIv();
        byte[] textoCifrado = cipheredMessage.getCipheredText();
        byte[] tagMkr = new byte[vi.length + textoCifrado.length];
        System.arraycopy(vi, 0, tagMkr, 0, vi.length);
        System.arraycopy(textoCifrado, 0, tagMkr, vi.length, textoCifrado.length);
        byte[] tagHMACCalculada = HMAC.hMac(keyHMAC, tagMkr);

        return (MessageDigest.isEqual(tagHMACCalculada, cipheredMessage.getTag()));
    }

    public void handleMessage(String message, Cipherer cipherer) throws Exception {
        if (Pattern.matches("client[0-9]", message) && message.length() == 7) {
            // Cliente
            char index = message.charAt(message.length() - 1);
            String serverIp = MapServer.dnsMap.get("server" + index);

            try {
                CipheredMessage cm = cipherer.cifrar(serverIp);
                cm.setName("Server");

                out.writeObject(cm);
                out.flush();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (message.contains("registrador")) {
            // Registrador
            String[] parts = message.split(" ");
            String cmd = parts[0].substring(0, parts[0].length() - 2); // "registrador"
            String server = parts[1]; // "serverX"
            String ip = parts[2]; // "192.168.0.x"

            if (Pattern.matches("server[0-9]", server)) {
                String oldIp = MapServer.dnsMap.get(server);
                MapServer.dnsMap.put(server, ip);

                String pushMessage = "Mapeamento atualizado com sucesso.\n" +
                        server + " : " + oldIp + " -> " + server + " : " + ip;

                System.out.println(pushMessage);
                MapServer.broadcastUpdate(pushMessage);
            } else {
                String error = "Comando de registro inválido: " + message;
                System.out.println(error);
                CipheredMessage cm = cipherer.cifrar(error);
                cm.setName("Server Error");
                out.writeObject(error);
                out.flush();
            }
        } else {
            String error = "Mensagem em formato inválido";
            System.out.println(error);
            CipheredMessage cm = cipherer.cifrar(error);
            cm.setName("Server Error");
            out.writeObject(cm);
            out.flush();
        }
    }
}
