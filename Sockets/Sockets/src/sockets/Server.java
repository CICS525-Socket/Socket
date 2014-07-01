package sockets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		final int port = 8003;
		
		//create the storage files if they do not exist		
		File userstocks = new File("userstocks.txt");
		if(!userstocks.exists()) {
		    try {
				userstocks.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create userstocks storage file");
			}
		} 	
		
		File users = new File("users.txt");
		if(!users.exists()) {
		    try {
				users.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create users storage file");
			}
		} 	

		File stocks = new File("stocks.txt");
		if(!stocks.exists()) {
		    try {
				stocks.createNewFile(); 
			} catch (IOException e) {
				System.out.println("Could not create userstocks storage file");
			}
		} 	
		// start the price updater
		Thread t2 = new Thread(new PriceUpdater());
		t2.start();

		try {
			// Starting the server
			ServerSocket server = new ServerSocket(port);
			System.out.println("Server Started");
			while (true) {
				System.out.println("Waiting for a client");
				// creating one socket for each client
				Socket socket = server.accept();
				System.out.println("Contacting end-system ip address: "
						+ socket.getInetAddress().toString());
				System.out.println("Contacting process port number: "
						+ socket.getPort() + "\n");
				// ConnectionManager class is in charge of receiving the
				// requests and handling them
				Thread t = new Thread(new ConnectionManager(socket));
				t.start();
			}
		} catch (IOException e) {
			System.out.println("IO Exception :" + e.getMessage());
		}
	}

}
