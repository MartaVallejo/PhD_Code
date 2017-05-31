package gi.city;

import gi.agents.Agent;
import gi.agents.Mature;
import gi.agents.Old;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;

public class Cell{
	/**
	 * 
	 * @author marta
	 *
	 */
	public class Location{
		private int xAxis;
		private int yAxis;
		
		public Location(int x, int y){
			this.xAxis = x;
			this.yAxis = y;
		}
		
		public int getX(){
			return this.xAxis;
		}
		
		public int getY(){
			return this.yAxis;
		}
	}
	/**
	 * A list of the cell's neighbours, currently the Moore neighbourhood, i.e. 8 cells
	 */
	private List<Cell> neighbours;
	/**
	 * The agents who are living in the cell
	 */
	private Set<Agent> agents;
	
	private int city_ID;
	private Lattice lattice;
	private Location location;

	/**
	 * The current state of the cell
	 */
	private CellState state;
	/**
	 * The updated state of the cell, which will be applied once all the cells have calculated their next states
	 */
	private CellState nextState;
	/**
	 * The number of ticks the cell has spent in the current state
	 */
	private int age;
	/**
	 * Distance from the centre of the cell
	 */
	private int ring;
	
	/**
	 * The probability of empty land with one or more developed neighbours being released for development
	 */
	public static double RELEASE_PROBABILITY;
	/**
	 * The probability of old development (i.e. age > REDEVELOPMENT_AGE) being redeveloped
	 */
	public static double REDEVELOPMENT_RATE;
	/**
	 * The age after which cells are considered for redevelopment
	 */
	public static int REDEVELOPMENT_AGE;
	/**
	 * The probability of build houses
	 */
	public static double DEVELOPMENT_PROBABILITY;
	/**
	 * The initial capacity of the cell
	 */
	public static int INITIAL_CAPACITY;
	/**
	 * Length of the neighbourhood affected by the degradation of the urbanization
	 */
	public static int SIZE_BIODEGRADATION;
	
	/**
	 * Parameter green space is considered to influence the price of a cell
	 */
	public static final int GREEN;
	
	/**
	 * BioValue lower limit of a forest
	 */
	public static final double FOREST_LIMIT;
	
	/**
	 * BioValue upper limit of a urban cell
	 */
	public static final double URBAN_LIMIT;
	
	/**
	 * Bio Value step that it is added or removed according to the 
	 * value of the neighbours
	 */
	public static double BIOVALUE_STEP;
	
	/**
	 * Prices of the non-urban cells
	 */
	public static int FOREST_CELL;
	public static int AGRICULTURE_CELL;
	
	/**
	 * Change rate applied to the non-urban price
	 */
	public static double CHANGE_RATE;
	
	/**
	 * Maximum times undemanded
	 */
	public static int MAX_TIMES_UNDEMANDED;
	
	/**
	 * Price low step that it is applied to the urban prices
	 */
	public static double URBAN_UNDEMANDED_PRICE_STEP;	
	public static double URBAN_DEMANDED_PRICE_STEP;
	public static double URBAN_LESS_POPULATION_PRICE_STEP;

	static{
		RELEASE_PROBABILITY = 0.1;
		REDEVELOPMENT_RATE = 0.2;
		REDEVELOPMENT_AGE = 30;
		DEVELOPMENT_PROBABILITY = 0.3;

		INITIAL_CAPACITY = 1;
		SIZE_BIODEGRADATION = 3;
		
		GREEN = 1;
		
		FOREST_CELL = 66000;
		AGRICULTURE_CELL = 30000;
		FOREST_LIMIT = 0.7;
		URBAN_LIMIT = 0.3;
		BIOVALUE_STEP = 0.02;
		CHANGE_RATE = 0.8;
		MAX_TIMES_UNDEMANDED = 3;
		URBAN_UNDEMANDED_PRICE_STEP = 0.99;
		URBAN_DEMANDED_PRICE_STEP = 1.005;
		URBAN_LESS_POPULATION_PRICE_STEP = 0.999;
	}
	
