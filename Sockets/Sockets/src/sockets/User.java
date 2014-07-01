package sockets;

import java.util.ArrayList;

public class User {
	private String username;
	private double balance;
	private ArrayList<UserStocks> thisUserStock;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * @return the userStock
	 */
	public ArrayList<UserStocks> getUserStock() {
		return thisUserStock;
	}

	/**
	 * @param userStock
	 *            the userStock to set
	 */
	public void setUserStock(ArrayList<UserStocks> userStock) {
		thisUserStock = new ArrayList<UserStocks>();
		if(userStock != null) {
		for (UserStocks us : userStock) {
			if (us.getUsername().equalsIgnoreCase(this.username) && us.getNo() > 0) {
				thisUserStock.add(us);
			}
		} }		
	}
	
	public int getNoOfStocksByUserAndTickerName(String username, String tickername) {
		tickername = tickername.toUpperCase();
		for(UserStocks us:thisUserStock) {
			if(us.getTickername().equals(tickername) && us.getUsername().equals(username)) {
				return us.getNo();
			}
		}
		return 0;
	}
}
