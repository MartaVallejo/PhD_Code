package GA;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Class which creates each of the individuals of the GA
 * @author mv59
 *
 */
public class Individual {
	
	private List<Selection> selections;
	/**
	 * List of individuals in our population
	 */
	private int code;
	private int[] budget;
	private int[] remainBudget;
	private double fitness;
	private String lastMutate;
	private static int MAX_ATTEMPS;
	
	static{
		MAX_ATTEMPS = 1000;
	}
	
	/**
	 * @author marta
	 * @param simulations
	 * @param code
	 * @param city
	 * 
	 * Constructor for the individuals within the GA population
	 */
	public Individual(int code, Lattice city, final int[] budget){
		try{
			selections = new ArrayList<Selection>();
			lastMutate = "";
			this.budget = budget;
			this.remainBudget = new int[GeneticAlgorithm.SIMULATIONS];
			Arrays.fill(this.remainBudget, 0);
			this.code = code;
			populateSelections(city);
			calculateIndividualFitness();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}catch(NullPointerException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor by copy. Used in the mutation procedure to copy the best one of the 
	 * tournament selection
	 * 
	 * @author marta
	 * @param simulations
	 * @param code
	 * @param size
	 * @param city
	 * @param best best individual to copy
	 * @param budget array with the budget for the entire simulation
	 */
	public Individual(int code, Lattice city, Individual best, int[] budget){
		this.budget = budget;
		this.code = code;
		selections = new ArrayList<Selection>();
		//Copy the selections. Checked that it is ok
		for(Selection s: best.getSelection()){
			Selection newSelection = new Selection(s.getTick(), city, s);
			this.selections.add(newSelection);	
		}
		lastMutate = "";
		//Copy the fitness
		this.fitness = best.getFitness();
		
		//Copy remainBudget
		this.remainBudget = new int[GeneticAlgorithm.SIMULATIONS];
		int[] bestRemainBudget = best.getRemainBudget();
		for(int i=0;i<remainBudget.length;i++){
			this.remainBudget[i]=bestRemainBudget[i];
		}
		
		//Copy forbidden
		for(Integer i:Lattice.getForbidden(best.getCode())){
			Lattice.setForbidden(GeneticAlgorithm.NUM_INDIVIDUAL, i);
		}
	}
	
	//Select randomly the cells that are going to be protected every time step
	private void populateSelections(Lattice city){
		int newBudget = 0;
		Selection newSelection;
		
		for(int i=0; i<GeneticAlgorithm.SIMULATIONS; i++){
			newBudget = budget[i];
			if(i != 0){
				newBudget += remainBudget[i -1];
				//System.err.print(remainBudget[i -1] + " ");
			}
			
			if(GeneticAlgorithm.TYPE_SELECTION == 'F'){
				newSelection = new Selection(i, code, city, newBudget, false);
			}else{
				Random probability = new Random();
				if(probability.nextInt(10)>GeneticAlgorithm.SELECTION_PERCENTAGE){
					newSelection = new Selection(i, code, city, newBudget, false);
				}else{
					//No populate selection for this tick
					newSelection = new Selection(i, code, city, newBudget, true);
				}
			}
			
			if(newSelection.getCells().size()!= 0){
				selections.add(newSelection);
				remainBudget[i] = newSelection.getBudget();	
				int price = 0;
				for(Cell c: newSelection.getCells()){
					price += c.getPrice();
				}
				//System.err.println("Tick:" + i + " Budget:" + budget[i] + " price cells:" + price + " remain:" + remainBudget[i]);
			}else{
				if(i==0)
					remainBudget[i] = budget[i];
				else
					remainBudget[i] = remainBudget[i-1] + budget[i];
				//System.err.println("Tick:" + i + " Budget:" + budget[i] + " remain:" + remainBudget[i]);
			}
		}
	}
	
	public boolean mutate(Lattice city){
		double newFitness = 0;
		Selection sel = null;
		int formerPrice = 0;
		int tick = 0;
		int counter = 0;
		boolean changed = false;
		
		do{
			try{
				//Select a tick to mutate. 
				int index = new Random().nextInt(this.selections.size());
				tick = this.selections.get(index).getTick();					
				int newBudget = 0;
				if(tick !=0)
					newBudget = remainBudget[tick -1];
				
				if(GeneticAlgorithm.FEASIBLE.compareTo("F")==0){
					newBudget += budget[tick] - remainBudget[tick];
					for(Cell c: this.selections.get(index).getCells()){	
						this.lastMutate += "[" + c.getX() + "," + c.getY() + "],";
					}
				}else{
					int feasibleBudget = 0;
					if(tick !=0)
						feasibleBudget = remainBudget[tick -1];
					feasibleBudget += budget[tick] - remainBudget[tick];
					for(Cell c: this.selections.get(index).getCells()){
						newBudget += c.getPrice();	
						formerPrice += c.getPrice();
						this.lastMutate += "[" + c.getX() + "," + c.getY() + "],";
					}
					//System.err.println("FeasibleBudget:" + feasibleBudget + " newBudget:" + newBudget + " remainBudge[tick]:" + remainBudget[tick]);
					if(newBudget> feasibleBudget*1.2){
						newBudget = (int)(feasibleBudget*1);
					}
				}
				//New selection - the constructor includes the cells in forbidden
				sel = new Selection(tick, GeneticAlgorithm.NUM_INDIVIDUAL, city, newBudget, false);	
				if(sel.getCells().size()!=0){
					sel.calculateFitness();
					newFitness = sel.getFitness();
					this.selections.get(index).calculateFitness();
					double formerFitness = this.selections.get(index).getFitness();
					//System.err.println("new Fitness:" + newFitness + " former:" + formerFitness);
					if(newFitness>formerFitness){
						System.err.println("changed");
						if(sel.getCells().size()< this.selections.get(index).getCells().size())
							System.err.println("!!!!!!!! SMALL SELECTION TO:" + sel.getCells().size() + " FROM " + this.selections.get(index).getCells().size());
						if(sel.getCells().size()> this.selections.get(index).getCells().size())
							System.err.println("!!!!!!!! LARGE SELECTION TO:" + sel.getCells().size() + " FROM " + this.selections.get(index).getCells().size());
						
						this.lastMutate += "Tick:" + tick;
						changed = true;
						int newPrice = 0;
						for(Cell c: sel.getCells()){
							newPrice += c.getPrice();
							this.lastMutate += "[" + c.getX() + "," + c.getY() + "],";
						}
						for(Cell c: this.selections.get(index).getCells()){
							Lattice.deleteForbidden(GeneticAlgorithm.NUM_INDIVIDUAL, c.getX(), c.getY());
						}
						this.selections.set(index, sel);
						
						/*if((this.remainBudget[tick] + formerPrice) - newBudget<0)
							System.err.println("ERROR IN MUTATION. Former:" + (this.remainBudget[tick] + formerPrice) + " New:" + newBudget);*/
						this.remainBudget[tick] = newBudget - newPrice;
						this.lastMutate += "(" + newFitness + "-" + formerFitness + ")\n";
					}else{
						counter++;
						for(Cell c: sel.getCells()){
							Lattice.deleteForbidden(GeneticAlgorithm.NUM_INDIVIDUAL, c.getX(), c.getY());
						}
					}
				}else{
					//System.err.println("No cells found. New budget:" + newBudget + " tick:" + tick + " Forb:" + Lattice.getForbidden(this.code).size());
					counter++;
				}
			}catch(Exception e){
				System.err.println("Exception in mutate.");
				e.printStackTrace();
			}
			//System.err.println("newFitnes: " + newFitness + " worstfitness" + worstFitness[tick-1]);
		}while(!changed && counter<MAX_ATTEMPS);
		if(counter<MAX_ATTEMPS){
			//System.err.println("Individual changed. Old fitness:" + formerFitness + " new:" + newFitness);
			//System.err.println("Individual changed.");
			return true;
		}else{
			System.out.println("Generation fail.");
			return false;
		}		
	}
	
	//Fitness from the individual
	public void calculateIndividualFitness(){
		this.fitness = 0;
		for(Selection s: selections){
			s.calculateFitness();
			this.fitness += s.getFitness();
		}
		this.fitness /=100;
	}
	
	public double getFitness(){
		return fitness;
	}
	
	public int[] getRemainBudget(){
		return this.remainBudget;
	}
	
	public List<Selection> getSelection(){
		return this.selections;
	}
	
	public String toString(){
		String printable = "Individual " + code + " Selections:\n";
		for(Selection s: selections){
			printable += s.toString();
		}
		printable += "Fitness " + this.getFitness() + "\n";
		return printable;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public void setCode(int code){
		this.code = code;
		for(Selection s: this.selections){
			s.setCode(code);
		}
	}
	
	public String getLastMutate(){
		return this.lastMutate;
	}
}

