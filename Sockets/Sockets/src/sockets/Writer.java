/**
 * 
 */
package sockets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author welcome
 * 
 */
public class Writer {

	/* write all the stock values to the file that stores them */
	public synchronized static void writeStockValues(ArrayList<Stock> stocks) {

		// sampling purposes. remove later
		/*
		 * stocks = new ArrayList<Stock>(); stocks.add(new Stock("goog", 1000,
		 * PriceUpdater.price("goog"))); stocks.add(new Stock("yhoo", 1000,
		 * PriceUpdater.price("yhoo"))); stocks.add(new Stock("msft", 1000,
		 * PriceUpdater.price("msft")));
		 */
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

	/* write all the user stocks to the userstocks.txt file */
	public synchronized static void writeAllUserStocks(ArrayList<UserStocks> all) {
		File f = new File("userstocks.txt");
		FileWriter fr = null;
		try {
			fr = new FileWriter(f);
			BufferedWriter br = new BufferedWriter(fr);
			String s = "";

			for (UserStocks e : all) {
				s += e.getUsername() + " " + e.getTickername() + " "
						+ e.getNo();
				s += "\n";
			}
			br.write(s);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* write all the users to the file that stores the users */
	public synchronized static void writeAllUsers(ArrayList<User> users) {
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
	}

	/*
	 * this method takes the name of the stock that the user has requested for
	 * and add its to the file if it is valid
	 */
	public static synchronized ArrayList<Stock> addStock(String tickername,
			ArrayList<Stock> stocks) {
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
		writeStockValues(stocks);
		return stocks;
	}

	/* this method checks if the stock exists in the stock list */
	public static boolean stockExists(String tickername, ArrayList<Stock> stocks) {
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
	public static synchronized User addUser(String username,
			ArrayList<User> users) {

		// check if the user exists
		for (User u : users) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				System.out.println("User already exists");
				return u;
			}
		}

		// add the user to the user arraylist
		User u = new User();
		u.setBalance(1000);
		u.setUsername(username);
		u.setUserStock(null);
		users.add(u);

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
		return u;
	}

	/* call this method when you want a user to purchase a stock */
	public static synchronized Collection purchaseStock(String tickername,
			User username, int no, ArrayList<User> users,
			ArrayList<Stock> stocks) {
		// the collection that is returned
		Collection results = new ArrayList();

		// check if that no of stocks are available
		Stock purchasedStock = DataReader.getStockByTickername(tickername,
				stocks);
		
		if(purchasedStock == null) {
			return null;
		}
		
		if (purchasedStock.getNo() < no) {
			System.out
					.println("The amount of stocks remaining is less that what you want. The amount remaining is "
							+ purchasedStock.getNo());
			return null;
		}

		// calculate what it would cost from the amount and the price
		double costPrice = 0;
		try {
			costPrice = no * PriceUpdater.price(tickername);
		} catch (Exception e) {
			System.out.println("Invalid tickername. Please try again");
			return null;
		}
		// check if the user has enough balance
		double userBalance = DataReader.getUserBalance(username.getUsername(),
				users);
		if (userBalance < costPrice) {
			System.out.println("You do not have sufficient funds. You have $"
					+ userBalance + ". Please review your purchase");
			return null;
		}

		// add the stock to the userStocks arraylist and write the values
		ArrayList<UserStocks> userStocks = DataReader.getUserStocks();
		int index = 0;
		boolean flag = false;
		// check to see if the user already has some of that stock
		for (UserStocks us : userStocks) {
			if (us.getTickername().equalsIgnoreCase(tickername)
					&& us.getUsername()
							.equalsIgnoreCase(username.getUsername())) {
				// user already has some of those stocks
				us.setNo(us.getNo() + no);
				userStocks.set(index, us);
				flag = true;
				break;
			}
			index++;
		}
		// user does not have any of those stocks
		if (!flag) {
			UserStocks newUs = new UserStocks();
			newUs.setNo(no);
			newUs.setTickername(tickername);
			newUs.setUsername(username.getUsername());
			userStocks.add(newUs);
			results.add(userStocks); // add the user stocks to the collection.
										// index 0
		}
		// write updated userstocks values to file
		writeAllUserStocks(userStocks); // write the stock values to the file

		// update and write the no of stocks of the stock
		int indexOfPurchasedStock = stocks.indexOf(purchasedStock);
		purchasedStock.setNo(purchasedStock.getNo() - no);
		stocks.set(indexOfPurchasedStock, purchasedStock);
		results.add(stocks); // index 1
		writeStockValues(stocks);

		// update the user balance
		int userIndex = users.indexOf(username);
		username.setBalance(username.getBalance() - costPrice);
		username.setUserStock(userStocks);
		users.set(userIndex, username);

		results.add(users); // index 2 is the updated list of all the users
		writeAllUsers(users); // write all the users to file
		results.add(username); // index 3 is the updated value of the user
		// return the collection that was just purchased
		return results;
	}

	public synchronized static Collection sellStock(String tickername,
			User username, int no, ArrayList<User> users,
			ArrayList<Stock> stocks) {
		// initialize a collection to hold the updated values
		Collection results = new ArrayList();
		// calculate what it would cost from the no and the price
		double costPrice = 0;
		try {
			costPrice = no * PriceUpdater.price(tickername);
		} catch (Exception e) {
			System.out.println("Invalid tickername. Please try again");
			return null;
		}
		// check if the user has that no of stocks
		int usercount = username.getNoOfStocksByUserAndTickerName(
				username.getUsername(), tickername);
		if (usercount < no) {
			System.out.println("You do not have that no of stocks. You have "
					+ usercount + " " + tickername + " stocks");
			return null;
		}

		// update the userStocks arraylist and write the values to file
		ArrayList<UserStocks> userStocks = DataReader.getUserStocks();
		int index = 0;
		// look for that particular stock being sold and update it
		for (UserStocks us : userStocks) {
			if (us.getTickername().equalsIgnoreCase(tickername)
					&& us.getUsername()
							.equalsIgnoreCase(username.getUsername())) {
				// user already has some of those stocks
				us.setNo(us.getNo() - no);
				userStocks.set(index, us);
				break;
			}
			index++;
		}

		results.add(userStocks); // add the user stocks to the collection. index
		// update and write the no of stocks of the stock
		writeAllUserStocks(userStocks); // write the stock values to the file

		// update and write the no of stocks of the stock
		Stock soldStock = DataReader.getStockByTickername(tickername, stocks);
		int indexOfSoldStock = stocks.indexOf(soldStock);
		soldStock.setNo(soldStock.getNo() + no);
		stocks.set(indexOfSoldStock, soldStock);
		results.add(stocks); // index 1
		writeStockValues(stocks);

		// update the user balance
		int userIndex = users.indexOf(username);
		username.setBalance(username.getBalance() + costPrice);
		username.setUserStock(userStocks);
		users.set(userIndex, username);

		results.add(users); // index 2 is the updated list of all the users
		writeAllUsers(users); // write all the users to file
		results.add(username); // index 3 is the updated value of the user
		// return the collection that was just purchased
		return results;
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
