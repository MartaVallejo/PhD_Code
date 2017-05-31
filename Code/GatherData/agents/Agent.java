package gi.agents;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import gi.city.Cell;
import gi.city.CellState;
import gi.city.City;
import gi.city.Lattice;

import repast.simphony.context.Context;

/**
 * Information related to each agents
 *
 */
public class Agent{
	protected Cell location;
	protected Context context;
	protected Lattice lattice;
	/**
	 * Children of this agent
	 */
	protected Set<Agent> children = new HashSet<Agent>(3);
	/**
	 * Type of agent: Blue, Orange
	 */
	protected AgentType type;
	
	/**
	 * Preference for green space. Range from 0 to 10
	 */
	private int greenSpacePreference;
	/**
	 * A constant that shows when an agent achieves the mature age
	 */
	public static int MATURATION_AGE;
	/**
	 * A constant that shows when an agent achieves an old age
	 */
	public static int OLD_AGE;
	/**
	 * The current age of the agent
	 */
	protected int age;
	
	static{
		MATURATION_AGE = 20;
		OLD_AGE = 50;
	}
	
	/**
	 * number of agents death in this tick
	 */
	private static int numDeaths;

	/**
	 * Generic constructor of the agent. The type is selected randomly.
	 * The agent is created calling the Specific constructor.
	 * 
	 * @param model The model where the agent is created
	 */
	public Agent( Context context, Lattice lattice){
		this( context, lattice, AgentType.getRandomType() );
		location = null;
		age = 1;
		greenSpacePreference = (int)(new Random().nextDouble()*10);
	}

	/**
	 * Specific constructor of the agent.
	 * 
	 * @param model The model where the agent is created
	 * @param type The determine type of the agent
	 */
	public Agent( Context context, Lattice lattice, AgentType type ){
		super();
		if(this.context==null)
			this.context = context;
		this.lattice = lattice;
		this.type = type;
	}

