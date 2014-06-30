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
import java.util.Collection;
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
	private ArrayList<Stock> stocks; // all the stocks
	private ArrayList<User> users; // all the users
	private ArrayList<UserStocks> userStocks; // all the users stocks
	private String currentCommand = "";
	private User user; // user using this client

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
		user = DataReader.getUserByUsername(username, users);
		stocks = DataReader.getStocks();
		// changed ended here
	}

	public void run() {
		System.out.println("run method running.");
		while (true) {
			String userCommand = this.readInputStream();
			System.out.println("user command: " + userCommand);
			String[] command = commandParser(userCommand.toLowerCase().trim());
			if (!command[0].equals("close")) {
				switch (userCommand) {
				case "close":
					this.closeConnection();
					break;				
				case "checkportfolio":
					currentCommand = "checkportfolio";
					this.checkportfolio();
					break;
				case "buy":
					currentCommand = "buy";
					break;
				case "sell":
					currentCommand = "sell";
					break;
				case "follow":
					currentCommand = "follow";
					break;
				default:
					this.unknowCommand(userCommand);
					break;
				}
			} else {
				this.closeConnection();
				break;
			}
		}
	}

	private String readInputStream() {
		while (true) {
			if (inStream.hasNext()) {
				String line = inStream.nextLine();
				return line;
			} else {
				continue;
			}
		}		
	}

	private void sendToUser(String userCommand) {
		out.print(userCommand + "\n");
		out.print("end\n");
		out.flush();
	}

	private void closeConnection() {
		try {
			this.sendToUser("Closing user connection ...");
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

	private void follow(String ticker) {
		currentCommand = "follow";
		// this.sendToUser("Calling the follow function");
		String[] comps = ticker.split("\\s+");
		String tickername = comps[1].substring(1,comps[1].length()-1);
		
		System.out.println("The tickername is " + tickername);
		this.stocks = Writer.addStock(tickername, stocks);
		Stock rStock = DataReader.getStockByTickername(tickername, stocks);
		if (rStock != null) {
			this.sendToUser("Stockname: "
					+ PriceUpdater.name(rStock.getTickername())
					+ " \t Tickername: " + rStock.getTickername()
					+ "\t Price: " + rStock.getPrice() + "\t Remaining: "
					+ rStock.getNo());
		} else {
			this.sendToUser("Tickername is invalid or query invalid");
		}
	}

	private void checkportfolio() {
		String response = "Your current balance is $" + user.getBalance();
		System.out.println("Your current balance is $" + user.getBalance());
		ArrayList<UserStocks> portfolio = user.getUserStock();

		if (portfolio != null) {
			System.out.println("Your portfolio is as follows :");
			System.out
					.println("TickerName \t\t\t Full Name \t\t\t Current Price \t\t\t Quantity");
			response += "\nYour portfolio is as follows :"
					+ "\nTickerName \t\t\t Full Name \t\t\t Current Price \t\t\t Quantity";
			for (UserStocks e : portfolio) {
				System.out.println(e.getTickername() + " \t\t\t "
						+ PriceUpdater.name(e.getTickername()) + " \t\t\t "
						+ PriceUpdater.price(e.getTickername()) + " \t\t\t "
						+ e.getNo());
				response += "\n" + e.getTickername() + " \t\t\t "
						+ PriceUpdater.name(e.getTickername()) + " \t\t\t "
						+ PriceUpdater.price(e.getTickername()) + " \t\t\t "
						+ e.getNo();
			}
		} else {
			System.out.println("You do not have any stocks yet");
			response += "\nYou do not have any stocks yet";
		}
		sendToUser(response);
	}

	private void buy(String command) {
		currentCommand = "buy";
		String[] commandComps = command.split("\\s+");
		if (commandComps.length != 3) {
			sendToUser("Invalid command");
			return;
		}
		if (commandComps[0].equalsIgnoreCase("BUY") && commandComps[1].startsWith("<")
				&& commandComps[1].endsWith(">")
				&& commandComps[2].startsWith("<")
				&& commandComps[2].endsWith(">")) {
			String tickername = commandComps[1].substring(1,
					commandComps[1].length() - 1);
			int no = 0;
			try {
				no = Integer.valueOf(commandComps[2].substring(1,
						commandComps[2].length() - 1));
			} catch (Exception e) {
				sendToUser("Invalid amount entered. Please enter an integer");
				return;
			}

			Collection results = Writer.purchaseStock(tickername, user, no,
					users, stocks);
			if (results != null) {				
				userStocks = DataReader.getUserStocks();
				stocks = DataReader.getStocks();
				users = DataReader.getUsers(); // (ArrayList<User>) list.get(2);
				user = DataReader.getUserByUsername(user.getUsername(), users);				
				this.sendToUser("Purchase successful.");
			} else {
				this.sendToUser("Purchase unsuccessful.");
			}
		} else {
			this.sendToUser("Invalid command");
		}
	}

	private void sell(String command) {
		currentCommand = "sell";
		String[] commandComps = command.split("\\s+");
		if (commandComps.length != 3) {
			sendToUser("Invalid command");
			return;
		}
		if (commandComps[0].equalsIgnoreCase("SELL") && commandComps[1].startsWith("<")
				&& commandComps[1].endsWith(">")
				&& commandComps[2].startsWith("<")
				&& commandComps[2].endsWith(">")) {
			String tickername = commandComps[1].substring(1,
					commandComps[1].length() - 1);
			int no = 0;
			try {
				no = Integer.valueOf(commandComps[2].substring(1,
						commandComps[2].length() - 1));
			} catch (Exception e) {
				sendToUser("Invalid amount entered. Please enter an integer");
				return;
			}

			Collection results = Writer.sellStock(tickername, user, no, users,
					stocks);
			if (results != null) {				
				userStocks = DataReader.getUserStocks();
				stocks = DataReader.getStocks();
				users = DataReader.getUsers(); 
				user = DataReader.getUserByUsername(user.getUsername(), users);				
				this.sendToUser("Sale successful.");
			} else {
				this.sendToUser("Sale unsuccessful. Please cross-check your portfolio");
			}
		} else {
			this.sendToUser("Invalid sell command. Please use SELL <TICKERNAME> <NO>");
		}
	}

	private void unknowCommand(String command) {
		System.out.println("Current command is " + currentCommand);
		if (currentCommand.equalsIgnoreCase("follow")) {
			if (command.equals("reset")) {
				currentCommand = "";
				sendToUser("Server Working ... ");
			} else {
				this.follow(command);
			}
		} else if (currentCommand.equalsIgnoreCase("checkportfolio")) {
			this.checkportfolio();
		} else if (currentCommand.equalsIgnoreCase("buy")) {
			if (command.equals("reset")) {
				currentCommand = "";
				sendToUser("Server Working ... ");
			} else {
				this.buy(command);
			}
		} else if (currentCommand.equalsIgnoreCase("SELL")) {
			if (command.equals("reset")) {
				currentCommand = "";
				sendToUser("Server Working ... ");
			} else {
				this.sell(command);
			}
		} else {
			sendToUser("Invalid command");
		}

	}
}
