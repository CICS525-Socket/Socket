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
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Ali
 */
public class ConnectionManager implements Runnable {

	private final Socket socket;
	private Scanner inStream;
	private OutputStream outStream;
	private PrintWriter out;

	// changed here //
	ArrayList<Stock> stocks; // all the stocks
	ArrayList<User> users; // all the users
	ArrayList<UserStocks> userStocks; // all the users stocks
	User user; // user using this client
	// changed ended here

	public ConnectionManager(Socket socket) throws IOException {
		this.socket = socket;
		inStream = new Scanner(socket.getInputStream());
		outStream = socket.getOutputStream();
		out = new PrintWriter(outStream);

		// changed here //

		String username = this.readInputStream();
		System.out.println("Handling user " + username);
		userStocks = DataReader.getUserStocks();
		users = DataReader.getUsers();

		user = Writer.addUser(username, users);
		if (!users.contains(user)) {
			users.add(user);
		}
		System.out.println("The size of the users list is " + users.size()
				+ " " + users.get(0).getUsername());
		user = DataReader.getUserByUsername(username, users);
		stocks = DataReader.getStocks();
		// changed ended here
	}

	public void run() {
		System.out.println("run method running.");
		String userCommand = this.readInputStream();
		System.out.println("user command: " + userCommand);
		String[] command = commandParser(userCommand.toLowerCase().trim());
		this.sendToUser(command[0]);

		while (!command[0].equals("close")) {
			userCommand = this.readInputStream();
			System.out.println("user command: " + userCommand);
			command = commandParser(userCommand.toLowerCase().trim());
			this.sendToUser(command[0]);
			switch (command[0]) {
			case "close":
				this.closeConnection();
				break;
			case "query":
				this.query();
				break;
			case "checkportfolio":
				this.checkportfolio();
				break;
			case "buy":
				this.buy();
				break;
			case "sell":
				this.sell();
				break;
			case "followstock":
				this.follow();
				break;
			default:
				this.unknowCommand();
				break;
			}
		}
		this.closeConnection();
	}

	private String readInputStream() {
		if (inStream.hasNextLine()) {
			String line = inStream.nextLine();
			return line;
		}
		return null;
	}

	private void sendToUser(String userCommand) {
		out.print(userCommand + "\n");
		out.print("end\n");
		out.flush();
	}

	private void closeConnection() {
		try {
			inStream.close();
			outStream.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static String[] commandParser(String command) {
		return command.split("\\s+");
	}

	private void query() {

	}
	
	private void follow() {
		//System.out.println();
		String tickername = readInputStream();
		Writer.addStock(tickername, stocks);
	}

	private void checkportfolio() {
		System.out.println("Your current balance is $" + user.getBalance());
		ArrayList<UserStocks> portfolio = user.getUserStock();
		if (portfolio != null) {
			System.out.println("Your portfolio is as follows :");
			System.out
					.println("TickerName \t Full Name \t Current Price \t Quantity");
			for (UserStocks e : portfolio) {
				System.out.println(e.getTickername() + " \t "
						+ PriceUpdater.name(e.getTickername()) + " \t "
						+ PriceUpdater.price(e.getTickername()) + " \t "
						+ e.getNo());
			}
		} else {
			System.out.println("You do not have any stocks yet");
		}
	}

	private void buy() {

	}

	private void sell() {

	}

	private void unknowCommand() {

	}

}
