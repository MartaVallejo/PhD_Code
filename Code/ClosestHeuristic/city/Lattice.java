package gi.city;

import java.util.Arrays;
import java.util.List;
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
	public static int TYPE_CRITERIA;
	private GreenArea greenArea;
	public Context context;
	private Schedule sche;
	
	static{
		TOTAL_TICKS = 600;
		Parameters p = RunEnvironment.getInstance().getParameters();
		SIZE_LATTICE = (Integer) p.getValue( "worldSize" );
		
		//0: RANDOM DISTRIBUTION, 1: POPULATION DEPENDANT
		TYPE_BUDGET = 0;
		
		//0: SATISFACTION, 1: ECOLOGICAL
		TYPE_CRITERIA = 1;
		
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
		greenArea = new GreenArea(this.context, city);		
		sche.schedule(greenArea);
		init();	
	}
	
	/**
	 * Basic initialisation - put your feature addition code here! You can use
	 * addFeature( name, x, y ) to add a feature. Each cell can only have one feature in
	 * it. See examples below.
	 */
	public void init(){
		cellCreation();
		greenArea.initBioInfluence();
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
		int greenDistance = 0;
		
		int maxPopulation = 0, minPopulation = Integer.MAX_VALUE;
		for(Cell c: City.getUrbanCells()){
			if(c.isHabitable()){
				int ring = c.getRing();
				//System.out.println("Salary: " + salary + " Rent: " + c.getPrice() + " Transport: " + (ring * City.TRANSPORT_RATE));
				
				int solution = (salary - c.getPrice() - (ring * City.TRANSPORT_RATE));
				//Other factors: Green Space
				greenDistance = this.distanceTo(c, CellState.PROTECTED);
				if(greenDistance > 0 && greenDistance <= 3)
					solution = solution + (greenPreference * (4 - greenDistance));
				
				/*System.err.println("Solution: " + solution);
				System.err.println("Green Preference: " + this.greenSpacePreference);
				System.err.println("Distance: " + city.distanceTo(c, c.GREEN));
				System.err.println("Add green: " + (this.greenSpacePreference * city.distanceTo(c, c.GREEN)));*/
				
				if(max < solution && solution >= 0){
					max = solution;
					cell = c;
				}else{
					if(solution < 0){
						System.out.println("Salary: " + salary + " Rent: " + c.getPrice() + " Transport: " + (ring * City.TRANSPORT_RATE));
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
	
	/**
	 * @author mv59
	 * Calculate the distance of a cell to a certain characteristic of the lattice
	 * @param cell Cell from which we want to calculate the distance
	 * @param characteristic Characteristic to find the shortest distance to the cell
	 * 1.- Green Space
	 */	
	public int distanceTo(Cell cell, CellState state){
		switch(state){
			case PROTECTED:
				return this.greenArea.distanceProtected(cell);
			case NEW:
			case OLD:
				return City.distanceUrban(cell);
			default:
				System.err.println("Error in method distanceTo");	
		}
		return 0;
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
	
	public int searchGreenSpaceS(int annualBudget, int tick){
		return this.greenArea.searchGreenSpaceS(annualBudget, tick);
	}
	
	public int searchGreenSpaceP(int annualBudget, int tick){
		return this.greenArea.searchGreenSpaceP(annualBudget, tick);
	}
	
	public static int getTotalProtectedCells(){
		return GreenArea.getTotalProtectedCells();
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
	
	public int getTotalSatisfaction2(){
		int satisfaction = 0;
		for(Cell cg:GreenArea.getProtectedCells()){
			for(Cell cu: City.getUrbanCells()){
				int distance = Math.abs(cg.getLocation().getX() - cu.getLocation().getX())
				+ Math.abs(cg.getLocation().getY() - cu.getLocation().getY());
				if(distance <=3){
					satisfaction += distance * cu.getPopulation();
				}
			}
		}
		return satisfaction;
	}
	
	public int getTotalSatisfaction3(){
		int fitness = 0;
		for(Cell cg:GreenArea.getProtectedCells()){
			for(Cell c: cg.getNeighbours()){
				if(c.isProtected())
					fitness+=2;
			}
			//System.err.println("FitnessN:" + fitness + " Cell:" + cell.getBioValue());
			fitness += (cg.getBioValue()*10);
		}
		return fitness;
	}

	public static String getProtectedCellsString(){
		return Arrays.toString(GreenArea.getProtectedCells().toArray());
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
	
	public double getAvgCloseness(){
		return this.greenArea.getAvgCloseness();
	}
	
	public String getGreenAreaString(){
		return greenArea.getGreenAreaString();
	}
}
