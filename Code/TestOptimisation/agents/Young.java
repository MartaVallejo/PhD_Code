package gi.agents;

import gi.city.*;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.context.Context;

/**
 * 
 * Class inherited from agent. It is referred to young agents
 *
 */
public class Young extends Agent
{
	public static double MORTALITY_RATE = 0.01;
	private Agent father;
	
	/**
	 * Constructor of a young agent
	 * @param model
	 */
	public Young( Context context, Lattice lattice )
	{
		super( context, lattice );
	}
	
	/**
	 * Update of the state of the agent in each time scheduled
	 */
	@ScheduledMethod( start=1,interval=1)
	public void update()
	{
		if( RandomHelper.nextDouble() < MORTALITY_RATE ) 
			die();
		else{
			age++;
			if( age > MATURATION_AGE ) 
				mature();
		}
		
	}
	
	/**
	 * The agent becomes mature. The young agent is destroy and a new
	 * mature agent is created.
	 * @return The mature agent
	 */
	public Mature mature()
	{
		Mature mature = new Mature( context, lattice );
		mature.age = this.age;
		mature.type = this.type;
		mature.context.add(mature);
		/*System.out.println("Agent: " + this.hashCode() + " become mature: " + 
				mature.hashCode() + " in the cell: " + this.location.getLocation());*/
		
		mature.moveOrEmigrate();
		try{
			releaseCell();
			removeFromContext();
		}catch(Exception e){
			System.out.println("Error changing from young to mature. Agent: " + 
					this.hashCode() + "in cell: " + this.location.getLocation());
		}
		return mature;
	}
	
	public void setFather(Agent agent){
		this.father = agent;
	}
	
	public Agent getFather(){
		return this.father;
	}
	
	@Override
	public String toString(){
		return "Agent Young" + this.hashCode();
	}
}
