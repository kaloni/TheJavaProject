import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
 * Class that keeps track on when to produce new cars
 * Visually this is the areas from which cars depart from and arrive at
 */

public class CarArea {

	private Pos pos;
	private int[] color;
	private HashMap<CarArea, Long> destinationMap;
	private HashMap<Long, Long> clock;
	private CarSimulator parent;
	
	public CarArea(Pos pos) {
		
		this.parent = parent;
		this.pos = pos;
		this.color = color;
		destinationMap = new HashMap<>();
		clock = new HashMap<>();
		color = new int[3];
		for(int i = 0; i < color.length; i++) {
			color[i] = 0;
		}
		
	}
	
	/*
	@Override
	public int hashCode() {
		return Objects.hash(pos.x,pos.y);
	}
	
	@Override
	public boolean equals(Object o) {
		if( !(o instanceof CarArea) ) {
			return false;
		}
		return Objects.equals( ((CarArea) o).pos.x, pos.x) && Objects.equals( ((CarArea) o).pos.y, pos.y);
	}
	*/
	
	// this needs to be done after construcor because of practical reasons
	public void setParent(CarSimulator parent) {
		this.parent = parent;
	}
	
	public Pos pos() {
		return pos;
	}
	public int[] color() {
		return color;
	}
	public void setPos(Pos pos) {
		this.pos = pos;
	}
	public void setColor(int[] color) {
		
		this.color = new int[color.length];
		
		for(int i = 0; i < color.length; i++) {
			this.color[i] = color[i];
		}
		
	}
	
	public Long getDestinationTimeInterval(CarArea dest) {
		return destinationMap.get(dest);
	}
	
	public Set<CarArea> destinationSet() {
		return destinationMap.keySet();
	}
	
	public void mapAreaToInterval(CarArea area, Long interval) {
		destinationMap.put(area, interval);
		clock.put(interval, interval);
		
	}
	
	public void produce(long passedTime) {
		
		// count down the clocks
		for(Map.Entry<Long, Long> intervalCountdown : clock.entrySet() ) {
			
			if( intervalCountdown.getValue() <= 0 ) {
				// reset clock
				clock.put(intervalCountdown.getKey(), intervalCountdown.getKey());
				// check if to produce
				for(Map.Entry<CarArea, Long> areaEntry : destinationMap.entrySet()) {
					if( areaEntry.getValue().equals( intervalCountdown.getKey()) ) {
						// create car from this to dest = areaEntry.getKey()
						parent.produceCar(this, areaEntry.getKey());
					}
				}
				
			}
			// else just count down clock
			else {
				clock.put(intervalCountdown.getKey(), intervalCountdown.getValue() - passedTime);
			}
			
		}
		
	}

}
