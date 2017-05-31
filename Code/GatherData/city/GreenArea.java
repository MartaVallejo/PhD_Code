package gi.city;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author marta
 *
 */
public class GreenArea{
	Context context;
	private static List<Cell> nonUrbanCells;
	
	/**
	 * Array with statistics about prices
	 */
	private List<int[]> statisticsNonUrbanPrices;
	
	/**
	 * Parameters of biology value
	 */
	private boolean updateBioValue;

	/**
	 * Maximum green price
	 */
	private static double maxGreenPrice;
	
	/**
	 * Minimum green price
	 */
	private static double minGreenPrice;
	
	static{
		nonUrbanCells = new ArrayList<Cell>();
		
		maxGreenPrice = Double.MIN_VALUE;
		minGreenPrice = Double.MAX_VALUE;
	}
	
	/**
	 * Constructor of the areas non urbanised of the city
	 */
	public GreenArea(Context context){
		this.context = context;
		updateBioValue = false;
	}
	
	/**
	 * @author marta
	 */
	public void addCell(Cell cell){
		GreenArea.nonUrbanCells.add(cell);
	}

	/**
	 * @author mv59
	 * Load in an array the population settled in the lattice
	 * (used to calculate the future fitness)
	 */
	public void loadNonUrbanPricesStatistics(){
		statisticsNonUrbanPrices = new ArrayList<int[]>();
		try{	
			Scanner sc = new Scanner(new File("NonUrbanPrices.txt"));
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					int[] row = new int[Lattice.SIZE_LATTICE*Lattice.SIZE_LATTICE + 1];
					String[] val = newline.split(" ");
					for(int i=0; i<val.length;i++){
						row[i] = Integer.parseInt(val[i]);
					}
					//System.out.println("Load the row: " + ++line + " with length: " + row.length);
					this.statisticsNonUrbanPrices.add(row);
				}else{
					System.out.println("Line empty:" + newline);
				}
			}
			
