package gi.city.display;

import gi.city.Cell;

import java.awt.*;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

/**
 * Displays the state of the cells, with:
 * NEW -> red
 * AVAILABLE -> white
 * EMPTY -> gray
 * OLD -> orange
 * RIVER -> blue
 * PROTECTED -> green
 * 
 * Bordered cells are in the floodplain, red borders in the 100year, orange borders = 50 year.
 * @author dave
 *
 */
public class CellStateStyle extends DefaultStyleOGL2D{
	@Override
	public Color getColor( Object agent ){
		float ageProp = (float)((Cell) agent).getAge()/Cell.REDEVELOPMENT_AGE;
		if( ageProp > 1 ) 
			ageProp = 1;
		if( ageProp < 0 ) 
			ageProp = 0;
		switch ( ((Cell) agent).getState() ){
			case NEW:
				return Color.red;
			case AVAILABLE:
				return Color.WHITE;
			case OLD:
				return Color.ORANGE;
			case EMPTY:
				return Color.gray;
			case RIVER:
				return Color.blue;
			case PROTECTED:
				return Color.green;
			default:
				return Color.black;
		}
	}
}