	/**
	 * Releasing of resources when an agent dies and remove from the cell
	 */
	public void die(){
		try{
			releaseCell();
			removeFromContext();
			numDeaths++;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * When an agent is create but it is not enough space for is
	 * the agent should emigrate. In the case there is space the
	 * agent is moved to it.
	 */
	public void moveOrEmigrate(){
		Cell cell = findNewLocation();
		//Try to move, otherwise, emigrate
		if( cell != null){
			moveTo( cell );
		}	 
	}
	
	/**
	 * When an agent is create but it is not enough space for is
	 * the agent should emigrate. In the case there is space the
	 * agent is moved to it.
	 * @author marta
	 * @param cell Cell where the agent is going to move
	 */
	public void moveOrEmigrate(Cell cell) throws Exception{
		//Try to move, otherwise, emigrate
		if( cell != null){
			moveTo( cell );
			//System.out.println("Agent " + this.hashCode() + " with age: " + this.age +
			//		" is settled in cell: " + this.location);
		}	 
		else{
			throw new Exception();
		}
	}

	/**
	 * Remove agent in two cases:
	 * 1. When the agent is created but there is no available space for it
	 * 2. When agent die
	 * 3. When an agent changes from young to mature
	 * 4. When an agent changes from mature to old.
	 */
	public void removeFromContext()throws Exception{
		if( this.context != null ){
			this.context.remove( this );
		}
		else{
			throw new Exception();
		}
	}

	/**
	 * @author mv59
	 * The method the agent from the cell. If it is a child, it is removed
	 * from the offspring of the father
	 * @throws Exception if the cell does not have a location
	 * before the cell is released.
	 */
	public void releaseCell()throws Exception{
		if( location != null ){
			if(this instanceof Young)
				//Remove himself from the list of the father
				((Young) this).getFather().children.remove(this);
			else{
				location.remove(this);
				removeAllChildren();
				//If the cell is empty we don't allow to transform new land in urban
				if(location.hasNoPopulation()){
					Lattice.addEmptyCells();
				}
				location.decrementDemand();
			}	
			location = null;
		}else{
			throw new Exception();
		}
	}
	
	/**
	 * Remove all the agents that are located in the cell.
	 */
	public void removeAllChildren(){
		//Case Mature or Old with (perhaps) children
		//Modify if a cell can allocate more than one mature agent
		try{
			for(Agent a: children){
				a.removeFromContext();
				a.context = null;
			}
			children.clear();
		}catch(Exception e){
			System.out.println("Error in removing a child agent when the parent is dead");
			e.printStackTrace();
		}
	}

	/**
	 * Move the agent to a determined cell
	 * <p>
	 * (Modify: Divide checking if the cell is free and add the agent to the cell.
	 * Adding the exception instead of a boolean variable)
	 * @param cell Cell that the agent wants to move
	 */
	public void moveTo( Cell cell ){
		try{
			if(this instanceof Young){
				location = cell;
				cell.add(this);
			}else{	
				cell.add(this);
				if( location != null ){
					location.remove( this );
				}
				location = cell;
			}
		}catch(Exception e){
			System.err.println("Error adding the agent to the new cell");
		}
	}
	
	/**
	 * @author mv59
	 * Find a new location:
	 * - If area = 1 then take this cell
	 * - else find a cell
	 */
	public Cell findNewLocation(){
		Cell cell = null;
	
		int salary = 0;
		if(this instanceof Mature){
			salary = ((Mature)this).getWage();
		}else{
			if(this instanceof Old){
				salary = ((Old)this).getPension();
			}else
				System.err.println("A child trying to find a flat on his own");
		}
		//Select the cell according to the salary
		cell = this.lattice.evaluateCell(salary, this.greenSpacePreference);
		//Increment the price of the cell
		if(cell == null){
			System.err.println("Agent " + this.hashCode() + " with a wage of: " 
					+ salary + " didn't find any available residence. Avg price:" + City.getAvgUrbanPrice() + " TC:" 
					+ Lattice.getMaxTransportCosts());
		}else{
			/*System.out.println("Agent with a wage of: " + salary + " is settle down in [" 
					+ cell + " at price: " + cell.getPrice());*/
			((Mature)this).setRent(cell.getPrice());
			cell.incrementDemand();
		}
		//If the cell is empty of agents remove CellsEmpty = true
		if(cell.hasNoPopulation()){
			//System.err.println("Cell " + cell + " is empty");
			//System.err.println("Num cells empty (Before) " + city.getNumEmptyCells());
			Lattice.removeEmptyCells();
			//System.err.println("Num cells empty (After) " + city.getNumEmptyCells());
		}
		return cell;
	}

	/**
	 * Return if the agent is mature
	 * @return
	 */
	public boolean isMature(){
		if(this instanceof Mature){
			return true;
		}
		return false;
	}
	
	/**
	 * Return if the agent is old
	 * @return
	 */
	public boolean isOld(){
		if(this instanceof Old){
			return true;
		}
		return false;
	}

	/**
	 * Assign the type to the agent
	 * @param type New type of the agent
	 */
	public void setType( AgentType type ){
		this.type = type;
	}

	/**
	 * Retrieve the type of the agent
	 * @return Type of the agent
	 */
	public AgentType getType(){
		return type;
	}
	
	/**
	 * Return the location of the agent
	 * @return the cell where the agent is located
	 */
	public Cell getLocation(){
		return location;
	}
	
	/**
	 * Set the age to the agent
	 * 
	 * @param age New age
	 */
	public void setAge( int age ){
		this.age = age;
	}

	/**
	 * Retrieve the age of the agent
	 * @return age of the agent
	 */
	public int getAge(){
		return this.age;
	}
	
	//Methods for Datasets & Charts
	/**
	 * Return the number of matures in the population
	 */
	public int getMatures(){
		return context.getObjects( Mature.class ).size();
	}
	
	/**
	 * Return the number of matures in the population
	 */
	public int getOlds(){
		return context.getObjects( Old.class ).size();
	}
	
	/**
	 * Return the number of matures in the population
	 */
	public int getYoungs(){
		return context.getObjects( Young.class ).size();
	}
	
	public String cityToString(){
		return this.lattice.toString();
	}
	
	public int getChildren(){
		return this.children.size();
	}
	
	public static int getNumDeaths(){
		return numDeaths;
	}
	
	public static void initialiseNumDeaths(){
		numDeaths=0;
	}
}
