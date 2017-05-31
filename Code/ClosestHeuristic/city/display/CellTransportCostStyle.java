package gi.city.display;

import gi.city.Cell;
import gi.city.City;

import java.awt.Color;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class CellTransportCostStyle extends DefaultStyleOGL2D{

	@Override
	public Color getColor(Object cell) {
		int colour;
		if(((Cell) cell).isHabitable()){
			double value = ((Cell) cell).getTransportCosts();
			double max = City.getMaxTransportCosts();
			double min = City.getMinTransportCosts();
			if((max - min) == 0)
				colour = 0;
			else{
				colour = (int)(((value-min) * 255)/(max - min));
				//System.err.println("Colour=" + colour + " value=" + value + " Max=" + max + " Min=" + min);
				if(colour>255 || colour<0)
					colour = 255;
				if(colour<0)
					colour = 0;				
			}
		
			return new Color( colour, colour, colour);
		}else{
			return new Color( 255, 255, 180);
		}
	}
}