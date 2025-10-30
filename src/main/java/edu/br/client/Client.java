package edu.br.client;

import edu.br.global.security.Cipherer;
import edu.br.global.security.entity.CipheredMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    private String name;
    private String discoveryIp;
    private int discoveryPort;

    private Cipherer cipherer;

    HashMap<String, String> discoveredServices;

    public Client(String name, String ip, int port) {
        this.name = name;
        this.discoveryIp = ip;
        this.discoveryPort = port;

        cipherer = new Cipherer(name);
        discoveredServices = new HashMap<>();

        System.out.println("Client " + name + " initialized.");
    }

    public String executarCalculo(String operation, int a, int b) {
        String ip = discoveredServices.get(operation);

        if (ip == null) {
            ip = discoverService(operation);
        }

        String command = operation + " " + a + " " + b;
        return processCommand(ip, command);
    }

    public String discoverService(String operation) {
        try {
            Socket socket = new Socket(discoveryIp, discoveryPort);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            CipheredMessage cm = cipherer.cifrar("dc " + operation);
            cm.setName(name);

            out.writeObject(cm);
            out.flush();

            CipheredMessage response = (CipheredMessage) in.readObject();
            if (cipherer.autenticarHMAC(response.getIv(), response.getCipheredText(), response.getTag())) {
                String serverIp = cipherer.decifrar(response);
                if (!serverIp.contains("Invalid Request")) {
                    //System.out.println("Discovered service for command " + operation + " at " + serverIp);
                    discoveredServices.put(operation, serverIp);

                    in.close();
                    out.close();
                    socket.close();

                    return serverIp;
                } else {
                    System.out.println("Service discovery request was invalid.");
                    return null;
                }
            } else {
                System.out.println("HMAC authentication failed for service discovery response.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String processCommand(String serverIp, String command) {
        try {
            String[] parts = serverIp.split(":");

            Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            CipheredMessage cm = cipherer.cifrar(command);
            cm.setName(name);

            out.writeObject(cm);
            out.flush();

            CipheredMessage response = (CipheredMessage) in.readObject();
            if (cipherer.autenticarHMAC(response.getIv(), response.getCipheredText(), response.getTag())) {
                String result = cipherer.decifrar(response);

                in.close();
                out.close();
                socket.close();

                return result;
            } else {
                System.out.println("HMAC authentication failed for command response.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("client1", "192.168.0.3", 8081);

        Scanner sc = new Scanner(System.in);

        System.out.println("Bem vindo à calculadora distribuída!");
        System.out.println("Operações disponíveis: soma, subtracao, multiplicacao, divisao");
        System.out.println("Exemplo de comando: soma 5 10");
        System.out.println("---------------------------------------");
        while (true) {
            System.out.print("> ");
            String command = sc.nextLine();

            String[] parts = command.split(" ");

            if (parts.length != 3) {
                System.out.println("Comando inválido. Use o formato: operação número1 número2");
            } else if (parts[0].equals("divisao") && parts[2].equals("0")) {
                System.out.println("Erro: Divisão por zero não é permitida.");
            } else {
                System.out.println(client.executarCalculo(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            }
        }
    }
}
