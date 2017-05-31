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

/**
 * Class demographic creates the initial population in the model
 *
 */
public class Demographics {
	Context context;
	Schedule schedule;
	Lattice lattice;
	
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
	
	private static int[] totalYoung;
	private static int[] totalMature;
	private static int[] totalOld;
	private static double[] avgSalary;
	
	static{
		INITIAL_MATURES = 5;
		MAX_MIGRATION = 1000;
		MIGRATION_RATE = 0.029;
		
		totalYoung = new int[Lattice.TOTAL_TICKS];
		totalMature = new int[Lattice.TOTAL_TICKS];
		totalOld = new int[Lattice.TOTAL_TICKS];
		avgSalary = new double[Lattice.TOTAL_TICKS];
	}
	
	/**
	 * Constructor of demographics
	 * @param model
	 */
	public Demographics( Context context, Lattice lattice, Schedule sche ){
		this.context = context;
		this.schedule = sche;
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
			System.out.println("Tick of the system: " + currentTick);
			System.out.println( "**************************************" );
			//System.out.println( lattice.populationDistribution());
			//System.out.println( lattice.populationDistance());
			
			lattice.updateStatisticsUrbanised(currentTick);
			lattice.updateStatisticsDensity(currentTick);
			lattice.updateStatisticsNonUrbanPrices(currentTick);
			lattice.updateStatisticsBio(currentTick);
			lattice.updateCellTypes(currentTick);
			updatePopulationTypes(currentTick);
			
			gatherPricesRings();
			gatherSalaryAvg();
		}
	}
	
	private void updatePopulationTypes(int currentTick){
		Demographics.totalMature[currentTick] = this.getTotalMatures();
		Demographics.totalOld[currentTick] = this.getTotalOlds();
		Demographics.totalYoung[currentTick] = this.getTotalYoung();
	}
	
	public void gatherPricesRings(){
		try{
			double[] ringPrices = new double[City.NUMBER_RINGS+1];
			for(Cell cell: lattice.getNonUrbanCells()){
				if(ringPrices[cell.getRing()] == 0.0){
					ringPrices[cell.getRing()] = cell.getPrice();
				}else{
					ringPrices[cell.getRing()] = (double)Math.round(((ringPrices[cell.getRing()] + cell.getPrice())/2)*1000)/1000;
				}
			}
			lattice.addPriceRing(ringPrices);
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Error in array with ringPrices. Size:" + City.NUMBER_RINGS + " index:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void gatherSalaryAvg(){
		double mean = 0;
		int population = 0;
		int currentTick = (int)schedule.getTickCount()-1; //Starts in one
		for(Object a:context.getObjects( Mature.class )){
			//System.err.println("Agent " + a.hashCode() + " with wage=" + ((Mature)a).getWage());
			mean += ((Mature)a).getWage() - ((Mature)a).getCostRent() - ((Mature)a).getCostTransport();
			population++;
		}
		Demographics.avgSalary[currentTick] = (mean/population);
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
	
	public static int[] getTotalMatureArray(){
		return Demographics.totalMature;
	}
	
	public static int[] getTotalOldArray(){
		return Demographics.totalOld;
	}
	
	public static int[] getTotalYoungArray(){
		return Demographics.totalYoung;
	}
	
	public static double[] getSalaryAvg(){
		return Demographics.avgSalary;
	}
}
