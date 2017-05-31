package gi.city.display;

import gi.city.Cell;
import gi.city.City;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;


public class CellDensityStyle extends DefaultStyleOGL2D{
	@Override
	public Color getColor( Object cell ){
		int colour;
		if(((Cell) cell).isHabitable() ) {
			int maxPopulation = City.getMaxPopulated();
			int minPopulation = City.getMinPopulated();
			int population = ((Cell) cell).getPopulation();
			if((maxPopulation - minPopulation) == 0){
				colour = 0;
			}else{
				colour = (int)(((population-minPopulation) * 255)/(maxPopulation - minPopulation));
				/*System.err.println("Colour=" + colour + " population=" + population + " Max=" + 
						maxPopulation + " Min=" + minPopulation);*/
				if(population==minPopulation)
					return new Color(220, 20, 60);
				
				if(population==maxPopulation)
					return new Color(110, 139, 61);
				
				if(colour>255)
					colour = 255;
				if(colour<0)
					colour = 0;	
				/*System.err.println("Colour=" + colour + " population=" + population + " Max=" + 
						maxPopulation + " Min=" + minPopulation);*/
			}
		}else{
			return new Color( 255, 225, 180);
		}
		return new Color( 60, 255 - colour, 255 - colour);
	}
}
