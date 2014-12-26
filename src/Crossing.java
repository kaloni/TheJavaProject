
public class Crossing extends BuildingBlock {

	public Crossing(int dirX, int dirY, boolean redLight, GUI gui) {
		
		this.gui = gui;
		
		BuildingBlock tempCrossing;
		BuildingBlock road1 = new Road(dirX, redLight, gui);
		BuildingBlock road2 = new Road(dirY, redLight, gui);
		
		if( redLight ) {
			// add the roads together to get an alternating redLight switch function
			road1.removeState(0);
			road2.removeState(0);
			tempCrossing = blockSum(road1,road2);
			
		}
		else {
			// fuse the roads together to get a single always open intersection
			tempCrossing = blockFuse(road1,road2);
		}
		
		addStateList(tempCrossing.getStateList());
		
		dir = dirX;
		
	}

	public void display() {
		
		gui.displayBlock(connectionRing, diagonal);
		
	}
	
}