	/**
	 * For future use, when more than one mature agent can live
	 * in the same cell
	 */
	private int capacity;
	/**
	 * Biological value of the cell
	 */
	private double bioValue;
	/**
	 * Biological value of a cell due to its neighbourhood
	 */
	private double bioNeighbourValue;
	/**
	 * Mean rent of the area
	 */
	private int price;
	
	/**
	 * Demand over the cell
	 */
	private int timesUndemanded;
	
	/**
	 * Control if in the cell has been demand fluctuations in the time step
	 */
	private boolean undemanded;
	
	Schedule sche;
	
	/**
	 * Constructor of the cell
	 * @param context
	 * @param city
	 */
	public Cell(Lattice lattice, int i, int j, Schedule sche){
		age = 0;
		agents = new HashSet<Agent>(10);
		this.lattice = lattice;
		this.location = new Location(i, j);
		capacity = INITIAL_CAPACITY;
		bioValue = Math.abs((double)(long)(RandomHelper.nextDouble()*100));
		bioValue/=100;
		if(bioValue>1)
			System.err.println("BioValue > 1");
		bioNeighbourValue=0;
		neighbours = new ArrayList<Cell>();
		undemanded = false;
		this.sche = sche;
	}
	
	/**
	 * @author mv59
	 * Load a determined scenario
	 * @param parameter to load in this cell
	 */
	public void loadBioValue(int x, int y){	
		int i = 0;
		int j = 0;
		boolean isFound = false;

		try{
			Scanner sc = new Scanner(new File("Scenario.txt"));
			while (sc.hasNextDouble() && !isFound) {
				if(j == Lattice.SIZE_LATTICE){
					j = 0;
					i++;
				}
				if(x==i && y==j){
					this.bioValue = sc.nextDouble();
					isFound = true;
				}else{
					sc.nextDouble();
				}
				j++;
			}
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the scenario");
			e.printStackTrace();
		};	
	}
	
	/**
	 * @author marta
	 * Determine the initial state of a cell. This can be protected
	 * if the biological value of the cell is high or empty
	 */
	public void setInitialState(){
		state = CellState.EMPTY;
		nextState = CellState.EMPTY;
	}
	
	public void updatePriceRuralArea(){		
		int priceBase =0;
		double priceRecentDevelopment = this.lattice.getLastUrbanised(this.city_ID).getRingPrice();

		if(this.isForest()){
			priceBase = FOREST_CELL;
		}else{
			priceBase = AGRICULTURE_CELL;
		}

		double distance = (ring - this.lattice.getLastUrbanised(this.city_ID).getRing())+1;
		if(distance<=0){
			distance = 1;
		}
		
		/*System.err.println("Distance:" + distance + " ring=" + this.getRing() + 
		" last urbanised=" + this.lattice.getLastUrbanised(this.city_ID));*/
		
		double futurePrice = 0;
		int priceA, priceB;
		switch(Lattice.SCENARIO){
		case -1: //Scenario 6
			this.price = priceBase;
			break;
		case 0: //Scenario 9a
			Cell.CHANGE_RATE = 0.06;
			futurePrice = priceRecentDevelopment * 12;
			priceA = (int) Math.round(3*priceBase * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			priceB = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			this.price =  priceA + priceB;
			break;
		case 1: //Scenario 9b
			Cell.CHANGE_RATE = 0.8;
			futurePrice = priceRecentDevelopment * 40;
			priceA = (int) Math.round(priceBase * (1 - Math.pow(Math.E, -CHANGE_RATE*distance)));
			priceB = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			this.price =  priceA + priceB;
			break;
		case 2: //Scenario 9c
			Cell.CHANGE_RATE = 0.08;
			futurePrice = priceRecentDevelopment * 100;
			priceA = (int) Math.round(priceBase * (1 - Math.pow(Math.E, -CHANGE_RATE*distance)));
			priceB = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			this.price =  priceA + priceB;
			break;
		case 3: //Scenario 9d
			Cell.CHANGE_RATE = 0.06;
			futurePrice = priceRecentDevelopment * 12;
			priceA = (int) Math.round(priceBase * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			priceB = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE*distance)));
			price =  priceA + priceB;
			break;
		case 4: // Scenario 9e
			Cell.CHANGE_RATE = 0.18;
			futurePrice = priceRecentDevelopment * 12;
			this.price = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE * distance))) + priceBase/70;
			break;
		case 5: //Scenario 9f
			Cell.CHANGE_RATE = 0.15;
			futurePrice = priceRecentDevelopment * 40;
			this.price = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE * distance))) + priceBase/20;
			break;
		case 6: //Scenario 9g
			Cell.CHANGE_RATE = 0.15;
			futurePrice = priceRecentDevelopment * 100;
			this.price = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE * distance))) + priceBase/5;
			break;
		case 7: //Scenario 9h
			Cell.CHANGE_RATE = 0.18;
			futurePrice = priceRecentDevelopment * 200;
			this.price = (int) ((futurePrice) * (Math.pow(Math.E, -CHANGE_RATE * distance))) + priceBase/5;
			break;	
		default:
			System.err.println("Error assigning prices in Cell.");
		}
		
