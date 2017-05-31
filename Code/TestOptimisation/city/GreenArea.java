package gi.city;

import gi.agents.Municipality;
import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * 
 * @author marta
 *
 */
public class GreenArea{
	Context context;
	private String greenAreas;
	private static List<Cell> nonUrbanCells;
	private static List<Cell> protectedCells;
	
	/**
	 * Parameters of biology value
	 */
	private boolean updateBioValue;
	
	/**
	 * Number of inconsistencies founded
	 */
	private int inconsistency;
	
	/**
	 * Number of failures due to lack of budget
	 */
	private int lackBudget;

	/**
	 * Maximum green price
	 */
	private static double maxGreenPrice;
	
	/**
	 * Minimum green price
	 */
	private static double minGreenPrice;
	
	static{
		nonUrbanCells = new ArrayList<Cell>();
		protectedCells = new ArrayList<Cell>();
		
		maxGreenPrice = Double.MIN_VALUE;
		minGreenPrice = Double.MAX_VALUE;
	}
	
	/**
	 * Constructor of the areas non urbanised of the city
	 */
	public GreenArea(Context context){
		this.context = context;
		updateBioValue = false;
		this.inconsistency = 0;
		this.lackBudget = 0;
		greenAreas = "[]";
	}
	
	/**
	 * @author marta
	 */
	public void addCell(Cell cell){
		GreenArea.nonUrbanCells.add(cell);
	}

