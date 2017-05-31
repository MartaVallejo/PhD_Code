package GA;
import java.util.ArrayList;
import java.util.List;

public class Selection {
	/**
	 * Step of the simulation
	 */
	private int tick;
	/**
	 * Cells selected in a determined time step
	 */
	private List<Cell> cells;
	
	private int code;
	private static int MIN_PRICE;
	private static int MAX_LOOP;
	private int budget;
	private Lattice city;
	private double fitness;
	//private int timesImproved;
	
	static{
		MIN_PRICE = 1000;
		MAX_LOOP = 100;
	}
	
	public Selection(int tick, int code, Lattice city, int budget, boolean isEmpty){
		this.tick = tick;
		cells = new ArrayList<Cell>();
		this.code = code;
		this.budget = budget;
		this.city = city;
		this.fitness = 0;
		
		if(!isEmpty){
			populateSelection();
			//System.err.println(" new Budget after population:" + this.budget);
		}
	}
	
	/**
	 * @author marta
	 * Constructor by copy. Used to copy the selections in the constructor
	 * by copy of Individual (used to mutate the individual to be mated)
	 */
	public Selection(int tick, Lattice city, Selection selection){
		this.tick = tick;
		this.code = GeneticAlgorithm.NUM_INDIVIDUAL;
		this.city = city;
		this.budget = selection.getBudget();
		//Copy cells
		cells = new ArrayList<Cell>();
		for(Cell c: selection.getCells()){
			cells.add(c);
		}
	}
	
	public void populateSelection(){
		//Select cells according to the budget
		Cell c = null;
		int maxLoop = 0;
		int numCells = 0;
		
		while(budget > MIN_PRICE && maxLoop < MAX_LOOP){			
			c = Cell.createCell(tick, code, city, budget);
			if(c != null){
				//System.err.print("Budget: " + budget);
				cells.add(c);
				Lattice.setForbidden(code, c.getX(), c.getY());
				maxLoop = 0;
				numCells++;
				budget -= c.getPrice();
				/*System.err.println(" .Protecting cell (" + c.getX() + "," + c.getY() + ") with price:" + c.getPrice() 
						+ " updated budget:" + budget + " in tick:" + tick);*/
			}else{
				maxLoop++;
			}
		}	
	}
	
	public double getFitness(){
		return this.fitness;
	}
	
	public int getBudget(){
		return budget;
	}
	
	public int getNumberCells(){
		return this.cells.size();
	}
	
	public int getCode(){
		return this.code;
	}
	
	public List<Cell> getCells(){
		return this.cells;
	}
	
	public void removeCells(){
		//System.err.println("Removing cells size(before):" + this.cells.size());
		this.cells.removeAll(cells);
		//System.err.println("Removing cells size:" + this.cells.size());
	}
	public int getTick(){
		return this.tick;
	}
	
	public void setCode(int code){
		this.code = code;
	}
	
	public String toString(){
		String printable = "Tick number: " + tick + " ";
		printable += "Cells size:" + cells.size() + " ";
		for(Cell c: cells){
			printable += c.toString();
		}
		printable += "\n";
		return printable;
	}
	
	public void calculateFitness(){
		this.fitness = 0;
		switch(GeneticAlgorithm.TYPE_FITNESS_TICKS){
		case 10:
			//Accumulative
			//System.err.println("Tick = 10");
			calculateFitness_1();
			break;
		case 21:
			//Single Init
			//System.err.println("Tick = 21");
			calculateFitness_2();
			break;
		case 22:
			//Single End
			//System.err.println("Tick = 22");
			calculateFitness_3();
			break;
		default:
			System.err.println("Option error in Individual:calculateFitness");	
		}
	}
	
	//Fitness from tick to the end
	private void calculateFitness_1(){
		double fitness = 0;
		int index;
		index = this.getTick();
		for(Cell c: this.getCells()){
			int i=index;
			while(i<GeneticAlgorithm.SIMULATIONS){
				fitness += city.getFitness(c.getX(), c.getY() , code, i);	
				i++;
			}
		}
		this.fitness = fitness/100;
	}
	
	//Fitness in the current tick
	private void calculateFitness_2(){
		double fitness = 0;
		int index = this.getTick();
		for(Cell c: this.getCells()){
			fitness += city.getFitness(c.getX(), c.getY() , code, index);
		}
		this.fitness = fitness;
	}
	
	//Fitness in the last tick
	private void calculateFitness_3(){
		double fitness = 0;
		int index = GeneticAlgorithm.SIMULATIONS;
		for(Cell c: this.getCells()){
			fitness += city.getFitness(c.getX(), c.getY() , code, index);
		}
		this.fitness = fitness;
	}
}
