package edu.br.mapServer;

import edu.br.globais.security.entity.CipheredMessage;
import edu.br.mapServer.entities.thread.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapServer {
    static int port = 8081;

    public static ConcurrentHashMap<String, String> dnsMap = new ConcurrentHashMap<>();
    public static List<ClientHandler> requisitantes = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try {
            // Inicializa o mapa DNS com alguns valores padrão
            for (int i = 0; i < 9; i++) {
                dnsMap.put("server" + (i + 1), "192.168.0." + ((i+1) * 10));
            }

            ServerSocket serverSocket = new ServerSocket(port);

            ExecutorService clientTask = Executors.newCachedThreadPool();
            System.out.println("Servidor iniciado na porta " + port);
            System.out.println("Aguardando conexões de clientes...");

            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + socket.getInetAddress());

                // Cria uma nova thread para lidar com o cliente
                clientTask.submit(new ClientHandler(socket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void broadcastUpdate(String pushMessage) {
        // Itera de forma segura na lista de requisitantes
        try {
            for (ClientHandler handler : MapServer.requisitantes) {
                CipheredMessage cm = handler.cipherer.cifrar(pushMessage);
                cm.setName("Server");

                handler.out.writeObject(cm);
                handler.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