	/**
	 * @author marta
	 * Search a cell to be protected according to a budget and a criteria.
	 * The cell is protected.
	 * @param budget Monetary budget to buy a given terrain
	 * @param criteria Criteria of search is going to be used
	 * @return the method returns the remained budget.
	 */
	public void searchGreenSpace(int tick, Municipality municipality){
		//Search for a possible cell that has to be
		try{
			//How many cells to protect this tick according the the GA
			int index = municipality.getMaxIndex(tick);
			int budget = 0;
			
			if(tick==0){
				budget = municipality.getBudget(tick);
				System.err.println("budget:" + budget + " (" + municipality.getBudget(tick) + " + 0)\n");
			}else{
				budget = municipality.getBudget(tick) + municipality.getAccumulateBudget(tick-1);
				System.err.println("budget:" + budget + " (" + municipality.getBudget(tick) + " + " +  municipality.getAccumulateBudget(tick-1)  + ")\n");
			}
			
			int i = 0;
			if(index!=-1){
				int coodX[] = municipality.getGreenAreaX(tick);
				int coodY[] = municipality.getGreenAreaY(tick);

				while(i<index){
					Cell cell = GreenArea.getCell(coodX[i], coodY[i]);
					if(cell==null){
						//System.err.print("Cell ("+ coodX + "," + coodY + ") is already urban: inconsistencies " + (this.inconsistency+1) + "\n" );
						this.inconsistency++;
						System.err.println("current remain budget:" + budget + "\n");
					}else{
						//System.out.println("Cell is available? " + cell.isAvailable());
						if(budget>=cell.getPrice()){
							if(cell.getState()==CellState.PROTECTED){
								System.err.println("ERROR: trying to protect a cell already protected!!!!!!!");
							}
							if(cell.getState()!=CellState.EMPTY){
								System.err.println("ERROR IN TYPE OF CELL (SEARCH GREEN SPACES)");
							}
							budget -= cell.getPrice();
							//System.err.println(" current remain budget:" + municipality.getRemainBudget(tick) + "\n");
							cell.setState(CellState.PROTECTED);
							GreenArea.protectedCells.add(cell);
							GreenArea.nonUrbanCells.remove(cell);
							try{
								if(greenAreas.length()==2)
									greenAreas = greenAreas.substring(0,1) + cell.getLocation().getX() + ":" + cell.getLocation().getY() + "]";
								else{
									//String before = greenAreas;
									greenAreas = greenAreas.substring(0,greenAreas.length()-1) + "-" + cell.getLocation().getX() + ":" + cell.getLocation().getY() + "]";
									//System.err.println("!!!!!!before:" + before + " after:" + greenAreas);
								}
							}catch(StringIndexOutOfBoundsException e){
							System.err.println("Total Length greenAreas:" + greenAreas.length());
							System.err.println("GreenAreas:" + greenAreas);
							e.printStackTrace();
							}
							System.err.print("Protected Cell (" + coodX[i] + "," + coodY[i] + ") with price:" + cell.getPrice() + " Budget:" +
									 budget + " state Cell:" + cell.getStateString() + "\n");
						}else{
							this.lackBudget++;
							System.err.println("Not enough Budget. Price:" + cell.getPrice() + " Budget:" + budget + " state Cell:" + cell.getStateString() + "\n");
						}
					}
					i++;
				}
		}else{
			System.out.println("No Protected Cell for tick " + tick + " Current budget:" + budget + "\n");
		}
			municipality.setRemainBudget(budget, tick);
				
		}catch(NullPointerException e){
			System.err.println("Problem with the coordenates in searchGreenSpace");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialise the biological value of the cells according to its neighbourhood
	 * @author marta
	 */
	public void initBioInfluence(){
		boolean isChanged;
		try{
			do{
				isChanged = false;
				for( Cell c : GreenArea.nonUrbanCells ) {
					double value = c.cellBioValue();
					if( value!= c.getBioNeighbourValue()){
						c.setBioNeighbourValue(value);
						isChanged = true;
					}
				}
			}while(isChanged);
		}catch(Exception e){
			System.err.println("Error in initBio Influence");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param cell Cell to look for the neighbourhood to decrease
	 * the biological value
	 * @return
	 */
	public void decreaseNeigbourhoodBioValue(Cell cell, int distance){
		int distanceX, distanceY, finalDistance;
		for( Cell c : GreenArea.nonUrbanCells ) {
			distanceX = Math.abs(c.getLocation().getX() - cell.getLocation().getX());
			distanceY = Math.abs(c.getLocation().getY() - cell.getLocation().getY());
			//if it is inside the range
			if(distanceX<= distance && distanceY<= distance && 
					(distanceX != 0 || distanceY != 0 )){
				if(distanceX > distanceY)
					finalDistance = distanceX;
				else
					finalDistance = distanceY;
				switch (finalDistance){
				case 1:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.1");
					c.setBioValue(cell.getBioValue() - 0.1);
					break;
				case 2:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.05");
					c.setBioValue(cell.getBioValue() - 0.05);
					break;
				case 3:
					//System.out.println("Cell: " + c.getLocation() + " decrease bio Value in 0.01");
					c.setBioValue(cell.getBioValue() - 0.01);
					break;
				}				
			}
			//It does not allow negative values
			if(c.getBioValue()<0)
				c.setBioValue(0);
		}
	}
	
	/**
	 * Update the bio parameters and prices
	 */
	@ScheduledMethod( start = 1.1, interval=1, priority=0 )
	public void updateParameters(){	
		//Update neighbourhood bio values
		if(getUpdateBioValue() == true){
			//Update bio influence
			initBioInfluence();
		}else
			setUpdateBioValue(false);
		updateGreenPrices();
	}
	
	/**
	 * Update green prices
	 */
	public void updateGreenPrices(){
		double minPrice = Double.MAX_VALUE;
		for(Cell c:GreenArea.nonUrbanCells){
			c.updatePriceRuralArea();
			if(c.getPrice()<minPrice)
				minPrice = c.getPrice();
		}
		GreenArea.minGreenPrice = minPrice;
		//System.out.println("!!!!!!!GreenPrices updated:" + GreenArea.minGreenPrice);
	}
	
	/**
	 * Reset min man green prices
	 */
	public void resetGreenPrices(){	
		GreenArea.maxGreenPrice = Integer.MIN_VALUE;
		GreenArea.minGreenPrice = Integer.MAX_VALUE;
	}
	
	/**
	 * @author marta
	 * Search a determine cell in the arrayList
	 * @param x Coordinate x
	 * @param y Coordinate y
	 * @return Cell searched
	 */
	public static Cell getCell(int x, int y){
		Cell returnCell = null;
		
		for(Cell c: GreenArea.nonUrbanCells){
			if(c.getLocation().getX() == x && c.getLocation().getY() == y)
				returnCell = c;
		}
		return returnCell;
	}
	
	/**
	 * @author mv59
	 * @param location Location of the cell that is going to be urbanised
	 * @return the cell removed
	 */
	public Cell getRemoveCell(int[] location){
		for(Cell cell: GreenArea.nonUrbanCells){
			if(cell.hasLocation(location)){
				GreenArea.nonUrbanCells.remove(cell);
				return cell;
			}
		}
		for(Cell cell: GreenArea.protectedCells){
			if(cell.hasLocation(location)){
				System.err.println("Trying to remove a rural cell that has been already protected.");
			}
		}
		return null;
	}
	
	/**
	 * @author mv59
	 * Distance to a protected area
	 */
	public int distanceProtected(Cell cell){
		int distance = 0;
		int minDistance = Integer.MAX_VALUE;
		for(Cell c: GreenArea.protectedCells){
			distance = Math.abs(c.getLocation().getX() - cell.getLocation().getX());
			distance += Math.abs(c.getLocation().getY() - cell.getLocation().getY());
			if(minDistance > distance)
				minDistance = distance;
		}
		return minDistance;
	}

	public void updateBioInfluence(){
		this.updateBioValue = true;
	}
	
	public static int getTotalProtectedCells(){
		return GreenArea.protectedCells.size();
	}
	
	public int getInconsistency(){
		return this.inconsistency;
	}
	
	public int getLackBudget(){
		return this.lackBudget;
	}
	
	public boolean getUpdateBioValue(){
		return this.updateBioValue;
	}
	
	public void setUpdateBioValue(boolean value){
		this.updateBioValue = value;
	}

	public static List<Cell> getNonUrbanCells(){
		return GreenArea.nonUrbanCells;
	}

	/**
	 * @return highest price in the urban lattice
	 */
	public static double getMaxGreenPrice(){
		return GreenArea.maxGreenPrice;
	}
	
	public static void setMaxGreenPrice(int price){
		GreenArea.maxGreenPrice = price;
	}
	
	public static double getMinGreenPrice(){
		return GreenArea.minGreenPrice;
	}
	
	public static void setMinGreenPrice(int price){
		GreenArea.minGreenPrice = price;
	}

	public double getAvgGreenPrice(){
		double totalPrice = 0;
		int times = 0;
		for(Cell c:GreenArea.nonUrbanCells){
			totalPrice +=c.getPrice();
			times++;
		}
		return (totalPrice*1.0)/times;
	}
	
	public static List<Cell> getProtectedCells(){
		return GreenArea.protectedCells;
	}

	public String getGreenAreaString(){
		String green = greenAreas;
		greenAreas = "[]";
		return green;
	}

	public double getAvgCloseness(){
		int closeness = 0;
		for(Cell c: GreenArea.protectedCells){
			closeness += c.getRing();
		}
		if((closeness *1.0)/GreenArea.protectedCells.size()>0)
			return (closeness *1.0)/GreenArea.protectedCells.size(); 
		else
			return 0.0;		
	}
}
