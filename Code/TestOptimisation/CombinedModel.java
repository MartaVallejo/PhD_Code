package gi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.Arrays;

import gi.agents.*;
import gi.city.*;
import repast.simphony.context.*;
import repast.simphony.random.RandomHelper;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.*;
import repast.simphony.engine.schedule.*;
import repast.simphony.parameter.Parameters;

public class CombinedModel extends DefaultContext implements ContextBuilder{
	Demographics demographics;
	Lattice lattice;
	Context context;
	public static CombinedModel instance = null;
	boolean updateParams = true;
	boolean writeRasters = true;
	boolean addedUI = false;
	
	//Time measurement
	private long startTime;

	/**
	 * Initialise the model
	 * @param context of the model
	 */
	public Context build( Context context ){
		startTime = System.currentTimeMillis();
		RandomHelper.setSeed((int) System.currentTimeMillis());
		this.context = context;
		instance = this;
		setupParameters();
		
		Schedule sche = new Schedule();
		//System.out.println( "Schedule actions - model: " + sche.getModelActionCount() + ", non model: " + 
				//sche.getActionCount() + " class: " + sche.getClass());
		RunState.getInstance().getScheduleRegistry().setModelSchedule( sche );
		context.clear();
		
		System.out.println( "Creating city... ");
		lattice = new Lattice(context, sche);
		
		demographics = new Demographics( this.context, lattice, sche );
		demographics.build();
		System.out.println( "Population created OK");
		sche.schedule( demographics );
		sche.schedule( lattice );
		sche.schedule( this );
		
		ScheduleParameters stop = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
		sche.schedule(stop, this, "endMethod");
		
		return context;
	}
	
	public void endMethod(){
		System.err.println("Saving files");
		int len = String.valueOf(System.currentTimeMillis()).length();
		String id = String.valueOf(System.currentTimeMillis()).substring(len-4, len);

		//Rename satisfaction
		File oldFile = new File("Satisfaction.txt");
		String name = "SatisfactionGA_" + Lattice.GA_ID + "_" + id + "_" + Lattice.TYPE_FITNESS;
		if(Lattice.TYPE_FITNESS.compareTo("S")==0){
			name += Lattice.TYPE_SATISFACTION_TICKS + Lattice.TYPE_SATISFACTION_AGENTS + "_" + Lattice.TYPE_SELECTION;
		}else{
			name += "_" + Lattice.TYPE_SELECTION;
		}
		if(Lattice.TYPE_SELECTION.compareTo("S")==0){
			name += Lattice.PERCENTAGE_STOCHASTIC;
		}
		name += "_" + Lattice.FEASIBILITY + ".txt";
		oldFile.renameTo(new File(name));
		
		try {			
			//File with information about cells protected
			FileWriter outFile2 = new FileWriter("Inconsistency.txt", true);
			PrintWriter out2 = new PrintWriter(outFile2);
		
			out2.print(Lattice.GA_ID + " ");
			out2.print(id + " ");
			out2.print(lattice.getInconsistency() + " ");
			out2.print(lattice.getLackBudget() + " ");
			out2.print(Lattice.NUM_STATISTICS + " ");
			out2.print(Lattice.TYPE_FITNESS + " ");
			if(Lattice.TYPE_FITNESS.compareTo("S")==0){
				out2.print( Lattice.TYPE_SATISFACTION_TICKS.substring(0,1) + " ");
				out2.print( Lattice.TYPE_SATISFACTION_TICKS.substring(1,2) + " ");
				out2.print( Lattice.TYPE_SATISFACTION_AGENTS + " ");
			}else{
				out2.print("0 0 0 ");
			}
			out2.print(Lattice.TYPE_SELECTION);
			out2.print(" " + Lattice.PERCENTAGE_STOCHASTIC);
			out2.print(" " + Lattice.FEASIBILITY);
			out2.print(" " + Lattice.SCENARIO_NAME);
			out2.print(" " + lattice.getCBDStrint());
			out2.print(" " + Lattice.SIZE_LATTICE);
			out2.print(" " + (System.currentTimeMillis() - startTime));
			out2.print(" " + Lattice.SCENARIO);
			out2.print(" " + Lattice.TYPE_BUDGET);
			out2.println();
			out2.close();
			System.err.println("Files Saved");	
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the parameters of the system in each lapse of time
	 */
	@ScheduledMethod( start = 1, interval=1, priority=ScheduleParameters.FIRST_PRIORITY )
	public void modelUpdate(){
		if( updateParams ) 
			setupParameters();
	}
	
	/**
	 * Setup parameters of the system
	 */
	private void setupParameters(){
		try{
			Parameters p = RunEnvironment.getInstance().getParameters();
			Cell.INITIAL_CAPACITY = (Integer) p.getValue( "initialCapacity" );
			Cell.RELEASE_PROBABILITY = (Double) p.getValue( "releaseProbability" );
			Cell.REDEVELOPMENT_RATE = (Double) p.getValue( "redevelopmentRate" );
			Cell.REDEVELOPMENT_AGE = (Integer) p.getValue( "redevelopmentAge" );
			
			Mature.MORTALITY_RATE = (Double) p.getValue( "matureMortality" );
			Mature.BIRTH_RATE = (Double) p.getValue( "birthRate" );
			Young.MORTALITY_RATE = (Double) p.getValue( "youngMortality" );
			Old.MORTALITY_RATE = (Double) p.getValue( "oldMortality" );
			Demographics.INITIAL_MATURES = (Integer) p.getValue( "initialPopulation" );
		} catch( Exception e ){
			System.err.println( "Missing parameter!");
			e.printStackTrace();
		}
	}

	public Demographics getDemographics(){
		return demographics;
	}

	public Context getContext(){
		return context;
	}

	public boolean isUpdateParams(){
		return updateParams;
	}

	public void setUpdateParams( boolean updateParams ){
		this.updateParams = updateParams;
	}
}
