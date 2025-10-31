package edu.br.client.thread;

import edu.br.global.security.Cipherer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThread implements  Runnable {

    private String name;
    private String nextIp;
    private int port;
    private int nextPort;
    private Cipherer cipherer;
    private CopyOnWriteArrayList<String> files = new CopyOnWriteArrayList<String>();

    public ServerThread(String name, int port, String nextIp, int nextPort, CopyOnWriteArrayList<String> files, Cipherer cipherer) {
        this.name = name;
        this.port = port;
        this.nextIp = nextIp;
        this.nextPort = nextPort;
        this.cipherer = cipherer;
        this.files = files;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Socket nextSocket = null;

            while (nextSocket == null) {
                try {
                    nextSocket = new Socket(nextIp, nextPort);

                } catch (IOException e) {
                    try {
                        Thread.sleep(5000); // Espera 5 segundos antes de tentar de novo
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            ObjectOutputStream nextOut = new ObjectOutputStream(nextSocket.getOutputStream());

            System.out.println("Servidor " + name + " Iniciado e conectando na rede P2P");
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new ClientHandler(name, socket, nextSocket, nextOut, files, cipherer));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
