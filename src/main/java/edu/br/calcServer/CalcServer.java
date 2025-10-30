package edu.br.calcServer;

import edu.br.calcServer.ClientHandler;
import edu.br.global.security.Cipherer;
import edu.br.global.security.entity.CipheredMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcServer {

    private int port;
    private ServerSocket serverSocket;
    private boolean connected = true;

    public CalcServer(int port) {
        try {
            this.port = port;
            serverSocket = new ServerSocket(port);

            postServices();

            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void start() {
        try {
            ExecutorService executor = Executors.newCachedThreadPool();

            while (connected) {
                Socket socket = serverSocket.accept();

                executor.submit(new ClientHandler(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void postServices() {
        System.out.println("Calculation Server is running...");

        try {
            //post soma subtracao multiplicacao divisao IP:port]
            String command = "post soma subtracao multiplicacao divisao " +
                    Inet4Address.getLocalHost().toString().substring(16) + ":" + port;

            Socket socket = new Socket(Inet4Address.getLocalHost(), 8081);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            CipheredMessage cm = new Cipherer("server").cifrar(command);
            cm.setName("server");

            out.writeObject(cm);
            out.flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new CalcServer(8083);
    }
}