/*		System.err.println("Cell [" + this.location.xAxis + "," + this.location.yAxis
				+ "] Price recent:" + priceRecentDevelopment
				+ " Future Price:" + futurePrice
				+ " Pow:" + (Math.pow(Math.E, -CHANGE_RATE*25))
				+ " distance:" + distance
				+ " price base:" + priceBase
				+ " final price:" + this.price);*/
		
		if(this.price> Lattice.getMaxGreenPrice()){
			Lattice.setMaxGreenPrice(this.price);
		}
		if(this.price< Lattice.getMinGreenPrice()){
			Lattice.setMinGreenPrice(this.price);
		}
	}
		
	/**
	 * @author mv59
	 * uses the demand to modify the prices of the cells
	 */
	public void updatePriceUrbanArea(){
		if(undemanded){
			this.timesUndemanded++;
			if(this.timesUndemanded>MAX_TIMES_UNDEMANDED){
				price = (int)((double)price * URBAN_UNDEMANDED_PRICE_STEP);
				this.timesUndemanded=0;
			}
		}else{
			this.timesUndemanded=0;
		}
		
		if(price<City.getMinUrbanPrice()){
			City.setMinUrbanPrice(price);
		}	
		if(price>City.getMaxUrbanPrice()){
			City.setMaxUrbanPrice(price);
		}
	}
	
	/**
	 * @author marta
	 * @return Number of neighbours with high biological
	 * value
	 */
	private int numberForestNeighbours(){
		int count = 0;
		
		for(Cell c: neighbours){
			if(c.getBioValue() + c.getBioNeighbourValue()>FOREST_LIMIT){
				count++;
				//System.out.println("Neighbour " + c.location + " is protected");
			}
		}
		return count;
	}
	/**
	 * Rules which control the evolution of cells
	 * 
	 */
	@ScheduledMethod( start = 1, interval=1, priority=10 )
	public void update(){
		age++;
		switch ( state ){
		case EMPTY:
			if(nextState!=CellState.PROTECTED){
				if( getDevelopedNeighbours() > 0 && RandomHelper.nextDouble() < RELEASE_PROBABILITY ) {
					setState( CellState.AVAILABLE );
					Lattice.incrementUrbanCells(1);
					this.lattice.urbanisedCell(new int[]{this.location.getX(), this.location.getY()});
				}	
			}
			break;
			
		case AVAILABLE:
			price = 0;
			double prob = RandomHelper.nextDouble();
			
			if( DEVELOPMENT_PROBABILITY < prob && !Lattice.isCellsEmpty()){
				bioDegradeCell();
				build();
				Lattice.addEmptyCells();
				lattice.updateBioInfluence();
				lattice.setLastUrbanised(this, this.city_ID);
			}			
			break;
			
		case NEW:
			updatePriceUrbanArea();
			if( this.agents.size() > 0 ){
				setState(CellState.OLD);
				//System.out.println("Cell " + this.location + " Change to OLD");
			}
			break;
			
		case OLD:
			updatePriceUrbanArea();
			if( age >= REDEVELOPMENT_AGE)
				//if( RandomHelper.nextDouble() < lattice.getNeighbourhoodCapacityRatio() * REDEVELOPMENT_RATE ){ 
				if( RandomHelper.nextDouble() < REDEVELOPMENT_RATE ){
					/*System.out.println("!!!!Cell " + this.location + " is redeveloped");
					System.out.println("!!!!Cell has " + this.agents.size() + " agents living within");
					Iterator<Agent> it = agents.iterator();
					while (it.hasNext()) {
					    // Get element
					    System.out.println( it.next().hashCode());
					}*/
					//redevelop();
				}
			break;
		}		
	}
	
	/**
	 * it evicts all the residents (removing the 
	 * previous capacity), increases the new capacity and rebuilds
	 */
	private void redevelop(){
		try{
			evictResidents();
			build();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @author mv59
	 * When a cell is urbanized the range of bio values are restricted from 0 to 3
	 */
	private void bioDegradeCell(){
		if(this.getBioValue()> URBAN_LIMIT){
			this.setBioValue(URBAN_LIMIT);
		}	
		lattice.decreaseNeigbourhoodBioValue(this,SIZE_BIODEGRADATION );
	}
	
	/**
	 * Use this function to transition from AVAILABLE to NEW - it updates the city about the
	 * current housing capacity
	 */
	private void build(){
		setState(CellState.NEW );
		calculatePrice();
		if(this.ring*City.TRANSPORT_RATE> Lattice.getMaxTransportCosts()){
			Lattice.setMaxTransportCosts(this.ring*City.TRANSPORT_RATE);
		}
	}
	
	/**
	 * Returns the density of population in the cell. If the cell is not
	 * habitable of the capacity is zero, the method returns zero.
	 * @return density
	 */
	public double getCapacityRatio(){
		if( !isHabitable() ){
			//System.out.println( "Zero capacity because not habitable");
			return 0;
		}else{
			if(capacity == 0){
				//System.out.println( "Zero capacity for some reason");
				return 0;
			}
			return (double)this.agents.size() / capacity;
		}
	}
	
	/**
	 * Remove all the agents that are located in the cell.
	 */
	public void removeAll(){
		//Case Mature or Old with (perhaps) children
		//Modify if a cell can allocate more than one mature agent
		try{
			for(Agent a: agents){
				a.removeAllChildren();
				a.removeFromContext();
			}
			agents.clear();
		}catch(Exception e){
			System.out.println("Error in removing a child agent when the parent is dead");
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes all the agents from the Cell
	 */
	public void evictResidents(){
		//This looks slightly complicated - we can't modify the original agents array
		//while iterating over it, so we create a new array temporarily
		Cell newCell = null;
		for( Agent a : new ArrayList<Agent>( agents ) ){
			if(a instanceof Mature || a instanceof Old){
				newCell = a.findNewLocation();
				if(newCell == null)
					System.err.println( "**Error Evicting residents!");
			}else
				System.err.println("Error. Children in Cell list");
		}
		System.out.println("All agents are evicted and they found a new cell:" + newCell.getLocation());
		for( Agent a : new ArrayList<Agent>( agents ) ){
			try{
				a.moveOrEmigrate(newCell);
			}catch(Exception e){
				System.err.println("Exception in move or emigrate");
			}
		}
		agents.clear();
	}
	
	/**
	 * Returns the number of NEW or OLD neighbours the cell has
	 * @return
	 */
	public int getDevelopedNeighbours(){
		int num = 0;
		//System.out.println("Cell: (" + this.location.getX() + "," + this.location.getY() + ")");
		//this.createNeighbourhood();
		for( Cell c : neighbours ) {
			//System.out.println("---Cell Neighbour: (" + c.location.getX() + "," + c.location.getY() + ")");
			if( c.getState().equals( CellState.NEW ) || c.getState().equals( CellState.OLD ) ) 
				num++;
		}
		return num;
	}
	
	@ScheduledMethod( start = 1, interval=1, priority=6 )
	/**
	 * Update the state of the cell and initialise the age of the cell.
	 * Called when the city and the river is created.
	 */
	public void updateState(){
		if( nextState != state ){ 
			age = 0;
			state = nextState;
		}
	}
	
	@ScheduledMethod( start = 1, interval=1, priority = ScheduleParameters.LAST_PRIORITY )
	/**
	 * Undemanded for this time step equal to true
	 */
	public void undemanded(){
		undemanded = true;
	}
	
	/**
	 * If the cell hasn't a context, the context is retrieved.
	 * If the cell hasn't a grid, the grid is created.
	 * If the cell hasn't neighbours they are created.
	 * 
	 * Called every time that a AVAILABLE cell is checked.
	 */
	protected void createNeighbourhood(){
		if( neighbours.size() == 0 ){
			MooreQuery<Cell> query = new MooreQuery<Cell>( lattice.getGrid(), this, 1, 1 );
	
			for ( Cell c : query.query() ){
				neighbours.add( c );
			}
		}
	}
	
	/**
	 * @author marta
	 * Calculate the ring where the cell is located
	 */
	public void calculateRing(){
		int difX, difY;
		difX = Math.abs(lattice.getCentreX(this.city_ID) - this.getLocation().getX());
		difY = Math.abs(lattice.getCentreY(this.city_ID) - this.getLocation().getY());
		ring = Math.max(difX, difY);
	}
	
	//Modified to separate when it is check for space and when agents is added
	/**
	 * Add an agent to the cell and decrease the total capacity of the cell
	 * 1. When the agent becomes old
	 * 2. When the agent gets born
	 * 3. When the agent is settled.
	 * @param agent The agent to be added
	 * @throws a Exception if there is no capacity for the new agent
	 */
	public void add( Agent agent ){
		agents.add( agent );
	}
	
	/**
	 * Remove an agent from the list of agents of the cell
	 * @param a
	 * @throws Exception
	 */
	public void remove( Agent agent )throws Exception{
		boolean sucess;
		sucess = agents.remove( agent );
		if(!sucess){
			throw new Exception();
		}
	}
	
	/**
	 * Check if the cell is habitable. A cell is habitable is its state
	 * is NEW or OLD
	 * @return if it is habitable
	 */
	public boolean isHabitable(){
		return( state.equals( CellState.NEW ) || state.equals( CellState.OLD ));
	}
	
	/**
	 * Return the number of old agents allocated in the cell
	 * @return
	 */
	public int getNumOlds(){
		int num = 0;
		for( Agent a : agents) 
			if( a instanceof Old ) 
				num++;
		return num;
	}
	
	/**
	 * Return the number of mature agents allocated in the cell
	 * @return
	 */
	public int getNumMatures(){
		int num = 0;
		for( Agent a : agents) 
			if( a instanceof Mature ) 
				num++;
		return num;
	}

	/**
	 * Return the list of agents allocated in the cell
	 * @return
	 */
	public Set<Agent> getAgents(){
		return agents;
	}

	/**
	 * @author marta
	 * @return Recalculate the biological value of the
	 * neighbourhood of a cell
	 */
	public double cellBioValue(){
		double value = 0.0;
		try{
			createNeighbourhood();
			for( Cell cell : neighbours ) {
				if(cell.bioValue>FOREST_LIMIT){
					value += BIOVALUE_STEP;
				}
				if(cell.bioValue<URBAN_LIMIT){
					value -= BIOVALUE_STEP;
				}
			}
		}catch(Exception e){
			System.err.println("Neigbours: " + neighbours.size());
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * @author marta
	 * Increment the demand of a cell a 1%
	 */
	public void incrementDemand(){
		price = (int)((double)price * URBAN_DEMANDED_PRICE_STEP);
		this.undemanded=false;
		this.timesUndemanded = 0;
	}
	
	/**
	 * @author marta
	 * Decrement the demand of a cell a 0.01%
	 */
	public void decrementDemand(){
		price = (int)((double)price * URBAN_LESS_POPULATION_PRICE_STEP);
	}
	
	/**
	 * @author mv59
	 * @return the mean wage of the cell
	 */
	public double getMeanWage(){
		double sumWage = 0;
		for(Agent a: agents){
			if(a.isMature())
				sumWage += ((Mature)a).getWage();
			if(a.isOld())
				sumWage += ((Old)a).getPension();
		}
		return sumWage/agents.size();
	}
	
	public Location getLocation(){
		return location;
	}
	public int getAge(){
		return age;
	}
	
	public List<Cell> getNeighbours(){
		Collections.shuffle(neighbours, new SecureRandom());
		return neighbours;
	}
	
	/**
	 * Use this to set the state of the cell to a new state (e.g. setState(NEW) )
	 * @param state
	 */
	public void setState( CellState state ){
		nextState = state;
	}
	
	/**
	 * Return the state of the cell
	 * @return state
	 */
	public CellState getState(){
		return state;
	}
	
	public CellState getNextState(){
		return nextState;
	}
	
	/**
	 * Return the state of the cell as a String
	 * @return state
	 */
	public String getStateString(){
		return state.toString();
	}
	
	public boolean hasNoPopulation() { 
		if(agents.size()==0)
			return true;
		else
			return false; 
	}
	
	public boolean isAvailable() { return CellState.AVAILABLE.equals( state ); }
	public boolean isNew() { return CellState.NEW.equals( state ); }
	public boolean isOld() { return CellState.OLD.equals( state ); }
	public boolean isProtected(){ return CellState.PROTECTED.equals(state);}
	public boolean isEmpty(){ return CellState.EMPTY.equals(state);}
	
	/**
	 * @return The capacity of the cell
	 */
	public int getCapacity(){
		return capacity;
	}
	
	/**
	 * @author mv59
	 */
	public boolean hasLocation(int[] location){
		if(this.location.xAxis == location[0] && 
				this.location.yAxis == location[1])
			return true;
		else
			return false;
	}
	
	/**
	 * @author mv59
	 * Calculate the price of a URBAN cell & the modifications due to other external
	 * factors
	 */
	public void calculatePrice(){
		int prices = 0;
		int counterPrices = 0;

		for(Cell n:neighbours){
			if(n.isHabitable()){
				counterPrices++;
				prices += n.price;
			}
		}
		if(counterPrices==0){
			price = City.INIT_URBAN_PRICE;
		}else{
			price = prices/counterPrices;
		}
		
		if(price<City.getMinUrbanPrice()){
			City.setMinUrbanPrice(price);
		}	
		if(price>City.getMaxUrbanPrice()){
			City.setMaxUrbanPrice(price);
		}
	}

	/**
	 * 
	 * @return The population of the cell
	 */
	public int getPopulation(){
		return agents.size();
	}
	
	/**
	 * @author marta
	 * @return the biological value of the cell according to its
	 * neighbourhood
	 */
	public double getBioNeighbourValue(){
		return bioNeighbourValue;
	}
	
	/**
	 * @author marta
	 * @param Assign a new value in the biological value of the cell
	 */
	public void setBioValue(double value){
		this.bioValue = value;
	}
	/**
	 * @author marta
	 * @param assign a new value in the biological value of the
	 * cell related to its neighbourhood
	 */
	public void setBioNeighbourValue(double value){
		bioNeighbourValue = value;
	}
	
	/**
	 * 
	 * @return The intrinsic biological value of a cell
	 */
	public double getBioValue(){
		return this.bioValue;
	}
	
	/**
	 * @author mv59
	 * @return the price of the cell
	 */
	public int getPrice(){
		return price;
	}
	
	public double getRingPrice(){
		return Lattice.getAvgUrbanPrice(ring);
	}
	
	public int getRing(){
		return ring;
	}
	
	public void setPrice(int price){
		this.price= price;
	}
	
	public boolean isUndemanded(){
		return this.undemanded;
	}
	
	//Function used only for displays
	public int getTransportCosts(){
		/*System.err.println("Ring:" + ring + " cell [" + this.getLocation().xAxis + "," + this.getLocation().yAxis + "]" +
				"city:" + this.city_ID);*/
		return ring * City.TRANSPORT_RATE;
	}
	
	public String toString(){
		return "[" + this.getLocation().getX() + "," + this.getLocation().getY() + "]";
	}
	
	public boolean isForest(){
		double value = this.bioValue + this.bioNeighbourValue;
		if(value>FOREST_LIMIT && numberForestNeighbours()>1){
			return true;
		}else{
			return false;
		}
	}
	
	public int getCity_ID(){
		return this.city_ID;
	}
	
	public void setCity_ID(int id){
		this.city_ID = id;
	}
}
