package edu.br.client;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private String name;
    private String ip;
    private int port;

    public Client(String name, String ip, int port) throws IOException {
        this.name = name;
        this.ip = ip;
        this.port = port;

        Socket socket = new Socket(ip, port);



    }
}
