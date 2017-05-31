package gi;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.io.File;

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

	/**
	 * Initialise the model
	 * @param context of the model
	 */
	public Context build( Context context ){
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
		//lattice.saveScenario();
		
		demographics = new Demographics( this.context, lattice, sche );
		demographics.build();
		System.out.println( "Population created OK");
		sche.schedule( demographics );
		sche.schedule( lattice );
		sche.schedule( this );
		
		ScheduleParameters stop = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
		sche.schedule(stop, this, "endMethod", lattice.getStatisticsUrbanised(), lattice.getStatisticsDensity(),
				lattice.getStatisticsNonUrbanPrices(), lattice.getPriceRing(), lattice.getStatisticsBio(), Lattice.getNumRural(), 
				Lattice.getNumAvailable(), Lattice.getNumNew(), Lattice.getNumOld(), Demographics.getTotalMatureArray(),
				Demographics.getTotalOldArray(), Demographics.getTotalYoungArray(), Demographics.getSalaryAvg());
		
		return context;
	}
	
	public void endMethod(List<int[]> statisticsU, List<int[]> statisticsD, List<int[]> statisticsP,
			ArrayList<double[]> arrayRings, List<double[]> statisticsB, int[] numRural, int[] numNew,
			 int[] numAvailable,  int[] numOld, int[] totalMature, int[] totalOld, int[] totalYoung, double[] salary){
		System.err.println("Saving files");
		try {			
			//File with information about cells protected
			FileWriter outFile2 = new FileWriter("Urbanised.txt");
			PrintWriter out2 = new PrintWriter(outFile2);
			
			//To copy each element of the list in a String
			for(int[] line: statisticsU){
				String row = "";
				for(int i=0; i< line.length; i++){
					row += line[i];
					if(i != line.length-1)
						row += " ";
				}
				out2.println(row);
			}
			out2.close();
			
			System.err.println("Finish Urbanised File");
			
			//File with information about cells protected
			FileWriter outFile1 = new FileWriter("Density.txt");
			PrintWriter out1 = new PrintWriter(outFile1);
			
			//To copy each element of the list in a String
			for(int[] line: statisticsD){
				String row = "";
				for(int i=0; i< line.length; i++){
					row += line[i];
					if(i != line.length-1)
						row += " ";
				}
				out1.println(row);
			}
			out1.close();
			
			System.err.println("Finish Density File");
			
			//File with information about non-urban prices
			FileWriter outFile0 = new FileWriter("NonUrbanPrices.txt");
			PrintWriter out0 = new PrintWriter(outFile0);
			
			//To copy each element of the list in a String
			for(int[] line: statisticsP){
				//System.err.println(Arrays.toString(line));
				String row = "";
				for(int i=0; i< line.length; i++){
					row += line[i];
					if(i != line.length-1)
						row += " ";
				}
				out0.println(row);
			}
			out0.close();
			
			System.err.println("Finish NonUrbanPrices File");
			
			//File with information about non-urban prices
			FileWriter outFile3 = new FileWriter("BioValue.txt");
			PrintWriter out3 = new PrintWriter(outFile3);
			
			//To copy each element of the list in a String
			for(double[] line: statisticsB){
				//System.err.println(Arrays.toString(line));
				String row = ((int)line[0]) + " ";
				for(int i=1; i< line.length; i++){
					row += line[i];
					if(i != line.length-1)
						row += " ";
				}
				out3.println(row);
			}
			out3.close();
			
			System.err.println("Finish Bio Value File");
			
			//Rename files
			//Rename Budget file
			File oldFile = new File("Budget.txt");
			oldFile.renameTo(new File("Budget" + System.currentTimeMillis() + ".txt"));
						
			//File with information about cells protected
			FileWriter outFile4 = new FileWriter("Rings.txt");
			PrintWriter out4 = new PrintWriter(outFile4);
			
			//To copy each element of the list in a String
			for(double[] line: arrayRings){
				String row = "";
				for(int i=0; i< line.length; i++){
					row += line[i];
					if(i != line.length-1)
						row += " ";
				}
				out4.println(row);
			}
			out4.close();
			
			System.err.println("Finish Rings File");
			
			//File with information about cells protected
			FileWriter outFile5 = new FileWriter("Cells.txt");
			PrintWriter out5 = new PrintWriter(outFile5);
			
			//To copy each element of the list in a String
			for(int i=0; i<numNew.length;i++){
				String row = "2500 " + numRural[i] + " " + numAvailable[i] + " " + numNew[i] + " " + numOld[i]
					+ " " + totalOld[i] + " " + totalMature[i] + " " + totalYoung[i] + " " + salary[i];
				out5.println(row);
			}
			out5.close();
			
			System.err.println("Finish Cells File");
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
