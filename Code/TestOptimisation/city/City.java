package gi.city;

import gi.agents.Agent;
import gi.city.Cell.Location;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;
import java.util.Arrays;

public class City{
	private int city_ID;
	private Lattice lattice;
	private List<int[]> CENTRAL_POINT;
	private static List<Cell> urbanCells;

	/**
	 * Last cell urbanised
	 */
	private Cell lastUrbanised;
	/**
	 * Total urban cells
	 */
	private static int numUrbanCells;
	/**
	 * Maximum urban price
	 */
	private static int maxUrbanPrice;
	/**
	 * Minimum urban price
	 */
	private static int minUrbanPrice;
	/**
	 * Current maximum population in a cell
	 */
	private static int maxPopulation;
	/**
	 * Current maximum population in a cell
	 */
	private static int minPopulation;
	/**
	 * Maximum value of the transport costs. For a display
	 */
	private static int maxTransportCosts;
	/**
	 * Maximum value of the transport costs. For a display
	 */
	private static int minTransportCosts;

	// Constants
	/**
	 * Number of rings
	 */
	public static int NUMBER_RINGS;

	/**
	 * Initial urban price
	 */
	public static int INIT_URBAN_PRICE;

	/**
	 * Urban maximum bioValue
	 */

	private static double MAX_URBAN_BIOVALUE;

	/**
	 * Percentage of non-occupied houses that it is allowed to constructed new
	 * houses
	 */
	public static final double LIMIT_CITY_GROWTH;
	public static final int MAX_EMPTY_CELLS;

	/**
	 * CBD of the lattice. Used to calculate the rings
	 */
	private Location centre;

	/**
	 *  Num of empty cells in the lattice
	 */
	private static int numEmptyCells;
	/**
	 * Constant that represents the increment rate of the cost of transport in
	 * relation to the distance
	 */
	public static int TRANSPORT_RATE;
	
	/**
	 * String with the protected cells selected in this tick
	 */
	private String selectedThisTick;

	static{
		LIMIT_CITY_GROWTH = 0.1;
		//PERCENTAGE_INITIAL_DENSITY = 0.07;
		TRANSPORT_RATE = 25;
		MAX_EMPTY_CELLS = 2;
		INIT_URBAN_PRICE = 500;
		MAX_URBAN_BIOVALUE = 0.3;
		
		urbanCells = new ArrayList<Cell>();
		
		numEmptyCells = 0;
		numUrbanCells = 1;
		maxUrbanPrice = Integer.MIN_VALUE;
		minUrbanPrice = Integer.MAX_VALUE;
		maxPopulation = Integer.MIN_VALUE;
		minPopulation = Integer.MAX_VALUE;
		maxTransportCosts = Integer.MIN_VALUE;
		minTransportCosts = Integer.MAX_VALUE;
	}

	/**
	 * Constructor of the city
	 * @param model
	 */
	public City(Lattice lattice, int ID){
		CENTRAL_POINT = new ArrayList<int[]>();
		
		this.city_ID = ID;
		this.lattice = lattice;
	}

	/**
	 * Build the initial city
	 */
	public void initCity(){
		initialiseShapes();

		// Initialise the starting cells in the city
		for(int[] coords : CENTRAL_POINT) {
			Cell cell = setState(CellState.NEW, coords);
			cell.setCity_ID(this.city_ID);
			centre = cell.getLocation();
			City.numUrbanCells = 1;
			cell.setBioValue(MAX_URBAN_BIOVALUE);
			cell.setPrice(INIT_URBAN_PRICE);

			//Calculate the ring that every cell belongs
			cell.calculateRing();
			cell.calculatePrice();
			this.lastUrbanised = cell;
		}

		// Calculate number of rings
		if (Lattice.SIZE_LATTICE % 2 == 0)
			NUMBER_RINGS = Lattice.SIZE_LATTICE / 2;
		else
			NUMBER_RINGS = (Lattice.SIZE_LATTICE + 1) / 2;
	}

	/**
	 * Sets the state of one of the cells in the grid
	 * @param state
	 * @param pos
	 * @return return the updated cell
	 */
	Cell setState(CellState state, int[] pos) {
		Cell c = lattice.urbanisedCell(pos);
		c.setState(state);
		c.updateState();
		return c;
	}

	/**
	 * Initialises the possible starting shapes
	 */
	void initialiseShapes() {
		int totalAreas = Lattice.CBDS.length;
		
		int counter = 0, i = 0;
		int maxCounter = Lattice.CBDS[0];
		while (counter < this.city_ID){
			counter++;
			if(counter == maxCounter){
				i++;
				maxCounter += Lattice.CBDS[i];	
			}
		}
		
		int cx = (Lattice.SIZE_LATTICE * (i + 1)) / (totalAreas + 1);
		int cy= (Lattice.SIZE_LATTICE * (maxCounter - counter)) / (Lattice.CBDS[i] + 1);
		
		System.out.println("Cx:" + cx + " Cy:" + cy + " in city:" + this.getCity_ID());

		// Central point is just one coordinate
		CENTRAL_POINT.add(new int[] { cx, cy });
	}

