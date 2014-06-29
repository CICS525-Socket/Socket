package sockets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataReader {

	/* gets all the registered users */
	public ArrayList<User> getUsers() {
		return null;
	}
	
	public ArrayList<UserStocks> getUserStocks(String username) {
		return null;
	}
	
	public double getUserBalance(String username) {
		return 0;
	}

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

	private String[] splitLine(String line) {
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
