package GA;
import GA.GeneticAlgorithm.Criteria;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


public class Lattice {
	private static List<int[]> statisticsUrbanised;
	private static List<int[]> statisticsDensity;
	private static List<Integer> forbidden[]; 
	private static List<int[]> statisticsNonUrbanPrices;
	private static List<double[]> statisticsBioValue;
	public static int TOTAL_URBANISED;
	/**
	 * Biological Value. Used for scenario 1 (constant prices)
	 */
	private static double[][] bioValue;
	
	static{
		bioValue = new double[GeneticAlgorithm.SIZE_LATTICE][GeneticAlgorithm.SIZE_LATTICE];
		statisticsUrbanised = new ArrayList<int[]>();
		forbidden = new ArrayList[GeneticAlgorithm.NUM_INDIVIDUAL+1];
		for( int i = 0; i < (GeneticAlgorithm.NUM_INDIVIDUAL+1); i++) {
			forbidden[i] = new ArrayList<Integer>();
		}
		statisticsDensity = new ArrayList<int[]>();
		statisticsNonUrbanPrices = new ArrayList<int[]>();
		statisticsBioValue = new ArrayList<double[]>();
	}
	
	public Lattice(){
		loadUrbanisticStatistics();
		loadDensityStatistics();
		if(GeneticAlgorithm.TYPE_PRICES == 0)
			readBioValueFile();
		else
			loadNonUrbanPricesStatistics();
		loadBioValueStatistics();
	}
	
	/**
	 * Return true if the cell was protected previously
	 * @param x
	 * @param y
	 * @param individual
	 * @return
	 */
	public static boolean isForbidden(int x, int y, int individual){
		if(forbidden[individual].size() == 0){
			return false;
		}else{
			for(Integer i: forbidden[individual]){
				//System.err.println(i + " pos:" + position(x, y) + " x:" + x + " y:" + y);
				if(i == position(x, y))
					return true;
			}
			return false;
		}
	}
	
/*	public static boolean isForbidden(Selection sel, int individual){
		if(forbidden[individual].size() == 0){
			return false;
		}else{
			for(Integer i: forbidden[individual]){
				//System.err.println(i + " pos:" + position(x, y) + " x:" + x + " y:" + y);
				for(Cell c:sel.getCells()){
					if(i == position(c.getX(), c.getY()))
						return true;
				}
			}
			return false;
		}
	}*/
	
	public static void setForbidden(int index, int x, int y){
		try{
			forbidden[index].add(position(x, y));
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Individual:" + index + " (" + x + "," + y + ")");
			e.printStackTrace();
		}
	}
	
	public static void setForbidden(int index, int pos){
		try{
			forbidden[index].add(pos);
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Individual:" + pos);
			e.printStackTrace();
		}
	}
	
	public static void deleteForbidden(int individual){
		forbidden[individual].clear();
	}
	
	public static void deleteForbidden(int individual, int x, int y){
		//System.out.println("-------------------------");
		Integer pos = position(x, y);
		if(forbidden[individual].contains(pos)){
			int size = forbidden[individual].size();
			forbidden[individual].remove(pos);
			if(forbidden[individual].size()!=(size-1))
				System.out.println("Error in deleteForbidden: Wrong size");
		}else{
			System.out.println("Error in deleteForbidden: Not found");
		}
	}
	
	public static List<Integer> getForbidden(int individual){
		return Lattice.forbidden[individual];
	}
	
	private static int position(int x, int y){
		return (x * GeneticAlgorithm.SIZE_LATTICE) + y;
	}
	
	private static int positionX(int pos){
		return (pos/GeneticAlgorithm.SIZE_LATTICE);
	}
	
	private static int positionY(int pos){
		//System.err.println("Pos:" + pos + " X:" + pos/SIZE + " Y:" + pos%SIZE);
		return pos%GeneticAlgorithm.SIZE_LATTICE; 
	}

	public double getFitness(int x, int y, int individual, int currentTick){
		if(GeneticAlgorithm.CRITERIA==Criteria.SATISFACTION){
			return getFitness_S(x, y, currentTick);
		}else{
			return getFitness_P(x, y, individual, currentTick);
		}
	}
	
