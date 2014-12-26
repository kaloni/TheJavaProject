
public class TCrossing extends BuildingBlock {

	public TCrossing(int dirRoad, int curveBend, boolean fork,  boolean redLight, GUI gui) {
		
		this.gui = gui;
		
		BuildingBlock tempT_crossing;
		BuildingBlock curve;
		BuildingBlock road =  new Road(dirRoad, redLight, gui);
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
		
	}
	
	public void display() {
		
		gui.displayBlock(connectionRing, diagonal);
		
	}

}
