
public class TCrossing extends BuildingBlock {

	private int bend;
	private boolean fork;
	private boolean reverted;
	
	public TCrossing(int dirRoad, int curveBend, boolean fork,  boolean redLight, GUI gui) {
		
		this.gui = gui;
		cost = 40;
		
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
		
		// removeState(0), when the redLight order is reversed in road and curve
		if( redLight ) {
			road.removeState(1);
			curve.removeState(1);
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
	
	public void setReverted(boolean reverted) {
		this.reverted = reverted;
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
	public void revert() {
		
		super.revert();
		reverted = !reverted;
		fork = !fork;
		bend = -bend;
		
	}
	
	@Override
	public TCrossing clone() {
		
		TCrossing TCrossingClone = new TCrossing(dir, bend, fork, redLight, gui);
		TCrossingClone.setDiagonal(diagonal);
		TCrossingClone.setReverted(reverted);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			TCrossingClone.addState(stateMatrix.clone());
			
		}
		
		return TCrossingClone;
		
	}

}
