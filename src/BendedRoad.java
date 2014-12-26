
public class BendedRoad extends Curve {

	public BendedRoad(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, bend, redLight, gui);
		
	}
	
	public void display() {
		
		gui.displayBendedRoad(connectionRing, diagonal, dir, bend);
	}
	
	@Override
	public void flip() {
		
		super.flip();
		bend = -bend;
		
		
	}
	
	
	
	
	
	/*
	public void setCurvature(int curvature) {
		
		if( curvature > 3 || curvature < 3 ) {
			this.curvature = curvature;
			
			if(curvature == 1 || curvature == -1) {
				fork = true;
			}
			
		}
		
	}
	
	public void changeCurvature(int delta) {
		
		if( delta < 0 ) {
			setCurvature(curvature - 1);
		}
		else if( delta > 0 ) {
			setCurvature(curvature + 1);
		}
		
	}
	*/
	
	
	
	
}