	public double getFitness(int x, int y, int individual, int currentTick, Selection sel){
			return getFitness_P(x, y, individual, currentTick, sel);
	}
	
	public double getFitness_S(int x, int y, int currentTick){
		double total = 0;
		int population[] = statisticsDensity.get(currentTick);
		Set<Integer> set=new TreeSet<Integer>();
		//System.err.print("Tick:" + currentTick + " cell: (" + x + "," + y + ")");
		
		for(int a=0;a<4;a++){
			for(int b=0;b<4;b++){
				if((a+b)<4 && (a+b)!=0){	
					if((x+a)<GeneticAlgorithm.SIZE_LATTICE && (y+b)<GeneticAlgorithm.SIZE_LATTICE){
						set.add(new Integer(Lattice.position(x+a, y+b)));
					}
					if((x-a)>=0 && (y+b)<GeneticAlgorithm.SIZE_LATTICE){
						set.add(new Integer(Lattice.position(x-a, y+b)));
					}
					if((x+a)<GeneticAlgorithm.SIZE_LATTICE && (y-b)>=0){
						set.add(new Integer(Lattice.position(x+a, y-b)));
					}
					if((x-a)>=0 && (y-b)>=0){
						set.add(new Integer(Lattice.position(x-a, y-b)));
					}	
				}
			}
		}
		if(GeneticAlgorithm.TYPE_FITNESS_AGENTS == 1){
			//System.err.println(" count:" + count + " size set:" + set.size());
			//System.out.println(Arrays.toString(population));
			for(Integer pos:set){
				total += population[pos];
			}
		}else{
			for(Integer pos:set){
				int xx = Lattice.positionX(pos);
				int yy = Lattice.positionY(pos);
				int weighted = 4 - (Math.abs(x-xx)+Math.abs(y-yy));
				if(weighted<0){
					System.err.println("W:" + weighted + " x:" + x + " y:" + y + " xx:" + xx + " yy:" + yy + " pos" + pos);
				}	
				total += population[pos]*weighted;
				/*if(population[pos]!=0)
					System.err.println("!!!!!!!!!!");*/
			}
			total/=100;
		}	
		return total;		
	}

	public double getFitness_P(int x, int y, int individual, int currentTick){
		double fitness = 0;
		//System.err.print("Cell:" + x + "," + y);
		for(Integer c: this.getNeighbours(x,y)){
			//System.err.print(" [" + Lattice.positionX(c) + "," + Lattice.positionY(c) + "]");
			if(Lattice.isForbidden(Lattice.positionX(c), Lattice.positionY(c), individual))
				fitness+=20;
		}
		double bio[] = statisticsBioValue.get(currentTick);
		//System.err.println("FitnessN:" + fitness + " bio:" + (bio[Lattice.position(x, y)]*100));
		return fitness += (bio[Lattice.position(x, y)]*100);
	}
	
	public double getFitness_P(int x, int y, int individual, int currentTick, Selection sel){
		double fitness = 0;
		//System.err.print("Cell:" + x + "," + y);
		for(Integer c: this.getNeighbours(x,y)){
			//System.err.print(" [" + Lattice.positionX(c) + "," + Lattice.positionY(c) + "]");
			if(Lattice.isForbidden(Lattice.positionX(c), Lattice.positionY(c), individual)){
				boolean found = false;
				for(Cell cell: sel.getCells()){
					if(cell.getX() == x && cell.getY() == y){
						found = true;
					}
				}
				if(!found)
					fitness+=20;
			}
		}
		double bio[] = statisticsBioValue.get(currentTick);
		//System.err.println("FitnessN:" + fitness + " bio:" + (bio[Lattice.position(x, y)]*100));
		return fitness += (bio[Lattice.position(x, y)]*100);
	}
	
	public static double statisticsUrbanised(int x, int y, int tick){
		double value = 0.0, total = 0.0;
		try{
			//System.err.print(" pos:" + position(x, y) + " Pre-value:" + (statisticsUrbanised.get(tick-1)[position(x, y)]));
			value = (statisticsUrbanised.get(tick)[position(x, y)]) *1.0/TOTAL_URBANISED;
			//System.err.println(" value urbanised:" + value);
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("x:" + x + " y:" + y + " pos:" + position(x, y) + " tick" + (tick-1));
			e.printStackTrace();
			e.getMessage();
		}
		total = 1/(1+ Math.exp(20*value - 0.5));
		return total;
	}
	
