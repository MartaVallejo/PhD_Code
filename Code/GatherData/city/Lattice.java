package gi.city;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;

/**
 * 
 * @author marta
 *
 */
public class Lattice {
	Grid<Cell> grid;
	private City[] city;
	public final static int TOTAL_TICKS;
	public static int NUM_CBDS;
	public final static int[] CBDS;
	public final static int SCENARIO;
	public static String SCENARIO_NAME;
	public final static int SIZE_LATTICE;
	public static int TYPE_BUDGET;
	private GreenArea greenArea;
	public Context context;
	private Schedule sche;
	
	//Urban growth data
	private static int[] numAvailable;
	private static int[] numNew;
	private static int[] numOld;
	private static int[] numRural;
	
	//Statistics
	/**
	 * Array with statistics about cells and when the cell is urbanised
	 */
	private List<int[]> statisticsUrbanised;
	/**
	 * Array with statistics about density
	 */
	private List<int[]> statisticsDensity;
	/**
	 * Array with prices of rings
	 */
	private ArrayList<double[]> priceRings;
	/**
	 * Array with bioValues
	 */
	private ArrayList<double[]> statisticsBio;
	
	static{
		TOTAL_TICKS = 600;
		Parameters p = RunEnvironment.getInstance().getParameters();
		SIZE_LATTICE = (Integer) p.getValue( "worldSize" );
		
		/////////////////////////////////////////
		numRural = new int[TOTAL_TICKS];
		numAvailable = new int[TOTAL_TICKS];
		numNew = new int[TOTAL_TICKS];
		numOld = new int[TOTAL_TICKS];
		
		//0: RANDOM DISTRIBUTION, 1: POPULATION DEPENDANT
		TYPE_BUDGET = 0;
		SCENARIO = 6;
		CBDS = new int[]{1,2};
		
		/////////////////////////////////////
		NUM_CBDS = 0;
		for(int i=0;i<CBDS.length;i++)
			NUM_CBDS += CBDS[i];
		
		/////////////////////////////////////
		if(NUM_CBDS==1)
			SCENARIO_NAME = "9";
		else
			SCENARIO_NAME = "10";
		
		switch(SCENARIO){
		case -1:
			SCENARIO_NAME = "6g";
			break;
		case 0:
			SCENARIO_NAME += "a";
			break;
		case 1:
			SCENARIO_NAME += "b";
			break;
		case 2:
			SCENARIO_NAME += "c";
			break;
		case 3:
			SCENARIO_NAME += "d";
			break;
		case 4:
			SCENARIO_NAME += "e";
			break;
		case 5:
			SCENARIO_NAME += "f";
			break;
		case 6:
			SCENARIO_NAME = "g";
			break;
		case 7:
			SCENARIO_NAME = "h";
			break;
		default:
			System.err.println("Error lattice checking prices");		
		}
		
		System.err.println("Scenario Name:" + SCENARIO_NAME + " Num CBDS:" + NUM_CBDS);
	}
	
	/**
	 * Constructor
	 */
	public Lattice(Context context, Schedule sche){
		this.context = context;
		this.sche = sche;
		
		this.city = new City[Lattice.NUM_CBDS];
		sche.schedule(city);
		for(int i=0;i<Lattice.NUM_CBDS;i++){
			city[i] = new City(this, i);
		}
		System.out.println( "City created OK (" + city.hashCode() + ")");
		greenArea = new GreenArea(this.context);		
		sche.schedule(greenArea);
		init();	
		System.out.println("Loading Urban Statistics");
		loadUrbanisticStatistics();
		System.out.println("Loading Density Statistics");
		loadDensityStatistics();
		System.out.println("Loading Bio Statistics");
		loadBioStatistics();
		this.priceRings = new ArrayList<double[]>();
	}
	
	/**
	 * Basic initialisation - put your feature addition code here! You can use
	 * addFeature( name, x, y ) to add a feature. Each cell can only have one feature in
	 * it. See examples below.
	 */
	public void init(){
		cellCreation();
		greenArea.initBioInfluence();
		System.out.println("Loading Non-Urban prices Statistics");
		greenArea.loadNonUrbanPricesStatistics();
		for(int i=0;i<Lattice.NUM_CBDS;i++){
			city[i].initCity();
		}
		initCells();
	}
	
