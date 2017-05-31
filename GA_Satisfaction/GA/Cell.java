package GA;

import java.util.Random;


public class Cell {
	/**
	 * Coordinates
	 */
	private int coordX;
	private int coordY;
	private int price;
	private static int CREATE_CELL_ATTEMPTS;
	private static double URBANISED_LIMIT;
	
	static{
		CREATE_CELL_ATTEMPTS = 40;
		URBANISED_LIMIT = 0.2;
	}
	
	public Cell(int x, int y, int price){
		this.coordX = x;
		this.coordY = y;
		this.price = price;
	}
	
	public static Cell createCell(int timeStep, int individual, Lattice city, int budget){
		Cell c = null;
		int times = CREATE_CELL_ATTEMPTS;
	
		do{
			int x = Math.abs(new Random().nextInt(GeneticAlgorithm.SIZE_LATTICE));
			int y = Math.abs(new Random().nextInt(GeneticAlgorithm.SIZE_LATTICE));
			//Check if this cell is not selected before
			if(!Lattice.isForbidden(x, y, individual)){
				if(Lattice.statisticsUrbanised(x, y, timeStep)>URBANISED_LIMIT){
					int price = city.getPrice(timeStep, x, y);
					if(budget>=price){
						//System.err.println("Budget:" + budget + " Price:" + price);
						c = new Cell(x, y, price);
						return c;
					}
				}
			}
			times--;
		}while(times>0);
		return c;
	}
	
	public int getPrice(){
		return this.price;
	}
	
	public int getX(){
		return this.coordX;
	}
	
	public int getY(){
		return this.coordY;
	}
	
	public String toString(){
		return " (" + this.coordX + "," + this.coordY + ")";
	}
}
