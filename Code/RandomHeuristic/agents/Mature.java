package gi.agents;

import gi.city.*;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

/**
 * 
 * Class inherited from agent. It is referred to mature agents
 *
 */

public class Mature extends Agent{
	/**
	 * Constant that represents the rate with which matured agents die
	 */
	public static double MORTALITY_RATE;
	
	/**
	 * Constant that represents the rate with which matured agents mate
	 */
	public static double BIRTH_RATE;
	
	public static double WAGE_FACTOR;
	
	static{
		MORTALITY_RATE = 0.005;
		BIRTH_RATE = 0.045;
		WAGE_FACTOR = 1.7;
	}
	
	/**
	 * Wage/Income of the agent from 1000 to 3000
	 */
	private int wage;
	/**
	 * Cost of transport to go to work
	 */
	private int costTransport;
	/**
	 * Cost of the rent
	 */
	private int costRent;
	/**
	 * Number of newborns
	 */
	private static int numNewborns;
	
	/**
	 * General constructor of mature
	 * @param model
	 */
	public Mature( Context context, Lattice lattice){
		super( context, lattice );
		wage = (int)(City.getAvgUrbanPrice()* WAGE_FACTOR);
	}
	
	/**
	 * Constructor that specify the type of the agent
	 * @param model
	 * @param type
	 */
	public Mature( Context context, Lattice lattice, AgentType type ){
		super( context, lattice, type);
	}
	
	/**
	 * Update the state of the agent: check if the agent dies, 
	 * check if it becomes old and if if it gives birth
	 */
	@ScheduledMethod( start=1,interval=1)
	public void update(){
		if( RandomHelper.nextDouble() < MORTALITY_RATE ){
			die();
		}
		else{
			age++;
			if( age > OLD_AGE ){
				becomeOld();
			}
			else
				if( RandomHelper.nextDouble() < BIRTH_RATE ) {
					giveBirth();
				}
		}
	}
	
	/**
	 * Change the agent from mature to old: Create a new old agent and remove the other one.
	 * Modified (13/04/10) Fix some errors
	 * @return The old agent
	 */
	public Old becomeOld(){
		Old old = new Old( context, lattice, (int)(wage*0.9), costRent);
		old.age = this.age;
		old.type = this.type;
		old.location = this.location;
		this.context.add(old);
		//System.out.println("Agent (" + this.hashCode() + " - "  + old.hashCode() + 
		//		") has become old in cell " + this.location );
		
		try{
			for(Agent a: this.children){
				old.children.add(a);
				((Young) a).setFather(old);
			}
			this.location.add(old);
			this.location.remove(this);
			removeFromContext();
			//Debug
			/*System.err.print("* ");
			System.out.println("Agents in the cell: " + old.location);
			Iterator<Agent> it = old.location.getAgents().iterator();
			int cont = 0;
			while (it.hasNext()) {
			    // Get element
				cont ++;
			    System.out.println( it.next().hashCode());
			}
			if(cont ==0){
				System.err.println("Error nobody in the set");
			}*/
		}catch(Exception e){
			System.err.println("Exception in becomeOld with agent: " + this.hashCode());
			e.printStackTrace();
		}
		return old;
	}
	
	/**
	 * The agent gives birth to a new agent
	 * 
	 * @return the new agent
	 */
	public Young giveBirth(){
		Young young = new Young( context, lattice );
		try{
			young.type = this.type;
			young.age = 0; 
			this.context.add( young );
			//System.out.println("Agent: " + this.hashCode() + 
			//		" has a child " + young.hashCode() + " in cell: " + this.location);
			young.location = location;
			this.children.add(young);
			young.setFather(this);
			numNewborns++;
			//young.location.add(young);
		}catch(Exception e){
			System.err.println("Exception when agent " + this.hashCode() 
					+ " in location " + this.location + " tryes to add an young agent");
			System.err.println("Father Location: " + this.location);
			e.printStackTrace();
		}
		return young;
	}
	
	@Override
	public String toString(){
		return "Agent Mature: " + this.hashCode();
	}
	
	public int getWage(){
		return wage;
	}
	
	public void setRent(int rent){
		costRent = rent;
	}
	
	public int getCostTransport(){
		return costTransport;
	}
	
	public int getCostRent(){
		return costRent;
	}
	
	public static int getNumNewborns(){
		return numNewborns;
	}
	
	public static void initialiseNumNewborns(){
		numNewborns = 0;
	}
}