	public static List<Cell> getUrbanCells(){
		Collections.shuffle((List<Cell>) City.urbanCells, new SecureRandom());
		return City.urbanCells;
	}

	/**
	 * @author mv59
	 * @param cell Cell to be allocated
	 * @param location Location of the new cell
	 */
	public static void addUrbanCell(Cell cell) throws NullPointerException{
		if(cell==null)
			throw new NullPointerException("Attempt to add a null cell (City Class)");
		City.urbanCells.add(cell);
	}

	/**
	 * Return a cell given their coordinates
	 * @author mv59
	 * @param x
	 * @param y
	 * @return Cell
	 */
	public static Cell getCell(int x, int y){
		Cell returnCell = null;
		for(Cell c: City.urbanCells){
			if(c.getLocation().getX() == x && c.getLocation().getY() == y)
				returnCell = c;
		}
		return returnCell;
	}

	public double calculateDensity() {
		return lattice.context.getObjects(Agent.class).size() * 1.0 / City.numUrbanCells;
	}

	/**
	 * Update Urban prices
	 * @return
	 */
	@ScheduledMethod( start = 1.1, interval=1, priority=0 )
	public static void updatePrices(){
		for(Cell c:City.urbanCells){
			//It can be available
			if(c.isHabitable()){
				c.updatePriceUrbanArea();
			}
		}
	}

	public static boolean isCellsEmpty(){
		if(City.numEmptyCells>MAX_EMPTY_CELLS)
			return true;
		else
			return false;
	}
	
	public static void addEmptyCells(){
		City.numEmptyCells++;
	}
	
	public static void removeEmptyCells(){
		City.numEmptyCells--;
	}
	
	public static int getNumEmptyCells(){
		return City.numEmptyCells;
	}
	
	/**
	 * @return highest price in the urban lattice
	 */
	public static int getMaxUrbanPrice(){
		return City.maxUrbanPrice;
	}
	
	public static void setMaxUrbanPrice(int price){
		City.maxUrbanPrice = price;
	}
	
	public static int getMinUrbanPrice(){
		return City.minUrbanPrice;
	}
	
	public static void setMinUrbanPrice(int price){
		City.minUrbanPrice = price;
	}

	public static int getTotalUrbanCells(){
		return City.numUrbanCells;
	}
	
	public static void incrementUrbanCells(int n){
		City.numUrbanCells += n;
	}
	
	public static double getAvgUrbanPrice(){
		int totalPrice = 0;
		int times = 0;
		for(Cell c: City.urbanCells){
			if(c.getState() != CellState.AVAILABLE){
				totalPrice += c.getPrice();
				times++;
			}
		}
		return (totalPrice*1.0)/times;
	}
	
	public static double getAvgUrbanPrice(int ring) {
		double priceRing = 0;
		int times = 0;
		for (Cell c : City.urbanCells) {
			if (c.getRing() == ring && c.isHabitable()) {
				priceRing += c.getPrice();
				times++;
			}
		}
		return (priceRing * 1.0) / times;
	}

	public int getCentreX() {
		return centre.getX();
	}
	
	public int getCentreY(){
		return centre.getY();
	}
	
	public static int getMaxPopulated(){
		return City.maxPopulation;
	}
	
	public static int getMinPopulated(){
		return City.minPopulation;
	}
	
	public String getSelectedThisTick(){
		return selectedThisTick;
	}
	
	public void setMaxPopulation(int maxPopulation){
		City.maxPopulation = maxPopulation;
	}

	public void setMinPopulation(int minPopulation){
		City.minPopulation = minPopulation;
	}

	public static int getMaxTransportCosts() {
		return maxTransportCosts;
	}

	public static int getMinTransportCosts() {
		return minTransportCosts;
	}

	public static void setMaxTransportCosts(int transportCost) {
		City.maxTransportCosts = transportCost;
	}
	
	public Cell getLastUrbanised(){
		return this.lastUrbanised;
	}

	public void setLastUrbanised(Cell cell){
		this.lastUrbanised= cell;
	}

	public static void resetUrbanPrices(){
		City.maxUrbanPrice = Integer.MIN_VALUE;
		City.minUrbanPrice = Integer.MAX_VALUE;
	}
	
	public int getCity_ID(){
		return this.city_ID;
	}

	public static int distanceUrban(Cell cell){
		int distance = Integer.MAX_VALUE;
		for(Cell cu: City.getUrbanCells()){
			int d =  Math.abs(cell.getLocation().getX() - cu.getLocation().getX())
			+ Math.abs(cell.getLocation().getY() - cu.getLocation().getY());
			if(d<distance){
				distance = d;
			}
		}
		return distance;
	}
}