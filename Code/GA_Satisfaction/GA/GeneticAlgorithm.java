package GA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
	private String id;
	private List<Individual> population;
	private Lattice lattice;
	public static int NUM_INDIVIDUAL;
	public static int SIZE_LATTICE;
	private int[] budget;
	private int numMutations;
	private double bestFitness;
	private double meanFitness;
	private double standardDeviation;
	private double worstFitness;
	private int generations;
	private long startTime;
	private static final int MAX_UNCHANGED_GENERATIONS;
	private static final int MAX_GENERATIONS;
	public static int SIMULATIONS;
	public static final char TYPE_SELECTION;
	public static final double SELECTION_PERCENTAGE;
	public static final int TYPE_FITNESS_TICKS;
	public static final int TYPE_FITNESS_AGENTS;
	public static final int TYPE_PRICES;
	public static final int TYPE_BUDGET;
	public static final String FEASIBLE;
	public static String SCENARIO;
	public static String distribution;
	
	//Criteria used to select green areas
	public static enum Criteria{SATISFACTION, ECOLOGICAL};
	public static Criteria CRITERIA;
	
	static{
		MAX_UNCHANGED_GENERATIONS = 200;
		SIZE_LATTICE = 50;
		//S: Stochastic, F: Fix
		TYPE_SELECTION = 'S';
		
		//SELECTION_PERCENTAGE initialisation
		if(TYPE_SELECTION == 'F')
			SELECTION_PERCENTAGE = 0;
		else
			SELECTION_PERCENTAGE = 8;
		
		//0 Distance, 1 Green Protection
		CRITERIA = Criteria.SATISFACTION;
		//CRITERIA = Criteria.ECOLOGICAL;
		
		if(CRITERIA == Criteria.SATISFACTION){
			MAX_GENERATIONS = 10000;
		}else{
			MAX_GENERATIONS = 15000;
		}
		
		//10:Accumulative, 21: Single - Init, 22: Single - End
		//21 & 22 WITHOUT FITNESS UNCERTAINTY!!!!!!!!
		TYPE_FITNESS_TICKS = 10;
		
		//1:Flat, 2:Weighted
		TYPE_FITNESS_AGENTS = 1;
		
		//F:feasible, I:infeasible
		FEASIBLE = "F";
		
		//If change from/to scenario 6 UPDATE THIS!!!!!!!
		//Prices: 0: Fixed (Scenario 6) 1: Variable
		TYPE_PRICES = 1;
		
		//Budget: 0: Random 1:PopulationFunction
		TYPE_BUDGET = 0;
	}
	
	public GeneticAlgorithm(int numIndividual, int simulations, String scenario,
			String distributionCBD){
		startTime = System.currentTimeMillis();
		int len = String.valueOf(System.currentTimeMillis()).length();
		this.id = String.valueOf(System.currentTimeMillis()).substring(len-4, len);
		//System.err.println("ID:" + this.id);
		generations = 0;
		int times = 0;
		numMutations = 0;
		bestFitness = 0;
		worstFitness = 0.0;
		SCENARIO = scenario;
		distribution = distributionCBD;
		
		System.out.println("GA Constructor");
		population = new ArrayList<Individual>();
		NUM_INDIVIDUAL = numIndividual;
		lattice = new Lattice();
		SIMULATIONS = simulations;
		this.budget = new int[simulations];
		readBudgetFile();
		createPopulation();

		int i = 0;
		
		while(times< GeneticAlgorithm.MAX_UNCHANGED_GENERATIONS && i<MAX_GENERATIONS){
			if(i%10 == 0){
				System.out.print(i + ".- ");
				System.out.println(printFitness());
			}
			i++;
			generations++;				
			if(!runGA())
				times++;
			else{
				times = 0;
			}
		}
		saveBest();	
		writeData();
		//checkDensityFile();
	}
	
	private void createPopulation(){
		for(int i=0;i<NUM_INDIVIDUAL;i++){
			try{
				Individual individual = new Individual(i, lattice, budget);
				population.add(individual);
			}catch(ArrayIndexOutOfBoundsException e){
				System.err.println("Population Size:" + population.size());
				e.printStackTrace();
			}
		}
		System.out.println("Population Created.");
	}
	
	private boolean runGA(){
		//Select the worst & the best individual
		int worst = -1;
		int best = -1;
		Random probability = new Random();
		for(int i = 0; i<NUM_INDIVIDUAL;i++){
			if(probability.nextInt(10)>6){
				if(worst == -1)
					worst = i;
				else{
					if(population.get(i).getFitness()<population.get(worst).getFitness()){
						worst = i;
					}
				}
			}
			if(probability.nextInt(10)>6){
				if(best == -1){
					best = i;
				}else{
					if(population.get(i).getFitness()>population.get(best).getFitness()){
						best = i;
					}
				}
			}
		}
		if(best==-1)
			best = probability.nextInt(this.population.size());
		if(worst==-1)
			worst = probability.nextInt(this.population.size());
		if(population.get(best).getFitness()<population.get(worst).getFitness()){
			int a = worst;
			worst = best;
			best = a;
		}
		//Copy & Mutate the best
		Individual mutateIndividual = new Individual(GeneticAlgorithm.NUM_INDIVIDUAL, lattice, population.get(best), budget);
		//System.out.println("Before mutated info:" + printIndividual(mutateIndividual));
		//System.out.println("Mutated " + printFitness(mutateIndividual));
		boolean isMutate = mutateIndividual.mutate(lattice);
		if(isMutate){
/*			try {			
				//File with information about cells protected
				FileWriter outFile2 = new FileWriter("Mutations.txt");
				PrintWriter out2 = new PrintWriter(outFile2);
				
				String data = "";
				data += "Individual:" + best + " mutate in:" + mutateIndividual.getLastMutate();
				out2.println(data);
				out2.close();
			}catch (IOException e){
				e.printStackTrace();
			}*/
/*			try {			
				//File with information about cells protected
				FileWriter outFile2 = new FileWriter("CheckIndividuals.txt");
				PrintWriter out2 = new PrintWriter(outFile2);
				
				int numIndiv = 0;
				String data = "";
				data += "Generation:" + this.generations + "\n";
				for(Individual indiv:population){
					numIndiv++;
					data += "Individual number:" + numIndiv + "\n";
					data +=checkIndividual(indiv);
				}
				data += "CHECK_MUTATION****************************************************\n";
				data += "Individual Mutate\n";
				data +=checkIndividual(mutateIndividual);
				out2.println(data);
				out2.close();
			}catch (IOException e){
				e.printStackTrace();
			}*/
		
			int code = population.get(worst).getCode();
			Lattice.deleteForbidden(code);
			this.population.set(worst, mutateIndividual);
			population.get(worst).setCode(code);
			population.get(worst).calculateIndividualFitness();
			for(Integer i:Lattice.getForbidden(NUM_INDIVIDUAL)){
				Lattice.setForbidden(code, i);
			}
			Lattice.deleteForbidden(NUM_INDIVIDUAL);
			
			numMutations++;
			return true;
		}else{
			Lattice.deleteForbidden(NUM_INDIVIDUAL);
			return false;
		}
	}
	
