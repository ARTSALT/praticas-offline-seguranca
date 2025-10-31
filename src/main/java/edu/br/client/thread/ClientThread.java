package edu.br.client.thread;

import edu.br.global.security.Cipherer;
import edu.br.global.security.entity.CipheredMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ClientThread implements Runnable {

    private String name;
    private String nextIp;
    private int nextPort;
    private Cipherer cipherer;

    public ClientThread(String name, String nextIp, int nextPort, Cipherer cipherer) {
        this.name = name;
        this.nextIp = nextIp;
        this.nextPort = nextPort;
        this.cipherer = cipherer;
    }

    @Override
    public void run() {
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

        try {
            ObjectOutputStream out = new ObjectOutputStream(nextSocket.getOutputStream());
            Scanner in = new Scanner(System.in);

            System.out.println("Qual arquivo deseja adquirir?");
            System.out.println("Utilize o padrão: file-X, onde X é o número do arquivo que deseja adquirir");
            while (true) {
                System.out.print("> ");
                String command = in.nextLine();

                try {
                    if (!command.startsWith("file-")) {
                        System.out.println("Erro na entrada de dados. tente outra vez!");
                    } else if (!Pattern.matches("([1-9]|[1-5][0-9]|60|)", command.split("-")[1])) {
                        System.out.println("Erro na entrada de dados. tente outra vez!");
                    } else {
                        command = name + " search " + command;
                        CipheredMessage cm = cipherer.cifrar(command);
                        cm.setName(name);

                        out.writeObject(cm);
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
