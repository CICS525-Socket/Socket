/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sockets;

import java.net.Socket;

/**
 *
 * @author Ali
 */
public class ConnectionManager implements Runnable{
    
    private Socket socket;
    
    public ConnectionManager(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
       
    }
    
    
}
