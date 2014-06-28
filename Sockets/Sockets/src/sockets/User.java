/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sockets;

import java.util.ArrayList;

/**
 *
 * @author Ali
 */
public class User {
	private String username;
	private double balance;
	private ArrayList<Stock> userStock;
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
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
	 * @param balance the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
	/**
	 * @return the userStock
	 */
	public ArrayList<Stock> getUserStock() {
		return userStock;
	}
	/**
	 * @param userStock the userStock to set
	 */
	public void setUserStock(ArrayList<Stock> userStock) {
		this.userStock = userStock;
	} 
}
