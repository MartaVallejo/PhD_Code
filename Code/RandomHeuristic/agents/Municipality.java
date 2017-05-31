package gi.agents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import gi.city.Lattice;
import repast.simphony.engine.schedule.*;

public class Municipality {
	Lattice lattice;
	Schedule schedule;
	
	//Budget of the city to invest in land banking
	private int[] budget;
	private int[] accumulateBudget;
	
	//Criteria used to select green areas
	private enum Criteria{SATISFACTION, ECOLOGICAL};
	private Criteria criteria;
	
	public Municipality(Lattice lattice, Schedule schedule){
		this.lattice = lattice;
		this.schedule = schedule;
		this.budget = new int[Lattice.TOTAL_TICKS];
		this.accumulateBudget = new int[Lattice.TOTAL_TICKS];
		Arrays.fill(this.accumulateBudget, 0);
		switch(Lattice.TYPE_CRITERIA){
		case 0:
			this.criteria = Criteria.SATISFACTION;
			break;
		case 1:
			this.criteria = Criteria.ECOLOGICAL;
			break;
		}
		if(Lattice.TYPE_BUDGET == 0)
			readBudgetFile1();
		else
			readBudgetFile2();
	}
	
	private void readBudgetFile1(){
		BufferedReader br = null; 
		try {
			br = new BufferedReader(new FileReader("Budget_N.txt"));

			String newline = br.readLine();
			//System.err.println("budget:" + newline);
			if (newline.compareTo("")!=0) {
				String[] val = newline.split(" ");
				//System.err.println("size budget:" + val.length);
				for(int i=0; i<val.length;i++){
					budget[i] = (int)Double.valueOf(val[i]).longValue();
				}
				//System.out.println("Finished to read budget file");
			}else
				System.err.println("Budget file empty");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readBudgetFile2(){
		BufferedReader br = null; 
		try {
			br = new BufferedReader(new FileReader("Budget_P.txt"));

			String newline = br.readLine();
			//System.err.println("budget:" + newline);
			if (newline.compareTo("")!=0) {
				String[] val = newline.split(",");
				//System.err.println("size budget:" + val.length);
				for(int i=0; i<val.length;i++){
					budget[i] = Integer.parseInt(val[i]);
				}
				//System.out.println("Finished to read budget file");
			}else
				System.err.println("Budget file empty");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void selectGreenAreas(int currentTick){
		try{
			if(currentTick==0){
				System.out.println("Tick of the system: " + currentTick + " Acc budget:0 budget now:" + this.budget[currentTick]);
				this.accumulateBudget[currentTick] = lattice.searchGreenSpace( this.budget[currentTick]);
			}else{
				System.out.println("Tick of the system: " + currentTick + " Acc budget:" + this.accumulateBudget[currentTick-1] +
						" budget now:" + this.budget[currentTick]);
				this.accumulateBudget[currentTick] = lattice.searchGreenSpace( this.budget[currentTick] + this.accumulateBudget[currentTick-1]);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Size of array budget:" + this.budget.length);
			e.printStackTrace();
		}catch(Exception e){
			System.err.println("Exception in Selection of Green Areas.");
			System.err.println("Budget:" + accumulateBudget);
			System.err.println("Tick of the system:" + currentTick);
			e.printStackTrace();
		}
	}
	
	public int getAccumulateBudget(int currentTick){
		return accumulateBudget[currentTick];
	}
	
	public int getCriteria(){
		return this.criteria.ordinal();
	}
}
