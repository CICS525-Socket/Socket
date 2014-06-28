/**
 * 
 */
package sockets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author welcome
 * 
 */
public class Writer {

	public synchronized static void writeStockValues(ArrayList<Stock> stocks) {

		// sampling purposes. remove later
		stocks = new ArrayList<Stock>();
		stocks.add(new Stock("goog", 1000, 577.90));
		stocks.add(new Stock("yhoo", 1000, 577.90));
		stocks.add(new Stock("msft", 1000, 577.90));
		// end of sampling

		File f = new File("stocks.txt");
		FileWriter fr = null;
		try {
			fr = new FileWriter(f);
			BufferedWriter br = new BufferedWriter(fr);
			String s = "";

			for (Stock e : stocks) {
				s += e.getTickername() + " " + e.getNo() + " " + e.getPrice();
				s += "\n";
			}
			br.write(s);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * this method takes the name of the stock that the user has requested for
	 * and add its to the file if it is valid
	 */
	public ArrayList<Stock> addStock(String tickername, ArrayList<Stock> stocks) {
		double price = 0;
		try {
			price = PriceUpdater.price(tickername);
		} catch (Exception e) {
			System.out.println("The tickername you entered is invalid");
			return stocks;
		}

		if (price > 0 && !stockExists(tickername, stocks)) {
			Stock newStock = new Stock(tickername, 1000, price);
			stocks.add(newStock);
		}
		return stocks;
	}

	/* this method checks if the stock exists in the stock list */
	public boolean stockExists(String tickername, ArrayList<Stock> stocks) {
		for (Stock e : stocks) {
			if (e.getTickername().equalsIgnoreCase(tickername)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * add a user to the list of users. It first checks if the user exists
	 * before adding. Returns null if no user was added
	 */
	public User addUser(String username, ArrayList<User> users) {

		// check if the user exists
		for (User u : users) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				System.out.println("User already exists");
				return null;
			}
		}

		// write to the users file
		File f = new File("users.txt");
		FileWriter fr = null;
		try {
			fr = new FileWriter(f);
			BufferedWriter br = new BufferedWriter(fr);
			String s = "";

			for (User e : users) {
				s += e.getUsername() + " " + e.getBalance();
				s += "\n";
			}
			br.write(s);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Stock> s = new ArrayList<Stock>();
		Writer.writeStockValues(s);

	}

}
