package edu.br.client.entities.thread;

import edu.br.globais.security.Cipherer;
import edu.br.globais.security.entity.CipheredMessage;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class OutputHandler implements Runnable{

    private final Socket socket;
    private String name;
    private Boolean connection = true;

    private final Cipherer cipherer;

    public OutputHandler(Socket socket, Cipherer cipherer, String name) {
        this.socket = socket;
        this.cipherer = cipherer;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.println("Pronto para enviar mensagens, aguardando Input...");
            System.out.println("Por favor, solicite um servidor para se conectar, digite client1, client2, ...");
            System.out.println("Alternativamente, escreva: 'registrador: server[0-9] 192.168.0.x' para alterar o mapeamento DNS local.");
            System.out.println("Ou digite 'fim' para encerrar a conexão.");

            // Escrita contínua
            while (connection) {
                System.out.print("> ");
                String mensagem = scanner.nextLine();

                if (mensagem.equalsIgnoreCase("fim")) {
                    connection = false;
                    socket.close();
                    System.out.println("Conexão encerrada.");
                    break;
                } else {
                    // Cifrar a mensagem (coletas - chaves, vi, texto cifrado e tag HMAC)
                    CipheredMessage cipheredMessage = cipherer.cifrar(mensagem);
                    cipheredMessage.setName(name);

                    // Enviar a mensagem cifrada
                    outputStream.writeObject(cipheredMessage);
                    outputStream.flush();
                    System.out.println("Mensagem enviada com sucesso.");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
