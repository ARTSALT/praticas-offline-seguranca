package edu.br.client;

import edu.br.client.entities.thread.InputHandler;
import edu.br.client.entities.thread.OutputHandler;
import edu.br.globais.security.Cipherer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public Client(String num_cliente, String ip, int port) {
        String name;
        try {
            int i = (Integer.parseInt(num_cliente));
            name = "client" + i;
        } catch (NumberFormatException e) {
            name = "registrador";
        }

        Cipherer cipherer = new Cipherer(name);

        try {
            Socket socket = new Socket(ip, port);

            ExecutorService clientTask = Executors.newFixedThreadPool(2);
            clientTask.submit(new InputHandler(socket, cipherer));
            clientTask.submit(new OutputHandler(socket, cipherer, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("1", "10.10.70.100", 8081);
    }
}

