
public class Crossing extends BuildingBlock {
	
	public Crossing(int dir, int bend, boolean redLight, GUI gui) {
		
		this.gui = gui;
		this.dir = dir;
		this.bend = bend;
		
		BuildingBlock tempCrossing;
		BuildingBlock road1 = new Road(dir, redLight, gui);
		BuildingBlock road2 = new Road(Direction.dirBend(dir, bend), redLight, gui);
		
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
		
	}
	
	@Override
	public Crossing clone() {
		
		Crossing crossingClone = new Crossing(dir, bend, redLight, gui);
		crossingClone.setDiagonal(diagonal);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			crossingClone.addState(stateMatrix.clone());
			
		}
		
		return crossingClone;
		
	}
	
}
