package gi.agents;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

import gi.city.Cell;
import gi.city.City;
import gi.city.Lattice;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/**
 * Class demographic creates the initial population in the model
 *
 */
public class Demographics {
	Context context;
	Schedule schedule;
	Lattice lattice;
	Municipality municipality;
	
	/**
	 * Maximum value of the migration
	 */
	public final static int MAX_MIGRATION;
	/**
	 * Migration rate
	 */
	public final static double MIGRATION_RATE;
	/**
	 * Immigration to the city
	 */
	private int immigration;
	/**
	 * Initial population
	 */
	private int initialMatures;
	/**
	 * Maximum immigration in each step
	 */
	public static int INITIAL_MATURES;
	
	//Parameters related to the satisfaction of the agents in terms of green spaces
	/**
	 * Sum of the satisfaction of the population
	 */
	private int totalSatisfaction;
	
	static{
		INITIAL_MATURES = 5;
		MAX_MIGRATION = 1000;
		MIGRATION_RATE = 0.029;
	}
	
	/**
	 * Constructor of demographics
	 * @param model
	 */
	public Demographics( Context context, Lattice lattice, Schedule sche ){
		this.context = context;
		this.totalSatisfaction = 0;
		this.schedule = sche;
		this.municipality = new Municipality(lattice, sche);
		sche.schedule(municipality);
		this.lattice = lattice;
	}
	
	/**
	 * Add mature agents as initial population in the model
	 * Add agent to the context and then move or emigrate
	 */
	public void addMature(){
		Agent mature = new Mature( context, lattice );
		mature.setAge( RandomHelper.nextIntFromTo( Agent.MATURATION_AGE, Agent.OLD_AGE-1 ) );
		
		//System.out.println( "Adding agent " + agent.hashCode() + " to context " + context.hashCode());
		context.add( mature );
		mature.moveOrEmigrate();
	}
	
	/**
	 * Assign the initial parameters related to the initial population
	 * to the model
	 * @param immigration
	 * @param initialPop
	 */
	public void initParams(int initialPop){
		INITIAL_MATURES = initialPop;
	}

	/**
	 * Create the initial population and assign the context to the model
	 * @param context
	 */
	public void build(){	
		initialMatures = 1 +(new Random().nextInt(INITIAL_MATURES - 1));
		
		for( int i = 0; i < initialMatures; i++ ){
			addMature();
		}		
	}
	
	/**
	 * Control the quantity of mature people coming from migration
	 * populate the city
	 */
	@ScheduledMethod(start=1,interval=1, priority = 3 )
	public void migration(){
		immigration = (int) Math.round((this.getTotalPopulation() * MIGRATION_RATE)+1);
		if(immigration>MAX_MIGRATION)
			immigration = MAX_MIGRATION;
		//System.out.println("Immigrants = " + immigration);
		
		for( int i = 0; i < immigration; i++ ){
			addMature();
		}
	}
	
	/**
	 * Retrieve the population of the context, divided into the different types
	 * and show the result
	 */
	@ScheduledMethod(start=1,interval=1,priority=ScheduleParameters.LAST_PRIORITY )
	public void updateAggregate(){
		int currentTick = (int)schedule.getTickCount()-1; //Starts in one
		//Stop the simulator
		if (schedule != null && currentTick == Lattice.TOTAL_TICKS){
			schedule.executeEndActions();
			System.out.println("Terminating simulation.");
			RunEnvironment.getInstance().endRun();
		}else{			
			this.lattice.searchGreenSpace(currentTick, municipality);
			System.out.println(Lattice.getProtectedCellsString());
			
			System.out.println( "**************************************" );
			writeSatisfaction(currentTick);
			lattice.resetPrices();
		}
	}
	
