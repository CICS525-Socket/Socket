package sockets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class PriceUpdater implements Runnable {
	private ArrayList<Stock> myStocks;
	private static Scanner scanner;

	public static Scanner in(URL url) {
		try {
			URLConnection site = url.openConnection();
			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is));
		} catch (IOException ioe) {
			System.err.println("Could not open " + url);
		}
		return scanner;
	}

	// Given tickername, get HTML
	public static String readHTML(String symbol) {
		URL stockUrl = null;
		try {
			stockUrl = new URL("http://finance.yahoo.com/q?s=" + symbol);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scanner = in(stockUrl);
		String html = readAll();
		// System.out.println(html);
		return html;
	}

	// Given tickername, get current stock price.
	public static double price(String symbol) {
		String html = readHTML(symbol);
		int p = html.indexOf("yfs_l84", 0); // "yfs_l84" index
		int from = html.indexOf(">", p); // ">" index
		int to = html.indexOf("</span>", from); // "</span>" index
		String price = html.substring(from + 1, to);
		return Double.parseDouble(price.replaceAll(",", ""));
	}

	// Given tickername, get current stock name.
	public static String name(String symbol) {
		String html = readHTML(symbol);
		int p = html.indexOf("<title>", 0);
		int from = html.indexOf("Summary for ", p);
		int to = html.indexOf("- Yahoo! Finance", from);
		String name = html.substring(from + 12, to);
		return name;
	}

	// Given tickername, get current date.
	public static String date(String symbol) {
		String html = readHTML(symbol);
		int p = html.indexOf("<span id=\"yfs_market_time\">", 0);
		int from = html.indexOf(">", p);
		int to = html.indexOf("-", from); // no closing small tag
		String date = html.substring(from + 1, to);
		return date;
	}

	public static String readAll() {
		if (!scanner.hasNextLine())
			return "";
		String result = "";
		while (scanner.hasNextLine()) {
			result = result + scanner.nextLine();
			result = result + "\n";
		}
		return result;
	}

	public void run() {
		while (true) {
			// get the prices every 2 minutes and update them
			myStocks = DataReader.getStocks();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println("Last updated stocks at "
					+ dateFormat.format(date));
			if (myStocks != null) {
				for (Stock e : myStocks) {
					int index = myStocks.indexOf(e);
					e.setPrice(price(e.getTickername()));
					myStocks.set(index, e);
				}

				// write the values to the file
				Writer.writeStockValues(myStocks);

				// let the updater sleep for 2 minutes
				try {
					Thread.sleep(120000);					
				} catch (InterruptedException e1) {					
					e1.printStackTrace();
				}
			}
		}
	}
}
