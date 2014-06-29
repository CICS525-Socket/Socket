package sockets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {

	/* gets all the registered users */
	public static ArrayList<User> getUsers() {
		ArrayList<User> users = new ArrayList<User>();
		User u = new User();
		File f = new File("users.txt");
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String s = "";
			while ((s = br.readLine()) != null) {
				String[] comp = splitLine(s);
				u.setUsername(comp[0]);
				u.setBalance(Double.valueOf(comp[1]));
				u.setUserStock(getUserStocks(u.getUsername()));
				users.add(u);
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

	/*
	 * this method takes a username and returns the stocks that the user has It
	 * performs the function of the checkPortfolio It reads the data from a file
	 * called userstocks.txt
	 */
	public static ArrayList<UserStocks> getUserStocks(String username) {
		ArrayList<UserStocks> usersStocks = new ArrayList<UserStocks>();
		UserStocks u = new UserStocks();
		File f = new File("userstocks.txt");
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String s = "";
			while ((s = br.readLine()) != null) {
				String[] comp = splitLine(s);
				if (comp[0].equalsIgnoreCase(username)) {
					u.setUsername(comp[0]);
					u.setTickername(comp[1]);
					u.setNo(Integer.valueOf(comp[2]));
					usersStocks.add(u);
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return usersStocks;
	}

	/*
	 * returns the cash balance of a user from the arraylist of users return 0
	 * if the user is not found
	 */
	public static double getUserBalance(String username, ArrayList<User> users) {
		for (User u : users) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				return u.getBalance();
			}
		}
		return 0;
	}

	/*
	 * this gets the stocks values from the file and populates the arraylist of
	 * stock
	 */
	public ArrayList<Stock> getStocks() {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		Stock stock = new Stock();
		File f = new File("stocks.txt");
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String s = "";
			while ((s = br.readLine()) != null) {
				String[] comp = splitLine(s);
				stock.setNo(Integer.valueOf(comp[2]));
				stock.setPrice(Double.valueOf(comp[1]));
				stock.setTickername(comp[0]);
				stocks.add(stock);
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stocks;
	}

	private static String[] splitLine(String line) {
		String[] comp = line.split(" ");
		if (comp.length == 3) {
			return comp;
		} else {
			return new String[3];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
