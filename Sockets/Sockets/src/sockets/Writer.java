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
		// FileReader fr = new FileReader(f);
		FileWriter fr = null;
		try {
			fr = new FileWriter(f);

			BufferedWriter br = new BufferedWriter(fr);
			// BufferedReader br = new BufferedReader(fr);

			String s = "";

			/*
			 * while ((br.readLine())!=null) { // Do whatever u want to do with
			 * the content of the file,eg print it on console using SysOut...etc
			 * }
			 */
			for (Stock e : stocks) {
				s += e.getTickername() + " " + e.getNo() + " " + e.getPrice();
				s += "\n";
			}
			br.write(s);
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Stock> s = new ArrayList<Stock>();
		Writer.writeStockValues(s);

	}

}
