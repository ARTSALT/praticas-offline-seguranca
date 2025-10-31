package edu.br.client.thread;

import edu.br.global.security.Cipherer;
import edu.br.global.security.entity.CipheredMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable{

    private String name;
    private Socket socket;
    private Socket nextSocket;
    private Cipherer cipherer;
    private CopyOnWriteArrayList<String> files;

    private ObjectOutputStream nextOut;

    public ClientHandler(String name, Socket socket, Socket nextSocket, ObjectOutputStream nextOut, CopyOnWriteArrayList<String> files, Cipherer cipherer) {
        this.name = name;
        this.socket = socket;
        this.nextSocket = nextSocket;
        this.nextOut = nextOut;
        this.cipherer = cipherer;
        this.files = files;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String clientName = "";
            switch(name) {
                case "P0":
                    clientName = "P5";
                    break;
                case "P1":
                    clientName = "P0";
                    break;
                case "P2":
                    clientName = "P1";
                    break;
                case "P3":
                    clientName = "P2";
                    break;
                case "P4":
                    clientName = "P3";
                    break;
                case "P5":
                    clientName = "P4";
            }

            System.out.println("Conectado ao cliente" + clientName);
            while (true) {
                CipheredMessage cm = (CipheredMessage) in.readObject();
                Cipherer clientCipherer = new Cipherer(cm.getName());

                if (clientCipherer.autenticarHMAC(cm.getIv(), cm.getCipheredText(), cm.getTag())) {
                    String message = clientCipherer.decifrar(cm);
                    //PX search file-Y
                    System.out.println("Mensagem recebida: " + message);

                    String[] parts = message.split(" ");

                    if (parts[0].equals(name) && parts[1].equals("found")) {
                        System.out.println("Arquivo requisitado recebido");
                        continue;
                    } else if (parts[1].equals("found")) {
                        System.out.println("Arquivo já encontrado. Enviado a diante");
                    } else if (files.contains(parts[2])) {
                        System.out.println("Arquivo " + parts[2] + " encontrado. Adiante na rede");
                        message = parts[0] + "found " + parts[2];
                    } else {
                        System.out.println("Arquivo " + parts[2] + " não encontrado. Encaminhando para o próximo cliente.");
                    }

                    CipheredMessage cr = cipherer.cifrar(message);
                    cr.setName(name);

                    synchronized (nextOut) {
                        nextOut.reset();
                        nextOut.writeObject(cr);
                        nextOut.flush();
                    }

                } else {
                    System.out.println("Falha na autenticação HMAC. Mensagem descartada.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
