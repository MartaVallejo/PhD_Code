package gi.agents;

import java.awt.Color;

import repast.simphony.random.RandomHelper;


/**
 * Allows the agents to be of different types. You can rename the types if you want, but be sure to use
 * Refactor->Rename to make sure that the name changes everywhere.
 * @author dmrust
 *
 */
public enum AgentType{
	A( Color.blue, 1 ),
	C( Color.orange, 2 );
	
	public final Color color;
	public final int id;
	
	AgentType(Color color, int id ){
		this.color = color;
		this.id = id;
	}

	public static AgentType getRandomType(){
		return values()[RandomHelper.nextIntFromTo( 0, values().length-1 )];
	}
}