	/**
	 * @author mv59
	 * Load in an array the cells protected in each tick
	 */
	private void loadUrbanisticStatistics(){
		try{			
			Scanner sc = new Scanner(new File("Urbanised.txt"));
			
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					int[] row = new int[GeneticAlgorithm.SIZE_LATTICE*GeneticAlgorithm.SIZE_LATTICE];
					String[] val = newline.split(" ");
					//System.out.println("val size:" + val.length + " and row size:" + row.length);
					TOTAL_URBANISED = Integer.parseInt(val[0]);
					for(int i=1; i<val.length;i++){
						row[i-1] = Integer.parseInt(val[i]);
					}
					//System.out.println("!!!!!row " + Arrays.toString(row));
					Lattice.statisticsUrbanised.add(row);
					//System.err.println("Size of statistic urbanised: " + this.statisticsUrbanised.size());
				}
			}
			sc.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the statistics about urban areas");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Urban: Accessing wronly to the array");
		}catch (Exception e){
			System.err.println("Other exception: statistics about urban areas");
			e.printStackTrace();	
		}
	}
	
	/**
	 * @author mv59
	 * Load in an array the cells protected in each tick
	 */
	private void loadBioValueStatistics(){
		try{			
			Scanner sc = new Scanner(new File("BioValue.txt"));
			
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					double[] row = new double[GeneticAlgorithm.SIZE_LATTICE*GeneticAlgorithm.SIZE_LATTICE];
					String[] val = newline.split(" ");
					//TOTAL_URBANISED = Integer.parseInt(val[0]);
					for(int i=1; i<val.length;i++){
						row[i-1] = Double.parseDouble(val[i]);
					}
					//System.out.println("!!!!!row " + Arrays.toString(row));
					Lattice.statisticsBioValue.add(row);
					//System.err.println("Size of statistic urbanised: " + this.statisticsUrbanised.size());
				}
			}
			sc.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the bioValue statistics");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Urban: Accessing wronly to the array in bioValue statistics");
		}catch (Exception e){
			System.err.println("Other exception: bioValue statistics");
			e.printStackTrace();	
		}
	}
	
	/**
	 * @author mv59
	 * Load in an array the agents presented in each tick
	 */
	private void loadDensityStatistics(){
		try{			
			Scanner sc = new Scanner(new File("Density.txt"));
			
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					//System.out.println("File Line:" + newline);
					int[] row = new int[GeneticAlgorithm.SIZE_LATTICE*GeneticAlgorithm.SIZE_LATTICE];
					String[] val = newline.split(" ");
					for(int i=1; i<val.length;i++){
						row[i-1] = Integer.parseInt(val[i]);
					}
					Lattice.statisticsDensity.add(row);
				}
			}
			sc.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the statistics about urban areas");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Density: Accessing wronly to the array");
			e.printStackTrace();
		}catch (Exception e){
			System.err.println("Other exception: statistics about urban areas");
			e.printStackTrace();	
		}
	}

	/**
	 * @author mv59
	 * Load in an array the NonUrbanPrices in each tick
	 */
	private void loadNonUrbanPricesStatistics(){
		try{			
			Scanner sc = new Scanner(new File("NonUrbanPrices.txt"));
			
			while(sc.hasNextLine()){
				String newline = sc.nextLine();
				if(newline.compareTo("")!=0){
					int[] row = new int[GeneticAlgorithm.SIZE_LATTICE*GeneticAlgorithm.SIZE_LATTICE];
					String[] val = newline.split(" ");
					//System.out.println("val size:" + val.length + " and row size:" + row.length);
					for(int i=1; i<val.length;i++){
						row[i-1] = Integer.parseInt(val[i]);
					}
					Lattice.statisticsNonUrbanPrices.add(row);
				}
			}
			sc.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error loading the statistics about NonUrbanPrices");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("NonUrbanPrices: Accessing wronly to the array");
		}catch (Exception e){
			System.err.println("Other exception: statistics about urban areas");
			e.printStackTrace();	
		}
	}
	
	private void readBioValueFile(){
		int i = 0;
		int j = 0;
		int z = 0;
		try {
			Scanner sc = new Scanner(new File("Scenario.txt"));
			
			while(sc.hasNextLine()){
				String newLine = sc.nextLine();
				if(newLine.compareTo("")!=0){
					String[] val = newLine.split(" ");
					//System.out.println("Line:" + i + " BioValue val size:" + val.length);
					for(j=0; j<val.length; j++){
						if(z == GeneticAlgorithm.SIZE_LATTICE)
							z = 0;
						bioValue[i][z] = Double.parseDouble(val[j]);
						z++;
					}	
					i++;
				}
			}
			sc.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error loading BioValue values");
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Biovalue: Accessing wronly to the array");
		}catch (Exception e){
			System.err.println("Other exception: biovalue");
			e.printStackTrace();	
		}
	}
	
	public int getPrice(int timeStep, int x, int y){
		int finalPrice = 0;
		if(GeneticAlgorithm.TYPE_PRICES==0){
			if(bioValue[x][y]<0.7)
				finalPrice = 30000;
			else
				finalPrice = 66000;
		}else{
			int[] price = Lattice.statisticsNonUrbanPrices.get(timeStep);
			finalPrice = price[position(x,y)];
			//System.err.println("Final price:" + finalPrice);
			if(finalPrice == 0)
				finalPrice = 30000;
			
		}
		return finalPrice;
	}
	
	public Set<Integer> getNeighbours(int x, int y){ 
		Set<Integer> s=new TreeSet<Integer>();
		//List<Integer> s=new ArrayList<Integer>();
		for(int a=-1;a<2;a++){
			for(int b=-1;b<2;b++){	
				if(a!=0 || b!=0){
					if((x+a)<GeneticAlgorithm.SIZE_LATTICE && (x+a)>=0 && (y+b)<GeneticAlgorithm.SIZE_LATTICE && (y+b)>=0){
						s.add(new Integer(Lattice.position(x+a, y+b)));
						//System.out.println("Cell [" + x + "," + y + "] GP:" + Grid.position(x+a, y+b) + " +" + a + " +" + b);
					}
					if((x-a)<GeneticAlgorithm.SIZE_LATTICE && (x-a)>=0 && (y+b)<GeneticAlgorithm.SIZE_LATTICE && (y+b)>=0){
						s.add(new Integer(Lattice.position(x-a, y+b)));
						//System.out.println("Cell [" + x + "," + y + "] GP:" + Grid.position(x-a, y+b) + " -" + a + " +" + b);
					}
					if((x+a)<GeneticAlgorithm.SIZE_LATTICE && (x+a)>=0 && (y-b)<GeneticAlgorithm.SIZE_LATTICE && (y-b)>=0){
						s.add(new Integer(Lattice.position(x+a, y-b)));
						//System.out.println("Cell [" + x + "," + y + "] GP:" + Grid.position(x+a, y-b) + " +" + a + " -" + b);
					}
					if((x-a)<GeneticAlgorithm.SIZE_LATTICE && (x-a)>=0 && (y-b)<GeneticAlgorithm.SIZE_LATTICE && (y-b)>=0){
						s.add(new Integer(Lattice.position(x-a, y-b)));
						//System.out.println("Cell [" + x + "," + y + "] GP:" + Grid.position(x-a, y-b) + " -" + a + " -" + b);
					}
				}
			}
		}
		//System.err.println("Cell: [" + x + "," + y + "] neigh:" + );
		return s;
	}
	
	public static int getTimesUrbanised(){
		return TOTAL_URBANISED;
	}
	
	public static int getTimesUrbanised(int x, int y, int tick){
		return statisticsUrbanised.get(tick)[position(x, y)];
	}
	
	public String printForbidden(int individual){
		String data = "";
		for(int i:forbidden[individual]){
			data += "[" + Lattice.positionX(i) + "," + Lattice.positionY(i) + "], ";
		}
		return data;
	}
	
	public static int[] getStatisticsDensity(int tick){
		int index = 0;
		for(int[] stat:statisticsDensity){
			if(index == tick){
				return stat;
			}
			index++;
		}
		return null;
	}
}
