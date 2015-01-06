import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Class that keeps track on when to produce new cars
 * Visually this is the areas from which cars depart from and arrive at
 */

public class CarArea {

	private Pos pos;
	private int[] color;
	private HashMap<CarArea, Long> intervalMap;
	private HashMap<Long, Long> clock;
	private CarSimulator parent;
	
	public CarArea(Pos pos, int[] color) {
		
		this.parent = parent;
		this.pos = pos;
		this.color = color;
		intervalMap = new HashMap<>();
		clock = new HashMap<>();
		
	}
	
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
	
	public Long getTimeInterval(CarArea dest) {
		return intervalMap.get(dest);
	}
	
	public void mapAreaToInterval(CarArea area, Long interval) {
		intervalMap.put(area, interval);
		clock.put(interval, interval);
		
	}
	
	public void produce(long passedTime) {
		
		// count down the clocks
		for(Map.Entry<Long, Long> intervalCountdown : clock.entrySet() ) {
			
			if( intervalCountdown.getValue() <= 0 ) {
				// reset clock
				clock.put(intervalCountdown.getKey(), intervalCountdown.getKey());
				// check if to produce
				for(Map.Entry<CarArea, Long> areaEntry : intervalMap.entrySet()) {
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
