package gi.city.display;

import gi.city.Cell;

import java.awt.Color;
import java.awt.Font;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class CellBiologicalValueStyle extends DefaultStyleOGL2D{

	@Override
	public Color getColor(Object cell) {
		double value = Math.abs(((Cell) cell).getBioValue() + ((Cell) cell).getBioNeighbourValue());
		if(value > 1){
			value = 1;
		}
		/*if(value > 0.7)
			return new Color( 255, 0, 0);*/
		return new Color( 0, (int)(value*255), 0);
	}
}

