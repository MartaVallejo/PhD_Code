package gi.agents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import gi.city.Lattice;
import repast.simphony.engine.schedule.*;

public class Municipality {
	Lattice lattice;
	Schedule schedule;
	
	//Budget of the city to invest in land banking
	private int[] budget;
	private int[] accumulateBudget;
	
	//Possible green areas to protect
	public class GreenCandidate{
		private int tick;
		private int maxIndex;
		private int[] greenX;
		private int[] greenY;
		
		public GreenCandidate(int tick, int x, int y){
			greenX = new int[8];
			greenY = new int[8];
			this.tick = tick;
			greenX[0] = x;
			greenY[0] = y;
			maxIndex = 1;
			//System.out.println("MaxIndex: 0, cell (" + x + "," + y + ") in tick:" + tick);
		}
		
		public int getTick(){
			return this.tick;
		}
		
		public int[] getGreenX(){
			return this.greenX;
		}
		
		public int[] getGreenY(){
			return this.greenY;
		}
		
		public void addCell(int x, int y){
			if(maxIndex>=8){
				System.err.println("More than 5 cells protected. MaxIndex:" + maxIndex + "cell (" + x + "," + y + ") in tick:" + tick);
			}else{
				//System.err.println("MaxIndex:" + maxIndex + "cell (" + x + "," + y + ") in tick:" + tick);
				greenX[maxIndex] = x;
				greenY[maxIndex] = y;
				maxIndex++;
			}
		}
		
		public int getMaxIndex(){
			return this.maxIndex;
		}
	}
	
	private List<GreenCandidate> candidateCells;
	
	public Municipality(Lattice lattice, Schedule schedule){
		this.lattice = lattice;
		this.schedule = schedule;
		this.budget = new int[Lattice.TOTAL_TICKS];
		this.accumulateBudget = new int[Lattice.TOTAL_TICKS];
		Arrays.fill(this.accumulateBudget, 0);
		this.candidateCells = new ArrayList<GreenCandidate>();

		loadGreenAreas();
		if(Lattice.TYPE_BUDGET == 0)
			readBudgetFile1();
		else
			readBudgetFile2();
	}
	
	public void loadGreenAreas(){
		//System.err.println("Green Areas Loaded");
		int i = 0;
		try{
			//Read the file to know the dimension of the arrays
			File folderToScan = new File(".");
			File[] listOfFiles = folderToScan.listFiles();
			for(File f: listOfFiles){
				if(f.isFile()){
					if(f.getName().startsWith("Result_GA")){
						Lattice.NUM_STATISTICS = f.getName().substring(10, 12);
						Lattice.TYPE_FITNESS = f.getName().substring(13, 14);
						int counter = 14;
						if(Lattice.TYPE_FITNESS.compareTo("S")==0){
							Lattice.TYPE_SATISFACTION_TICKS = f.getName().substring(counter, counter+2);
							counter+=2;
							Lattice.TYPE_SATISFACTION_AGENTS = f.getName().substring(counter, counter+1);
							counter++;
						}else{
							Lattice.TYPE_SATISFACTION_AGENTS = "-";
						}
						Lattice.TYPE_SELECTION = f.getName().substring(counter+1, counter+2);
						if(Lattice.TYPE_SELECTION.compareTo("S")==0){
							Lattice.PERCENTAGE_STOCHASTIC = f.getName().substring(counter+2, counter+4);
							counter+=2;
						}else{
							Lattice.PERCENTAGE_STOCHASTIC = "0";
						}
						Lattice.FEASIBILITY = f.getName().substring(counter+3, counter+4);
						Lattice.GA_ID = f.getName().substring(counter+5, counter+9);
						System.out.println("Num Stat:" + Lattice.NUM_STATISTICS + " fit:" + 
								Lattice.TYPE_FITNESS + " sat_ticks:" + Lattice.TYPE_SATISFACTION_TICKS +
								" sat_agent:" + Lattice.TYPE_SATISFACTION_AGENTS +
								" sel:" + Lattice.TYPE_SELECTION + " %:" + Lattice.PERCENTAGE_STOCHASTIC
								+ " fea:" + Lattice.FEASIBILITY + " Id:" + Lattice.GA_ID);
						
						Scanner sc = new Scanner(f);
						while (sc.hasNextLine()) {
							String newLine[] = sc.nextLine().split(" ");
							addCell(Integer.valueOf(newLine[0]), Integer.valueOf(newLine[1]), 
									Integer.valueOf(newLine[2]));
							//System.err.println("Tick:" + newLine[0] + " X:" + newLine[1] + " Y:" + newLine[2]);
							//i++;
						}
						sc.close();
						continue;
					}
				}
			}
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the scenario");
			e.printStackTrace();
		}catch(NumberFormatException e){
			System.err.println("Error in line:" + i);	
		}
	}
	
	public int getBudget(int tick){
		return this.budget[tick];
	}
	
	public int getAccumulateBudget(int tick){
		return this.accumulateBudget[tick];
	}
	
	public void setRemainBudget(int newBudget, int tick){
		this.accumulateBudget[tick] = newBudget;
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
	
	//Green Candidate
	public void addCell(int tick, int x, int y){
		for(GreenCandidate c:this.candidateCells){
			if(c.getTick() == tick){
				c.addCell(x, y);
				return;
			}
		}
		GreenCandidate candidate = new GreenCandidate(tick, x, y);
		candidateCells.add(candidate);
	}
	
	public int[] getGreenAreaX(int tick){
		try{
			for(GreenCandidate g:this.candidateCells){
				int t = g.getTick();
				if(t == tick){
					return g.getGreenX();
				}else{
					if(t > tick)
						return null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public int[] getGreenAreaY(int tick){
		try{
			for(GreenCandidate g:this.candidateCells){
				int t = g.getTick();
				if(t == tick){
					return g.getGreenY();
				}else{
					if(t > tick)
						return null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public int getMaxIndex(int tick){
		for(GreenCandidate g:this.candidateCells){
			int t = g.getTick();
			if(t == tick){
				return g.getMaxIndex();
			}
		}
		return -1;
	}
}
