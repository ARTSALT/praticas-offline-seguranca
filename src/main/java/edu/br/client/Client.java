package edu.br.client;

import edu.br.client.thread.ClientThread;
import edu.br.client.thread.ServerThread;
import edu.br.global.security.Cipherer;

import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public String name;
    private String nextIp;
    private int port;
    private int nextPort;

    private Cipherer cipherer;

    public CopyOnWriteArrayList<String> files = new CopyOnWriteArrayList<>();

    public Client(String name, String ip, int port) {
        this.name = name;
        this.nextIp = ip;

        int userIndex = Integer.parseInt(name.substring(1));
        this.port = port + userIndex;

        if (!name.equals("P5")) {
            this.nextPort = port +  userIndex + 1;
        } else if (name.equals("P7")) {

        } else {
            this.nextPort = port;
        }

        cipherer = new Cipherer(name);

        int x = name.charAt(name.length() - 1); // P0 - 0
        for (int i = 0; i < 10; i++) {
            files.add("file-" + (i + 1 + (x * 10)));
        }

        System.out.println("Client " + name + " initialized.");
        start();
    }

    private void start() {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(new ServerThread(name, port, nextIp, nextPort, files, cipherer));
            executor.submit(new ClientThread(name, nextIp, nextPort, cipherer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("P5", "192.168.0.3", 8081);
    }
}
