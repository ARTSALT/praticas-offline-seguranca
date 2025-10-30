package edu.br.mapServer;

import edu.br.globais.security.Cipherer;
import edu.br.globais.security.entity.CipheredMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            CipheredMessage cm = (CipheredMessage) in.readObject();
            Cipherer cipherer = new Cipherer(cm.getName());

            if (cipherer.autenticarHMAC(cm.getIv(), cm.getCipheredText(), cm.getTag())) {
                String mensagemDecifrada = cipherer.decifrar(cm);
                System.out.println("Mensagem decifrada: " + mensagemDecifrada);

                // Tratamento da mensagem recebida do cliente
                if (mensagemDecifrada.contains("dc ")) {
                    String[] parts = mensagemDecifrada.split(" ");

                    if (parts.length == 2) {
                        String serviceName = parts[1];

                        if (MapServer.serviceRegistry.containsKey(serviceName)) {
                            String serverIp = MapServer.serviceRegistry.get(serviceName).get(MapServer.roundRobin());
                            System.out.println("Serviço requisitado: " + serviceName);
                            System.out.println("Servidor descoberto: " + serverIp);

                            CipheredMessage cr = cipherer.cifrar(serverIp);
                            cr.setName("ServerDiscoveryResponse");

                            out.writeObject(cr);
                            out.flush();

                            in.close();
                            out.close();
                            clientSocket.close();
                        } else {
                            System.out.println("Serviço não encontrado para descoberta: " + serviceName);
                        }
                    } else {
                        System.out.println("Formato inválido para descoberta.");
                    }
                }
            } else {
                System.out.println("Falha na autenticação HMAC.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
