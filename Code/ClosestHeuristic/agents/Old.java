package gi.agents;

import gi.city.*;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

/**
 * 
 * Class inherited from agent. It is referred to old agents
 *
 */
public class Old extends Agent
{
	/**
	 * Constant that represent the rate with which old agents die
	 */
	public static double MORTALITY_RATE = 0.01;
	public static int MAX_AGE = 120;
	
	/**
	 * Pension/Income of the agent from 1000 to 3000
	 */
	private int pension;
	/**
	 * Cost of the rent
	 */
	private int costRent;
	
	/**
	 * Constructor of an old agent
	 * @param model
	 */
	public Old( Context context, Lattice lattice, int pension, int costRent )
	{
		super( context, lattice );
		this.pension = pension;
	}
	
	/**
	 * Update the state of the agent: check if the agent dies, 
	 * in two different ways.
	 */
	@ScheduledMethod( start=1,interval=1)
	public void update()
	{
		age++;
		if( age > MAX_AGE ) 
			die();
		else
			if( RandomHelper.nextDouble() < MORTALITY_RATE ) 
				die();
		
	}

	/**
	 * You can change the way the density preferences work if you want
	 */
	/*public double getDensityPreference( Cell c )
	{
		if( c == null ) return 0;
		return 1-c.getNeighbourhoodDensity() / Cell.CURRENT_MAXIMUM_CAPACITY;
	}*/
	
	@Override
	public String toString(){
		return "Agent Old " + this.hashCode();
	}
	
	public int getPension(){
		return pension;
	}
	
	public int getCostRent(){
		return costRent;
	}
	
}
