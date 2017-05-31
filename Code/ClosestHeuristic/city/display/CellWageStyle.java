package gi.city.display;

import gi.city.Cell;
import java.awt.Color;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class CellWageStyle extends DefaultStyleOGL2D{
	@Override
	public Color getColor(Object cell) {

		if(((Cell) cell).isHabitable()){
			double value = ((Cell) cell).getMeanWage();
			//System.err.println("mean wage=" + value);
			if(value>2000)
				return Color.red;
			if(value<2000 && value>=1500)
				return Color.orange;
			if(value<1500 && value>=1000)
				return Color.yellow;
			if(value<1000)
				return Color.white;
			return new Color( 0, 0, 0);
		}else
			return new Color( 255, 255, 255);
	}

}
