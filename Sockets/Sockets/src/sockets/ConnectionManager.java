/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sockets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Ali
 */
public class ConnectionManager implements Runnable{
    
    private Socket socket;
    private Scanner inStream;
    private OutputStream outStream;
    
    
    public ConnectionManager(Socket socket) throws IOException{
        this.socket = socket;
        inStream = new Scanner(socket.getInputStream());
        outStream = socket.getOutputStream();
    }

    @Override
    public void run() {
       System.out.println("run method running.");
    }
    
    
}
