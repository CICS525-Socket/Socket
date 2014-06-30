package sockets;

public class Stock {
	private double price;
	private int no;
	private String tickername;
	
	/* this constructor would be removed afterwards. It is just for test
	 * 
	 */
	public Stock(String tickername, int no, double price){
		this.tickername = tickername;
		this.price = price;
		this.no = no;
	}
	
	public Stock() {
		
	}
	
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	/**
	 * @return the no
	 */
	public int getNo() {
		return no;
	}
	/**
	 * @param no the no to set
	 */
	public void setNo(int no) {
		this.no = no;
	}
	/**
	 * @return the tickername
	 */
	public String getTickername() {
		return tickername;
	}
	/**
	 * @param tickername the tickername to set
	 */
	public void setTickername(String tickername) {
		this.tickername = tickername;
	}    
}