			/*int lines = 1;
			int numElem = 0;
			System.out.println("Writing the content of statistics density");
			for(int[] a: this.statisticsNonUrbanPrices){
				//System.err.println("line list:" + lines);
				lines++;
				for(int i=0; i<a.length;i++){
					numElem++;
					//System.out.print(a[i] + " ");
				}
				//System.err.println("\nTotal number of elements:" + numElem);
				numElem = 0;
			}*/
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the statistics about density");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Accessing wronly to the array");
			e.printStackTrace();
		}catch (Exception e){
			System.err.println("Other exception: statistics about density");
			e.printStackTrace();	
		}
	}
	
	/**
	 * Initialise the biological value of the cells according to its neighbourhood
	 * @author marta
	 */
	public void initBioInfluence(){
		boolean isChanged;
		try{
			do{
				isChanged = false;
				for( Cell c : GreenArea.nonUrbanCells ) {
					double value = c.cellBioValue();
					if( value!= c.getBioNeighbourValue()){
						c.setBioNeighbourValue(value);
						isChanged = true;
					}
				}
			}while(isChanged);
		}catch(Exception e){
			System.err.println("Error in initBio Influence");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param cell Cell to look for the neighbourhood to decrease
	 * the biological value
	 * @return
	 */
	public void decreaseNeigbourhoodBioValue(Cell cell, int distance){
		int distanceX, distanceY, finalDistance;
		for( Cell c : GreenArea.nonUrbanCells ) {
			distanceX = Math.abs(c.getLocation().getX() - cell.getLocation().getX());
			distanceY = Math.abs(c.getLocation().getY() - cell.getLocation().getY());
			//if it is inside the range
			if(distanceX<= distance && distanceY<= distance && 
					(distanceX != 0 || distanceY != 0 )){
				if(distanceX > distanceY)
					finalDistance = distanceX;
				else
					finalDistance = distanceY;
				switch (finalDistance){
				case 1:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.1");
					c.setBioValue(cell.getBioValue() - 0.1);
					break;
				case 2:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.05");
					c.setBioValue(cell.getBioValue() - 0.05);
					break;
				case 3:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.01");
					c.setBioValue(cell.getBioValue() - 0.01);
					break;
				}				
			}
			//It does not allow negative values
			if(c.getBioValue()<0)
				c.setBioValue(0);
		}
	}
	
	/**
	 * Update the bio parameters and prices
	 */
	@ScheduledMethod( start = 1.1, interval=1, priority=0 )
	public void updateParameters(){	
		//Update neighbourhood bio values
		if(getUpdateBioValue() == true){
			//Update bio influence
			initBioInfluence();
		}else
			setUpdateBioValue(false);
		updateGreenPrices();
	}
	
	/**
	 * Update green prices
	 */
	public void updateGreenPrices(){
		double minPrice = Double.MAX_VALUE;
		for(Cell c:GreenArea.nonUrbanCells){
			c.updatePriceRuralArea();
			if(c.getPrice()<minPrice)
				minPrice = c.getPrice();
		}
		GreenArea.minGreenPrice = minPrice;
		//System.out.println("!!!!!!!GreenPrices updated:" + GreenArea.minGreenPrice);
	}
	
	/**
	 * Reset min man green prices
	 */
	public void resetGreenPrices(){	
		GreenArea.maxGreenPrice = Integer.MIN_VALUE;
		GreenArea.minGreenPrice = Integer.MAX_VALUE;
	}
	
	/**
	 * @author marta
	 * Search a determine cell in the arrayList
	 * @param x Coordinate x
	 * @param y Coordinate y
	 * @return Cell searched
	 */
	public static Cell getCell(int x, int y){
		Cell returnCell = null;
		
		for(Cell c: GreenArea.nonUrbanCells){
			if(c.getLocation().getX() == x && c.getLocation().getY() == y)
				returnCell = c;
		}
		return returnCell;
	}
	
	/**
	 * @author mv59
	 * @param location Location of the cell that is going to be urbanised
	 * @return the cell removed
	 */
	public Cell getRemoveCell(int[] location){
		for(Cell cell: GreenArea.nonUrbanCells){
			if(cell.hasLocation(location)){
				GreenArea.nonUrbanCells.remove(cell);
				return cell;
			}
		}
		return null;
	}
	
	public List<int[]> getStatisticsNonUrbanPrices(){
		return this.statisticsNonUrbanPrices;
	}
	
	public int[] getStatisticsNonUrbanPrices(int elem){
		int[] aux = null;
		try{
			aux = this.statisticsNonUrbanPrices.get(elem);
		}catch(IndexOutOfBoundsException e){
			System.err.println("GetStatistics:Element in Statistic Urbanised does not exist");
			System.err.println("Trying to access elem:" + elem + " total elements:" + this.statisticsNonUrbanPrices.size());
		}
		return aux;
	}

	public void updateBioInfluence(){
		this.updateBioValue = true;
	}
	
	public void addStatisicsNonUrbanPrices(int[] newRow){
		this.statisticsNonUrbanPrices.add(newRow);
	}
	
	public void setStatisicsNonUrbanPrices(int index,  int[] newRow){
		try{
			this.statisticsNonUrbanPrices.set(index, newRow);
		}catch(IndexOutOfBoundsException e){
			System.err.println("SetStatistics:Element in Statistic Density does not exist");
			System.err.println("Trying to access elem:" + index + " total elements:" + this.statisticsNonUrbanPrices.size());
		}
	}
	
	public boolean getUpdateBioValue(){
		return this.updateBioValue;
	}
	
	public void setUpdateBioValue(boolean value){
		this.updateBioValue = value;
	}

	public static List<Cell> getNonUrbanCells(){
		return GreenArea.nonUrbanCells;
	}

	/**
	 * @return highest price in the urban lattice
	 */
	public static double getMaxGreenPrice(){
		return GreenArea.maxGreenPrice;
	}
	
	public static void setMaxGreenPrice(int price){
		GreenArea.maxGreenPrice = price;
	}
	
	public static double getMinGreenPrice(){
		return GreenArea.minGreenPrice;
	}
	
	public static void setMinGreenPrice(int price){
		GreenArea.minGreenPrice = price;
	}

	public double getAvgGreenPrice(){
		double totalPrice = 0;
		int times = 0;
		for(Cell c:GreenArea.nonUrbanCells){
			totalPrice +=c.getPrice();
			times++;
		}
		return (totalPrice*1.0)/times;
	}
}
