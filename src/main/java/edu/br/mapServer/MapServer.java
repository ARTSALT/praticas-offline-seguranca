package edu.br.mapServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapServer {
    private static int port = 8081;
    private static boolean connection = true;

    private static int robin = 0;

    public static ConcurrentHashMap<String, CopyOnWriteArrayList<String>> serviceRegistry = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        serviceRegistry.put("soma", new CopyOnWriteArrayList<>());
        serviceRegistry.put("subtracao", new CopyOnWriteArrayList<>());
        serviceRegistry.put("multiplicacao", new CopyOnWriteArrayList<>());
        serviceRegistry.put("divisao", new CopyOnWriteArrayList<>());

        System.out.println("Map Server is running...");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executor = Executors.newCachedThreadPool();

            while (connection) {
                Socket socket = serverSocket.accept();

                executor.submit(new ClientHandler(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static int roundRobin() {
        robin++;
        return robin % 2;
    }
}
