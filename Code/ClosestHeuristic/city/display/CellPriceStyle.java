package gi.city.display;

import gi.city.Cell;
import gi.city.City;

import java.awt.Color;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class CellPriceStyle extends DefaultStyleOGL2D{
	@Override
	public Color getColor(Object cell) {
		if(((Cell) cell).isHabitable()){
			int price = ((Cell) cell).getPrice();
			if(price<City.getMinUrbanPrice()){
				City.setMinUrbanPrice(price);
			}	
			if(price>City.getMaxUrbanPrice()){
				City.setMaxUrbanPrice(price);
			}
			int maxPrice = City.getMaxUrbanPrice();
			int minPrice = City.getMinUrbanPrice();
			//System.err.println("Price:" + price + " Max:" + maxPrice + " Min:" + minPrice);
			
			int range = (maxPrice - minPrice)/4;
			int choice;
			
			if (range==0){
				choice = 0;
			}else{
				choice = (price - minPrice)/range;
			}

			switch(choice){
			case 0:
				return new Color( 255, 204, 255);
			case 1:
				return new Color( 255, 102, 255);
			case 2:
				return new Color(255, 0, 255);
			case 3:
				return new Color( 153, 0, 153);
			case 4:
				return new Color( 51, 0, 51);
			default:
				return Color.WHITE;
			}	
		}
		return Color.WHITE;
	}
}