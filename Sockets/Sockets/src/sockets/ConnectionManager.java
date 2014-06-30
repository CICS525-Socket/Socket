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
import java.util.List;
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
		System.out.println("The size of the users list is " + users.size()
				+ " " + users.get(0).getUsername());
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
			// this.sendToUser(command[0]);
			if (!command[0].equals("close")) {
				switch (userCommand) {
				case "close":
					this.closeConnection();
					break;
				case "query":
					this.query();
					break;
				case "checkportfolio":
					currentCommand = "checkportfolio";
					this.checkportfolio();
					break;
				case "buy":
					currentCommand = "buy";
					// this.buy();
					break;
				case "sell":
					currentCommand = "sell";
					// this.sell();
					break;
				case "follow":
					currentCommand = "follow";
					// this.sendToUser("Calling the follow function");
					// this.follow();
					break;
				default:
					this.unknowCommand(userCommand);
					break;
				}
				// userCommand = this.readInputStream();
				// System.out.println("user command: " + userCommand);
				// command = commandParser(userCommand.toLowerCase().trim());
				// this.sendToUser(command[0]);
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
		// return null;
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

	private void follow(String ticker) {
		currentCommand = "follow";
		// this.sendToUser("Calling the follow function");
		String tickername = ticker;
		System.out.println("The tickername is " + tickername);
		this.stocks = Writer.addStock(tickername, stocks);
		Stock rStock = DataReader.getStockByTickername(tickername, stocks);
		if (rStock != null) {
			this.sendToUser("Stockname: " + rStock.getTickername()
					+ ". Price: " + rStock.getPrice() + ". Remaining: "
					+ rStock.getNo());
		} else {
			this.sendToUser("Tickername is invalid");
		}
	}

	private void checkportfolio() {
		String response = "Your current balance is $" + user.getBalance();
		System.out.println("Your current balance is $" + user.getBalance());
		ArrayList<UserStocks> portfolio = user.getUserStock();

		if (portfolio != null) {
			System.out.println("Your portfolio is as follows :");
			System.out
					.println("TickerName \t Full Name \t Current Price \t Quantity");
			response += "\nYour portfolio is as follows :"
					+ "\nTickerName \t Full Name \t Current Price \t Quantity";
			for (UserStocks e : portfolio) {
				System.out.println(e.getTickername() + " \t "
						+ PriceUpdater.name(e.getTickername()) + " \t "
						+ PriceUpdater.price(e.getTickername()) + " \t "
						+ e.getNo());
				response += "\n" + e.getTickername() + " \t "
						+ PriceUpdater.name(e.getTickername()) + " \t "
						+ PriceUpdater.price(e.getTickername()) + " \t "
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
		System.out.println("Getting to the buy command");
		// this.sendToUser("Calling the follow function");
		String[] commandComps = command.split(" ");
		if(commandComps.length != 3) {
			sendToUser("Invalid command");
			return;
		}
		if (commandComps[0].equals("buy") && commandComps[1].startsWith("<")
				&& commandComps[1].endsWith(">")
				&& commandComps[2].startsWith("<")
				&& commandComps[2].endsWith(">")) {
			String tickername = commandComps[1].substring(1,
					commandComps[1].length() - 1);
			int no = Integer.parseInt(commandComps[2].substring(1,
					commandComps[2].length() - 1));

			Collection results = Writer.purchaseStock(tickername, user, no,
					users, stocks);
			if (results != null) {
				List list = new ArrayList(results);
				userStocks = (ArrayList<UserStocks>) list.get(0);
				stocks = (ArrayList<Stock>) list.get(1);
				users = (ArrayList<User>) list.get(2);
				user = (User) list.get(3);
				System.out.println(user.getBalance());
				//checkportfolio();
				this.sendToUser("Purchase successful.");
			} else {
				this.sendToUser("Purchase unsuccessful.");
			}
		} else {
			this.sendToUser("Invalid command");
		}

	}

	private void sell() {

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
		} else {
			sendToUser("Invalid command");
		}

	}
}
