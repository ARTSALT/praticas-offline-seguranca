package edu.br.calcServer;

import edu.br.global.security.Cipherer;
import edu.br.global.security.entity.CipheredMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            CipheredMessage cm = (CipheredMessage) in.readObject();
            Cipherer cipherer = new Cipherer(cm.getName());

            if (cipherer.autenticarHMAC(cm.getIv(), cm.getCipheredText(), cm.getTag())) {
                String mensagemDecifrada = cipherer.decifrar(cm);
                System.out.println("Mensagem decifrada: " + mensagemDecifrada);

                // Tratamento da mensagem recebida do cliente
                String[] parts = mensagemDecifrada.split(" ");
                int result;
                String response;
                CipheredMessage cr;
                switch (parts[0]) {
                    case "soma":
                        result = Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]);
                        response = "Resultado da soma: " + result;
                        System.out.println(response);
                        cr = cipherer.cifrar(response);
                        cr.setName("CalcResponse");
                        
                        out.writeObject(cr);
                        out.flush();
                        
                        break;
                    case  "subtracao":
                        result = Integer.parseInt(parts[1]) - Integer.parseInt(parts[2]);
                        response = "Resultado da subtração: " + result;
                        System.out.println(response);
                        cr = cipherer.cifrar(response);
                        cr.setName("CalcResponse");

                        out.writeObject(cr);
                        out.flush();
                        
                        break;
                    case "multiplicacao":
                        result = Integer.parseInt(parts[1]) * Integer.parseInt(parts[2]);
                        response = "Resultado da multiplicação: " + result;
                        System.out.println(response);
                        cr = cipherer.cifrar(response);
                        cr.setName("CalcResponse");

                        out.writeObject(cr);
                        out.flush();

                        break;
                    case "divisao":
                        result = Integer.parseInt(parts[1]) / Integer.parseInt(parts[2]);
                        response = "Resultado da divisão: " + result;
                        System.out.println(response);
                        cr = cipherer.cifrar(response);
                        cr.setName("CalcResponse");

                        out.writeObject(cr);
                        out.flush();

                        break;
                    default:
                        System.out.println("Operação inválida: " + parts[0]);
                }
            } else {
                System.out.println("Falha na autenticação HMAC.");
            }

            out.writeObject(cipherer.cifrar("Invalid Request"));
            out.flush();
            in.close();
            out.close();
            clientSocket.close();

        } catch (Exception ignored) {

        }
    }
}