	private void writeSatisfaction(int currentTick){
		DecimalFormat numberFormat = new DecimalFormat("#0.###");
		try {
			//File with information about satisfaction
			FileWriter outFile = new FileWriter("Satisfaction.txt", true);
			PrintWriter out = new PrintWriter(outFile);

			out.print(this.getTotalPopulation() + " "); //1
			totalSatisfaction2();
			out.print(totalSatisfaction + " ");//2
			totalSatisfaction();
			out.print(totalSatisfaction + " ");//3
			totalSatisfaction3();
			out.print(totalSatisfaction + " ");//4
			out.print(Lattice.getTotalUrbanCells() + " ");//5
			out.print(Lattice.getTotalProtectedCells() + " ");//6
			out.print(Lattice.getMinGreenPrice() + " ");//7
			out.print(Lattice.getMaxGreenPrice() + " ");//8
			out.print(numberFormat.format(lattice.getAvgGreenPrice()) + " ");//9
			out.print(City.getMinUrbanPrice() + " ");//10
			out.print(City.getMaxUrbanPrice() + " ");//11
			out.print(numberFormat.format(City.getAvgUrbanPrice()) + " ");//12
			out.print(Lattice.getMinPopulated() + " ");//13
			out.print(Lattice.getMaxPopulated() + " ");//14
			out.print(immigration + " ");//15
			out.print(numberFormat.format(lattice.getAvgCloseness()) + " ");//16
			out.print(lattice.getGreenAreaString() + " ");//17
			if(currentTick==0)
				out.println(0);//18
			else
				out.println(municipality.getAccumulateBudget(currentTick));//18
			out.close();
		}catch (IOException e){
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate satisfaction of all population
	 */
	public void totalSatisfaction(){
		int minDistance = Integer.MAX_VALUE;
		String address = "";
		totalSatisfaction = 0;
		for( Object o : context.getAgentLayer( Agent.class ) ){
			Agent a = (Agent)o;
			totalSatisfaction += a.getGreenSatisfaction();
			int min = a.getMinGreenDistance();
			if(min<minDistance){
				minDistance = min;
				address = a.getLocation().toString();
			}
		}
		System.out.println("Satisfaction_1:" + totalSatisfaction + " min distance:" + minDistance + " address:" + address);
	}
	
	public void totalSatisfaction2(){
		totalSatisfaction = this.lattice.getTotalSatisfaction2();
		System.out.println("Satisfaction_2:" + totalSatisfaction);
	}
	
	//Clustering of green spaces & ecological value maximisation
	public void totalSatisfaction3(){
		totalSatisfaction = this.lattice.getTotalSatisfaction3();
		System.out.println("Satisfaction_3:" + totalSatisfaction);
	}
	
	/**
	 * Calculate the mean of the spare money of the population
	 */
	public double getMeanMoney(){
		double mean = 0f;
		int population = 0;
		//Count matures
		for(Object a:context.getObjects( Mature.class )){
			//System.err.println("Agent " + a.hashCode() + " with wage=" + ((Mature)a).getWage());
			mean += ((Mature)a).getWage() - ((Mature)a).getCostRent() - ((Mature)a).getCostTransport();
			population++;
		}
		
		for(Object a:context.getObjects( Old.class )){
			//System.err.println("Agent " + a.hashCode() + " with pension=" +  ((Old)a).getPension());
			mean += ((Old)a).getPension() - ((Old)a).getCostRent();
			population++;
		}
		if(population == 0){
			return 0;
		}
		return mean/population;
	}
	
	public int getTotalPopulation(){
		return context.getObjects( Agent.class ).size();
	}
	
	/**
	 * Return the number of mature agents in the model
	 * @return the number of mature agents
	 */
	public int getTotalMatures(){
		return context.getObjects( Mature.class ).size();
	}
	/**
	 * Return the number of old agents in the model
	 * @return the number of old agents
	 */
	public int getTotalOlds(){
		return context.getObjects( Old.class ).size();
	}
	/**
	 * Return the number of old agents in the model
	 * @return the number of old agents
	 */
	public int getTotalYoung(){
		return context.getObjects( Young.class ).size();
	}
}
