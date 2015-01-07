import java.util.List;

import processing.core.PVector;


public class Car {

	private List<Pos> path;
	private Pos pos;
	private Pos nextPos;
	private Pos endPos;
	private PVector floatPos;
	private int speed;
	private float minimumDistance;
	private float checkDistance;
	private GUI gui;
	private CarSimulator simulator;
	public static final float deltaPos = 0.001f;
	public static final float roundDist = 0.05f;
	
	public Car(List<Pos> path, CarSimulator simulator, GUI gui) {
		
		this.path = path;
		this.gui = gui;
		this.simulator =  simulator;
		// we assume that there is atleast a path of length 1
		pos = path.get(0);
		nextPos = path.get(1);
		endPos = path.get(path.size() - 1);
		floatPos = new PVector(pos.x, pos.y);
		
		// defaults
		minimumDistance = 0.25f;
		checkDistance = 0.5f;
		
	}
	
	public int speed() {
		return speed;
	}
	public Pos pos() {
		return pos;
	}
	public Pos nextPos() {
		return nextPos;
	}
	public Pos dir() {
		return nextPos.sub(pos);
	}
	public PVector floatPos() {
		return floatPos;
	}
	public PVector nextFloatPos() {
		return new PVector(nextPos.x, nextPos.y);
	}
	public float minimumDistance() {
		return minimumDistance;
	}
	public float checkDistance() {
		return checkDistance;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	// here additional parameters may be added, determining the cars behaviour in traffic
	public void setBehaviour(float minimumDistance) {
		this.minimumDistance = minimumDistance;
	}
	
	// this is not used, since rounding gives unexpected results
	public Pos floatPosToIntPos(PVector floatPos) {
		
		int x = (int) Math.round(floatPos.x);
		int y = (int) Math.round(floatPos.y);
		return new Pos(x,y);
		
	}
	
	public void drive(long passedTime) {
		
		// Can't use rounding (atleast not with floats) --> non expected results
		// The number 0.01f is arbitrary set to make the animation look smooth but not to small
		// such that "closeness" cannot be detected
		//if( floatPosToIntPos(floatPos).equals(nextPos) ) {
		boolean greenLight = simulator.greenLight(dir(), nextPos);
		float distToRedLight =  PVector.dist(floatPos, new PVector(nextPos.x, nextPos.y));
		//System.out.println(distToRedLight);
		if( PVector.dist(floatPos, new PVector(nextPos.x, nextPos.y)) < roundDist) {
			
			// if green light
			if( greenLight ) {
				// if reached destination
				if( nextPos.equals(endPos) ) {
					simulator.toRemove(this);
				}
				// else, put into next block
				else {
					
					path.remove(0);
					pos = path.get(0);
					nextPos = path.get(1);
					floatPos = new PVector(pos.x, pos.y);
					
				}
				
			}
			
		}
		// if clear path, drive
		if( ! simulator.carInfront(this) ) {
			// adding 0.5 because of gui reasons
			if( greenLight || ( ! greenLight && distToRedLight > (roundDist + 0.5f) )) {
				floatPos.x = floatPos.x + passedTime*deltaPos*(nextPos.x - pos.x);
				floatPos.y = floatPos.y + passedTime*deltaPos*(nextPos.y - pos.y);
			}
			
		}
		// draw this car in gui
		gui.drawCar(floatPos);
		
	}

}