/*	private String checkIndividual(Individual i){
		String data = "";
		for(Selection sel: i.getSelection()){
			for(Cell c: sel.getCells()){
				data+="\t\tCell protected " + c + " price:" + c.getPrice() + "\n";
			}
		}
		//print the forbidden
		data+= "\tForbidden: " +  this.lattice.printForbidden(i.getCode());
		return data;
	}*/
	
	private void saveBest(){
		int best = 0;
		//Calculate the best
		for(int i = 1; i<NUM_INDIVIDUAL;i++){
			if(population.get(i).getFitness()>population.get(best).getFitness()){
				best = i;
			}
		}
		
		try{
			//File with information about cells protected
			String title = "Result_GA_" + Lattice.TOTAL_URBANISED + "_S" 
			+ GeneticAlgorithm.TYPE_FITNESS_TICKS
			+ GeneticAlgorithm.TYPE_FITNESS_AGENTS
			+ "_" + GeneticAlgorithm.TYPE_SELECTION;
			if(GeneticAlgorithm.TYPE_SELECTION == 'S'){
				title+= ((int)(GeneticAlgorithm.SELECTION_PERCENTAGE*10)) + "_" + FEASIBLE + "_";
			}else{
				title+= "_" + FEASIBLE + "_";
			}
			title+= this.id + ".txt";
			
			FileWriter outFile1 = new FileWriter(title);
			PrintWriter out1 = new PrintWriter(outFile1);
			
			//To copy each element of the list in a String
			for(Selection s:population.get(best).getSelection()){
				for(Cell c: s.getCells()){
					//System.out.println(s.getTick() + " " + c.getX() + " " + c.getY() + " " + c.getPrice());
					out1.println(s.getTick() + " " + c.getX() + " " + c.getY() + " " + c.getPrice());
				}
			}
			out1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/*	private void checkDensityFile(){
		try{
			//File with information about cells protected
			String title = "CheckDensity.txt";
			
			FileWriter outFile1 = new FileWriter(title);
			PrintWriter out1 = new PrintWriter(outFile1);
		
			out1.println(Arrays.toString(Lattice.getStatisticsDensity(200)));
			out1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

	
	private String printFitness(){
		String print = "";
		for(int i=0; i<NUM_INDIVIDUAL;i++){
			population.get(i).calculateIndividualFitness();
			print += "(" + i + " - " + ((int)population.get(i).getFitness()) + ") ";
		}
		return print;
	}
	
/*	private String printSizeSelection(){
		String print = "";
		int counter;
		for(int i=0; i<NUM_INDIVIDUAL;i++){
			counter = 0;
			print += "(" + i + " - " + population.get(i).getSelection().size();
			
			for(Selection s:population.get(i).getSelection()){
				for(Cell c: s.getCells()){
					counter++;
				}
			}
			
			print += " - " + counter + ") ";
		}
		return print;
	}*/
	
	
	private void readBudgetFile(){
		//TYPE_BUDGET=0 random
		BufferedReader br = null; 
		try {
			if(TYPE_BUDGET==0)
				br = new BufferedReader(new FileReader("Budget_R.txt"));
			else
				br = new BufferedReader(new FileReader("Budget_P.txt"));

			String newline = br.readLine();
			if (newline.compareTo("")!=0) {
				String[] val;
				if(TYPE_BUDGET==0)
					val = newline.split(" ");
				else
					val = newline.split(",");
				for(int i=0; i<val.length;i++){
					if(TYPE_BUDGET==0)
						budget[i] = Integer.parseInt(val[i]);
					else
						budget[i] = (int)Double.valueOf(val[i]).longValue();
					/*System.out.println("String:" + val[i] + " Double:" + Double.valueOf(val[i]).longValue()
							+ " int:" + (int)Double.valueOf(val[i]).longValue());*/
				}
			}else
				System.err.println("Budget file empty");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate the best and the worst, mean and SD fitness
	 */
	private void resultFitness(){
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		double accumulate = 0;
		for(int i=0; i<NUM_INDIVIDUAL;i++){
			double fit = population.get(i).getFitness();
			accumulate += fit;
			if(fit > max)
				max = fit;
			if(fit < min)
				min = fit;
		}
		this.bestFitness = max;
		this.worstFitness = min;
		this.meanFitness = (accumulate * 1.0)/GeneticAlgorithm.NUM_INDIVIDUAL;
		
		accumulate = 0;
		for(int i=0; i<NUM_INDIVIDUAL;i++){
			accumulate += Math.pow(population.get(i).getFitness() - this.meanFitness, 2);
		}
		this.standardDeviation = Math.pow(accumulate/NUM_INDIVIDUAL, 1/2);
	}
	
	public void writeData(){
		System.err.println("Saving files");
		DecimalFormat numberFormat = new DecimalFormat("#.000");
		resultFitness();
		try {	
			//File with information about cells protected
			FileWriter outFile = new FileWriter("../GA_DATA.txt", true);
			PrintWriter out = new PrintWriter(outFile);
			out.print("OFF_SAT ");
			out.print(GeneticAlgorithm.SIZE_LATTICE + " ");
			out.print(NUM_INDIVIDUAL + " ");
			out.print(SIMULATIONS + " ");
			out.print(Lattice.TOTAL_URBANISED + " ");
			out.print(generations + " ");
			out.print(numMutations + " ");
			out.print(numberFormat.format(this.worstFitness) + " ");
			out.print(numberFormat.format(this.bestFitness) + " ");
			out.print(numberFormat.format(this.meanFitness) + " ");
			out.print(numberFormat.format(this.standardDeviation) + " ");
			out.print(this.id + " ");
			out.print("S ");
			out.print(GeneticAlgorithm.TYPE_FITNESS_TICKS/10 + " ");
			out.print(GeneticAlgorithm.TYPE_FITNESS_TICKS%10 + " ");
			out.print(GeneticAlgorithm.TYPE_FITNESS_AGENTS + " ");
			out.print(GeneticAlgorithm.TYPE_SELECTION + " ");
			if(GeneticAlgorithm.TYPE_SELECTION=='S')
				out.print(((int)(GeneticAlgorithm.SELECTION_PERCENTAGE*10)) + " ");
			else
				out.print("0 ");
			out.print(GeneticAlgorithm.FEASIBLE + " ");
			out.print(GeneticAlgorithm.SCENARIO + " ");
			out.print(GeneticAlgorithm.distribution + " ");
			out.print(GeneticAlgorithm.TYPE_BUDGET + " ");
			out.print(System.currentTimeMillis() - startTime);
			out.println();
			out.close();
			
			System.err.println("Finish GA Data File");
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