	public void cellCreation(){
		// Create a new 2D grid 
		GridFactory factory =  GridFactoryFinder.createGridFactory( null );
		grid = factory.createGrid( "cityGrid", context, new GridBuilderParameters<Cell>( 
				new StrictBorders(), new SimpleGridAdder<Cell>(), false, Lattice.SIZE_LATTICE, Lattice.SIZE_LATTICE ) );
		try{
			for( int i = 0; i < Lattice.SIZE_LATTICE; i++ ){
				for( int j = 0; j < Lattice.SIZE_LATTICE; j++ ){
					Cell cell = new Cell( this, i, j, this.sche);
					context.add( cell );
					grid.getAdder().add( grid, cell );
					grid.moveTo( cell, i, j );
					this.greenArea.addCell(cell);
					//System.err.println("cell added to the greenArea");
					cell.setInitialState();
					cell.loadBioValue(i, j);
				}
			}		
		}catch(NullPointerException e1){
			e1.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void initCells(){
		try{
			for( Cell c:GreenArea.getNonUrbanCells()){
				c.setCity_ID(this.closestCity(c));
				c.calculateRing();
				c.updatePriceRuralArea();
			}				
		}catch(NullPointerException e1){
			e1.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateCellTypes(int tick){
		System.err.println("NonUrban Cells:" + this.getNonUrbanCells().size() + " urban:" + Lattice.getUrbanCells().size() +
				" Available:" + Lattice.getNumAvailable(tick) + " New:" + Lattice.getNumNew(tick) + " Old:" + Lattice.getNumOld(tick) +
				 " City:" + City.getTotalUrbanCells() + " Total:" + (Lattice.SIZE_LATTICE*Lattice.SIZE_LATTICE) );
		numRural[tick] = this.getNonUrbanCells().size();
		for(Cell c: Lattice.getUrbanCells()){
			if( CellState.AVAILABLE==c.getState()){
				Lattice.numAvailable[tick]++;
			}
			if( CellState.NEW==c.getState()){
				Lattice.numNew[tick]++;
			}
			if( CellState.OLD==c.getState()){
				Lattice.numOld[tick]++;
			}
		}
	}
	
	public static int[] getNumRural(){
		return numRural;
	}
	
	public static int[] getNumAvailable(){
		return numAvailable;
	}
	
	public static int[] getNumNew(){
		return numNew;
	}
	
	public static int[] getNumOld(){
		return numOld;
	}
	
	public static int getNumRural(int index){
		return numRural[index];
	}
	
	public static int getNumAvailable(int index){
		return numAvailable[index];
	}
	
	public static int getNumNew(int index){
		return numNew[index];
	}
	
	public static int getNumOld(int index){
		return numOld[index];
	}
	
	/**
	 * @author mv59
	 * Load in an array the population settled in the lattice
	 * (used to calculate the future fitness)
	 */
	public void loadDensityStatistics(){
		//int line = 0;
		statisticsDensity = new ArrayList<int[]>();
		try{	
			Scanner sc = new Scanner(new File("Density.txt"));
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					int[] row = new int[Lattice.SIZE_LATTICE*Lattice.SIZE_LATTICE + 1];
					String[] val = newline.split(" ");
					for(int i=0; i<val.length;i++){
						row[i] = Integer.parseInt(val[i]);
					}
					//System.out.println("Load the row: " + ++line + " with length: " + row.length);
					this.statisticsDensity.add(row);
					//System.err.println("Size of statistic density: " + this.statisticsDensity.size());
				}else{
					System.out.println("Line empty:" + newline);
				}
			}
			
			int lines = 1;
			int numElem = 0;
			System.out.println("Writing the content of statistics density");
			for(int[] a: this.statisticsDensity){
				//System.err.println("line list:" + lines);
				lines++;
				for(int i=0; i<a.length;i++){
					numElem++;
					//System.out.print(a[i] + " ");
				}
				//System.err.println("\nTotal number of elements:" + numElem);
				numElem = 0;
			}
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
	 * @author mv59
	 * Load in an array the cells protected in each tick
	 */
	public void loadUrbanisticStatistics(){
		//int line = 0;
		Scanner sc = null;
		statisticsUrbanised = new ArrayList<int[]>();
		try{			
			sc = new Scanner(new File("Urbanised.txt"));
		/*}catch (FileNotFoundException e) {
			System.err.println("Urbanised file not found. Loading test file");
		}
		try{
			if(sc == null)
				sc = new Scanner(new File("Test.txt"));*/
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					int[] row = new int[Lattice.SIZE_LATTICE*Lattice.SIZE_LATTICE + 1];
					String[] val = newline.split(" ");
					for(int i=0; i<val.length;i++){
						row[i] = Integer.parseInt(val[i]);
					}
					//System.out.println("Load the row: " + ++line + " with length: " + row.length);
					this.statisticsUrbanised.add(row);
					//System.err.println("Size of statistic urbanised: " + this.statisticsUrbanised.size());
				}else{
					//System.out.println("Line empty:" + newline);
				}
			}
			
			int lines = 1;
			int numElem = 0;
			System.out.println("Writing the content of statistics urbanised");
			for(int[] a: this.statisticsUrbanised){
				//System.err.println("line list:" + lines);
				lines++;
				for(int i=0; i<a.length;i++){
					numElem++;
					//System.out.print(a[i] + " ");
				}
				//System.err.println("\nTotal number of elements:" + numElem);
				numElem = 0;
			}
		}catch (FileNotFoundException e) {
			System.err.println("Failing openinig statistical gathering process");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Accessing wronly to the array");
			e.printStackTrace();
		}catch (Exception e){
			System.err.println("Other exception: statistics about urban areas");
			e.printStackTrace();	
		}
	}
	
	/**
	 * @author mv59
	 * Load in an array the population settled in the lattice
	 * (used to calculate the future fitness)
	 */
	public void loadBioStatistics(){
		//int line = 0;
		statisticsBio = new ArrayList<double[]>();
		try{	
			Scanner sc = new Scanner(new File("BioValue.txt"));
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					double[] row = new double[Lattice.SIZE_LATTICE*Lattice.SIZE_LATTICE + 1];
					String[] val = newline.split(" ");
					for(int i=0; i<val.length;i++){
						row[i] = Double.parseDouble(val[i]);
					}
					//System.out.println("Load the row: " + ++line + " with length: " + row.length);
					this.statisticsBio.add(row);
					//System.err.println("Size of statistic density: " + this.statisticsDensity.size());
				}else{
					System.out.println("Line empty:" + newline);
				}
			}
			
			int lines = 1;
			int numElem = 0;
			System.out.println("Writing the content of statistics bioValue");
			for(double[] a: this.statisticsBio){
				//System.err.println("line list:" + lines);
				lines++;
				for(int i=0; i<a.length;i++){
					numElem++;
					//System.out.print(a[i] + " ");
				}
				//System.err.println("\nTotal number of elements:" + numElem);
				numElem = 0;
			}
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the statistics about bioValues");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Accessing wronly to the array bioValue");
			e.printStackTrace();
		}catch (Exception e){
			System.err.println("Other exception: statistics about bioValues");
			e.printStackTrace();	
		}
	}
	
	//Statistics
	public void updateStatisticsUrbanised(int currentTick){
		try {			
			//File with information about cells urbanised
			boolean isNew = false;
			int[] newRow;
			//Check if we need a new element on the list
			if(getStatisticsUrbanised().size() >= currentTick){
				newRow = getStatisticsUrbanised(currentTick);
			}else{
				//We have to add a new element to the list
				newRow = new int[Lattice.SIZE_LATTICE * Lattice.SIZE_LATTICE +1];
				Arrays.fill(newRow, 0);
				isNew = true;
				//System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			newRow[0]++;
			for(Cell cell: getUrbanCells()){
				//int direction = ((cell.getLocation().getY()) * size)+ cell.getLocation().getX() + 1;
				int direction = ((cell.getLocation().getX()) * Lattice.SIZE_LATTICE)+ cell.getLocation().getY();
				//System.err.println("Cell " + cell + " direction:" + direction);
				//First value: number of times updated
				newRow[direction]++;
			}

			if(isNew){
				addStatisicsUrbanised(newRow);
			}else{
				setStatisicsUrbanised(currentTick, newRow);
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public void updateStatisticsDensity(int currentTick){
		try {			
			//File with information about cells urbanised
			boolean isNew = false;
			int[] newRow;
			//Check if we need a new element on the list
			if(getStatisticsDensity().size() >= currentTick){
				//
				newRow = getStatisticsDensity(currentTick);
			}else{
				//We have to add a new element (a lattice) to the list
				newRow = new int[Lattice.SIZE_LATTICE * Lattice.SIZE_LATTICE +1];
				isNew = true;
			}
			//Total number of updates
			newRow[0]++;
			for(Cell cell: getUrbanCells()){
				//int direction = ((cell.getLocation().getY()) * size)+cell.getLocation().getX() + 1;
				int direction = ((cell.getLocation().getX()) * Lattice.SIZE_LATTICE)+cell.getLocation().getY();

				if(newRow[direction]==0){
					newRow[direction] = cell.getPopulation();
				}else{
					newRow[direction] = (newRow[direction] + cell.getPopulation())/2;
				}
			}

			if(isNew){
				addStatisicsDensity(newRow);
			}else{
				setStatisicsDensity(currentTick, newRow);
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public void updateStatisticsNonUrbanPrices(int currentTick){
		try {			
			//File with information about cells urbanised
			boolean isNew = false;
			int[] newRow;
			//Check if we need a new element on the list
			if(getStatisticsNonUrbanPrices().size() >= currentTick){
				newRow = getStatisticsNonUrbanPrices(currentTick);
			}else{
				//We have to add a new element (a lattice) to the list
				newRow = new int[Lattice.SIZE_LATTICE * Lattice.SIZE_LATTICE +1];
				isNew = true;
			}
			//Total number of updates
			newRow[0]++;
			for(Cell cell: getNonUrbanCells()){
				int direction = ((cell.getLocation().getX()) * Lattice.SIZE_LATTICE)+cell.getLocation().getY();
				
				//cell.updatePriceRuralArea();
				if(newRow[direction]==0){
					newRow[direction] = cell.getPrice();
				}else{
					//int prevPrice = newRow[direction];
					int value = (newRow[direction] + cell.getPrice())/2;
					newRow[direction] = value;
					//System.err.print("Final:" + newRow[direction] + " before:" + prevPrice + " new:" + cell.getPrice());
				}
				//System.err.println(" end:" + newRow[direction]);
			}
			if(isNew){
				addStatisicsNonUrbanPrices(newRow);
			}else{
				setStatisicsNonUrbanPrices(currentTick, newRow);
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public void updateStatisticsBio(int currentTick){
		DecimalFormat numberFormat = new DecimalFormat("##.###");
		try {			
			//File with information about cells bio
			boolean isNew = false;
			double[] newRow;
			//Check if we need a new element on the list
			if(getStatisticsBio().size() >= currentTick){
				newRow = getStatisticsBio(currentTick);
			}else{
				//We have to add a new element to the list
				newRow = new double[Lattice.SIZE_LATTICE * Lattice.SIZE_LATTICE +1];
				Arrays.fill(newRow, 0.0);
				isNew = true;
			}
			newRow[0]++;
			for(int i=0;i<Lattice.SIZE_LATTICE;i++){
				for(int j=1;j<Lattice.SIZE_LATTICE;j++){
					int direction = (i * Lattice.SIZE_LATTICE)+ j;
					Cell c = City.getCell(i, j);
					if(c ==null){
						c = GreenArea.getCell(i, j);
						if(c ==null)
							System.err.println("NULL in statistical bio");
					}
					double value = c.getBioValue();
					if(newRow[direction] < 0.001){
						/*System.err.println("Prev:" + newRow[direction] + " new:" + value + " state:" + c.getStateString() + " [" + c.getLocation().getX() +
								"," + c.getLocation().getY() + "] tick:" + currentTick );*/
						newRow[direction] = value;		
					}else{
						newRow[direction] = Double.valueOf(numberFormat.format((newRow[direction] + value)/2));
					}
				}
			}
			if(isNew){
				addStatisicsBio(newRow);
			}else{
				setStatisicsBio(currentTick, newRow);
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public List<int[]> getStatisticsDensity(){
		return this.statisticsDensity;
	}
	
	public int[] getStatisticsDensity(int elem){
		int[] aux = null;
		try{
			aux = this.statisticsDensity.get(elem);
		}catch(IndexOutOfBoundsException e){
			System.err.println("GetStatistics:Element in Statistic Density does not exist");
			System.err.println("Trying to access elem:" + elem + " total elements:" + this.statisticsDensity.size());
		}
		return aux;
	}
	
	public double[] getStatisticsBio(int elem){
		double[] aux = null;
		try{
			aux = this.statisticsBio.get(elem);
		}catch(IndexOutOfBoundsException e){
			System.err.println("GetStatistics:Element in Statistic Bio does not exist");
			System.err.println("Trying to access elem:" + elem + " total elements:" + this.statisticsBio.size());
		}
		return aux;
	}
	
	public List<double[]> getStatisticsBio(){
		return this.statisticsBio;
	}
	
	public void setStatisicsDensity(int tick,  int[] newRow){
		try{
			this.statisticsDensity.set(tick, newRow);
		}catch(IndexOutOfBoundsException e){
			System.err.println("SetStatistics:Element in Statistic Density does not exist");
			System.err.println("Trying to access elem:" + tick + " total elements:" + this.statisticsDensity.size());
		}
	}
	
	public void setStatisicsBio(int tick,  double[] newRow){
		try{
			this.statisticsBio.set(tick, newRow);
		}catch(IndexOutOfBoundsException e){
			System.err.println("SetStatistics:Element in Statistic Bio does not exist");
			System.err.println("Trying to access elem:" + tick + " total elements:" + this.statisticsBio.size());
		}
	}
	
	public void addStatisicsDensity(int[] newRow){
		this.statisticsDensity.add(newRow);
	}
	
	public void addStatisicsBio(double[] newRow){
		this.statisticsBio.add(newRow);
	}
	
	public List<int[]> getStatisticsUrbanised(){
		return this.statisticsUrbanised;
	}
	
	public int getStatisticsDensitySize(){
		return this.statisticsDensity.size();
	}
	
	public int[] getStatisticsUrbanised(int elem){
		int[] aux = null;
		try{
			aux = this.statisticsUrbanised.get(elem);
		}catch(IndexOutOfBoundsException e){
			System.err.println("GetStatistics:Element in Statistic Urbanised does not exist");
			System.err.println("Trying to access elem:" + elem + " total elements:" + this.statisticsUrbanised.size());
		}
		return aux;
	}
	
	public void setStatisicsUrbanised(int tick,  int[] newRow){
		try{
			this.statisticsUrbanised.set(tick, newRow);
		}catch(IndexOutOfBoundsException e){
			System.err.println("SetStatistics:Element in Statistic Urbanised does not exist");
			System.err.println("Trying to access elem:" + tick + " total elements:" + this.statisticsUrbanised.size());
		}
	}
	
	public void addStatisicsUrbanised(int[] newRow){
		this.statisticsUrbanised.add(newRow);
	}
	
	public void addStatisicsNonUrbanPrices(int[] newRow){
		this.greenArea.addStatisicsNonUrbanPrices(newRow);
	}
	
	public int[] getStatisticsNonUrbanPrices(int elem){
		return this.greenArea.getStatisticsNonUrbanPrices(elem);
	}
	
	public List<int[]> getStatisticsNonUrbanPrices(){
		return this.greenArea.getStatisticsNonUrbanPrices();
	}
	
	public void setStatisicsNonUrbanPrices(int tick,  int[] newRow){
		this.greenArea.setStatisicsNonUrbanPrices(tick, newRow);
	}

	
	/**
	 * Evaluation function. Using some weighted combination of
	 * the type- density- and feature preference functions later in this file.
	 * 
	 * @param salary Salary of the resident
	 * @return selected cell
	 */
	public Cell evaluateCell(int salary, int greenPreference){
		Cell cell = null;
		int max = Integer.MIN_VALUE;

		int maxPopulation = 0, minPopulation = Integer.MAX_VALUE;
		for(Cell c: City.getUrbanCells()){
			if(c.isHabitable()){
				int ring = c.getRing();
				//System.out.println("Salary: " + salary + " Rent: " + c.getPrice() + " Transport: " + (ring * City.TRANSPORT_RATE));
				
				int solution = (salary - c.getPrice() - (ring * City.TRANSPORT_RATE));
				/*System.err.println("Solution: " + solution);
				System.err.println("Green Preference: " + this.greenSpacePreference);
				System.err.println("Distance: " + city.distanceTo(c, c.GREEN));
				System.err.println("Add green: " + (this.greenSpacePreference * city.distanceTo(c, c.GREEN)));*/
				
				if(max < solution && solution >= 0){
					max = solution;
					cell = c;
				}else{
					if(solution < 0){
						//System.out.println("Salary: " + salary + " Rent: " + c.getPrice() + " Transport: " + (ring * City.TRANSPORT_RATE));
					}
				}
				
				int pop = c.getPopulation();
				if(pop > maxPopulation){
					maxPopulation = pop;
				}
				if(pop < minPopulation){
					minPopulation = pop;
				}
			}
			this.setMaxPopulation(maxPopulation, c.getCity_ID());
			this.setMinPopulation(minPopulation, c.getCity_ID());
		}
		if(max < 0){
			//System.err.println("Salary of the agent " + this.hashCode() + "is " + max);
		}
		return cell;
	}
	
	public boolean checkDimensions(int x, int y){
		if(x < Lattice.SIZE_LATTICE && y < Lattice.SIZE_LATTICE && x >= 0 && y >= 0)
			return true;
		else
			return false;
	}
	
	public Cell urbanisedCell(int[] location){
		Cell cell = this.greenArea.getRemoveCell(location);
		City.addUrbanCell(cell);
		return cell;
	}
	
	public static List<Cell> getUrbanCells(){
		return City.getUrbanCells();
	}
	
	public static Cell getUrbanCell(int x , int y){
		return City.getCell(x,y);
	}
	
	public Grid<Cell> getGrid(){
		return this.grid;
	}
	
	//Wrap methods of city
	public int getCentreX(int ID){
		return city[ID].getCentreX();
	}
	
	public int getCentreY(int ID){
		return city[ID].getCentreY();
	}
	
	public static void incrementUrbanCells(int n){
		City.incrementUrbanCells(n);
	}
	
	public static boolean isCellsEmpty(){
		return City.isCellsEmpty();
	}
	
	public static void addEmptyCells(){
		City.addEmptyCells();
	}
	
	public static void removeEmptyCells(){
		City.removeEmptyCells();
	}
	
	public static int getNumEmptyCells(){
		return City.getNumEmptyCells();
	}
	
	public static int getTotalUrbanCells(){
		return City.getTotalUrbanCells();
	}
	
	public static int getMaxPopulated(){
		return City.getMaxPopulated();
	}
	
	public static int getMinPopulated(){
		return City.getMinPopulated();
	}

	public void setLastUrbanised(Cell cell, int ID){
		this.city[ID].setLastUrbanised(cell);
	}
	
	public Cell getLastUrbanised(int ID){
		return this.city[ID].getLastUrbanised();
	}
	
	public void setMaxPopulation(int maxPopulation, int ID){
		city[ID].setMaxPopulation(maxPopulation);
	}
	
	public void setMinPopulation(int minPopulation, int ID){
		city[ID].setMinPopulation(minPopulation);
	}
	
	public static int getMaxTransportCosts(){
		return City.getMaxTransportCosts();
	}
	
	public static int getMinTransportCosts(){
		return City.getMinTransportCosts();
	}
	
	public static void setMaxTransportCosts(int transportCost){
		City.setMaxTransportCosts(transportCost);
	}
	
	public static double getAvgUrbanPrice(int ring){
		return City.getAvgUrbanPrice(ring);
	}
	
	public int selectCity(Cell cell){
		Set<Integer> cityNeigh = new TreeSet<Integer>();
		int[] neigh = new int[3];
		Arrays.fill(neigh, 0);
		cityNeigh.add(0);
		cityNeigh.add(1);
		cityNeigh.add(2);
		
		for(Cell c: cell.getNeighbours()){
			if(c.isHabitable()){
				neigh[c.getCity_ID()]++;
			}
		}
		
		//System.err.println(Arrays.toString(neigh));
		
		if(neigh[0]>neigh[1]){
			cityNeigh.remove(1);
		}
		if(neigh[0]>neigh[2]){
			cityNeigh.remove(2);
		}
		if(neigh[1]>neigh[0]){
			cityNeigh.remove(0);
		}
		if(neigh[1]>neigh[2]){
			cityNeigh.remove(2);
		}
		if(neigh[2]>neigh[0]){
			cityNeigh.remove(0);
		}
		if(neigh[2]>neigh[1]){
			cityNeigh.remove(1);
		}
		for(Integer i: cityNeigh){
			return i;
		}
		return -1;
	}
	
	//Wrap methods of greenSpaces
	public void updateBioInfluence(){
		greenArea.updateBioInfluence();
	}

	public void decreaseNeigbourhoodBioValue(Cell c, int distance ){
		this.greenArea.decreaseNeigbourhoodBioValue(c, distance);
	}

	public static double getMaxGreenPrice(){
		return GreenArea.getMaxGreenPrice();
	}
	
	public static void setMaxGreenPrice(int price){
		GreenArea.setMaxGreenPrice(price);
	}
	
	public static double getMinGreenPrice(){
		return GreenArea.getMinGreenPrice();
	}
	
	public static void setMinGreenPrice(int price){
		GreenArea.setMinGreenPrice(price);
	}
	
	/*
	 * For gather data in demographics
	 */
	public double getAvgGreenPrice(){
		return greenArea.getAvgGreenPrice();
	}
	
	public void resetPrices(){
		greenArea.resetGreenPrices();
		City.resetUrbanPrices();
	}	
	
	public List<Cell> getNonUrbanCells(){
		return GreenArea.getNonUrbanCells();
	}

		
	public void addPriceRing(double[] newRow){
		//System.err.println(Arrays.toString(newRow));
		this.priceRings.add(newRow);
	}
	
	public ArrayList<double[]> getPriceRing(){
		return priceRings;
	}
	
	/**
	 * @author mv59
	 * Save a scenario in a file
	 */
	public void saveScenario(){
		try {
			FileWriter outFile = new FileWriter("Scenario" + System.currentTimeMillis() + ".txt");
			PrintWriter out = new PrintWriter(outFile);
			for(Cell c: GreenArea.getNonUrbanCells()){
				out.print(c.getBioValue() + " ");
			}
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Show population per CBD. Used in updateAggregate() to show more information
	 * 
	 * DON'T DELETE!!!!!!!!
	 */
	public String populationDistribution(){
		String population = "";
		for(City c:this.city){
			population += "City " + c.getCity_ID() + ":" + c.getPopulation() + " ";
		}
		population += " Total Population:" + City.getTotalPopulation();
		return population;
	}
	
	/**
	 * Show distance to the CBD
	 */
	public String populationDistance(){
		String population = "";
		for(City c:this.city){
			population += "City " + c.getCity_ID() + ":" + c.getDistancePopulation() + " ";
		}
		return population;
	}

	public int closestCity(Cell c){
		int minDistance = Integer.MAX_VALUE;
		int cmin = Integer.MAX_VALUE;
		for(City city:this.city){
			int d = Math.abs(c.getLocation().getX() - city.getCentreX()) + Math.abs(c.getLocation().getY() - city.getCentreY());
			if(minDistance>d){
				minDistance = d;
				cmin = city.getCity_ID();
			}
		}
		return cmin;
	}
}
