
public class TCrossing extends BuildingBlock {

	private int bend;
	private boolean fork;
	
	public TCrossing(int dirRoad, int curveBend, boolean fork,  boolean redLight, GUI gui) {
		
		this.gui = gui;
		
		BuildingBlock tempT_crossing;
		BuildingBlock curve;
		BuildingBlock road =  new Road(dirRoad, redLight, gui);
		
		// if fork means if the curve part is going to be pointing away from the road part
		if( fork ) {
			curve = new Curve(dirRoad, curveBend, redLight, gui);
		}
		// else reverse the curve, making it  an input curve
		else {
			curve = new Curve(Direction.antiDir(dirRoad), - curveBend, redLight, gui);
			curve.revert();
		}
		
		if( redLight ) {
			road.removeState(0);
			curve.removeState(0);
			tempT_crossing = blockSum(road,curve);
		}
		else {
			tempT_crossing = blockFuse(road,curve);
		}
		
		addStateList(tempT_crossing.getStateList());
		
		dir = dirRoad;
		bend = curveBend;
		this.redLight = redLight;
		this.fork = fork;
		
	}
	
	@Override
	public void flip() {
		
		super.flip();
		bend = -bend;
		
	}
	
	@Override
	public void flip(int axis) {
		
		super.flip();
		bend = -bend;
		
	}
	
	@Override
	public TCrossing clone() {
		
		TCrossing TCrossingClone = new TCrossing(dir, bend, fork, redLight, gui);
		TCrossingClone.setDiagonal(diagonal);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			TCrossingClone.addState(stateMatrix.clone());
			
		}
		
		return TCrossingClone;
		
	}

}
