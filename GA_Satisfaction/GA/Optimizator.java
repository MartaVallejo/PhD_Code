package GA;

/**
 * Program to run the genetic algorithm approach to optimise
 * the selection of green spaces in Repast Simphony
 * @author mv59
 *
 */
public class Optimizator {
	/**
	 * @author mv59
	 * @param args[0] Number of individuals in the population
	 * @param args[1] Number of simulations
	 * @param args[2] Scenario
	 * @param args[3] CBDs distribution
	 */
	public static void main(String[] args) { 
		int numIndividual = 25;
		int simulations = 600;
		String scenario = "10b";
		String distributionCBD = "[1-2]";
		
		GeneticAlgorithm ga = new GeneticAlgorithm(numIndividual, simulations, scenario, distributionCBD);
	}
	
}
