/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ali
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int port = 8003;
        try {
            //Starting the server
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server Started");
            while (true) {
                System.out.println("Waiting for a client");
                //creating one socket for each client
                Socket socket = server.accept();
                System.out.println("Contacting end-system ip address: "
                        + socket.getInetAddress().toString());
                System.out.println("Contacting process port number: "
                        + socket.getPort() + "\n");
                //ConnectionManager class is in charge of receiving the requests and handling them
                Thread t = new Thread(new ConnectionManager(socket));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IO Exception :" + e.getMessage());
        }
    }

}