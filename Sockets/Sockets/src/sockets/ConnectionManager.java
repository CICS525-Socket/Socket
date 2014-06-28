/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sockets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Ali
 */
public class ConnectionManager implements Runnable{
    
    private final Socket socket;
    private Scanner inStream;
    private OutputStream outStream;
    private PrintWriter out;
    
    public ConnectionManager(Socket socket) throws IOException{
        this.socket = socket;
        inStream = new Scanner(socket.getInputStream());
        outStream = socket.getOutputStream();
        out = new PrintWriter(outStream);
    }

    public void run() {
       System.out.println("run method running.");
       String userCommand = this.readInputStream();
       System.out.println("user command: " + userCommand);
       this.sendToUser(userCommand);
       this.closeConnection();
    }

    private String readInputStream() {
        if(inStream.hasNextLine()) {
            String line = inStream.nextLine();
            return line;
        }
        return null;
    }

    private void sendToUser(String userCommand) {
        out.print(userCommand);
        out.flush();
    }

    private void closeConnection() {
        try{
        inStream.close();
        outStream.close();
        out.close();
        socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
}
