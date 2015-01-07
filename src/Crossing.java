
public class Crossing extends BuildingBlock {
	
	public Crossing(int dir, int bend, boolean redLight, GUI gui) {
		
		this.gui = gui;
		this.dir = dir;
		this.bend = bend;
		
		BuildingBlock tempCrossing;
		BuildingBlock tempFuse1;
		BuildingBlock tempFuse2;
		BuildingBlock road1 = new Road(dir, redLight, gui);
		BuildingBlock road2 = new Road(Direction.dirBend(dir, bend), redLight, gui);
		BuildingBlock curve1 = new Curve(dir, bend, redLight, gui);
		BuildingBlock curve2 = new Curve(Direction.dirBend(dir, bend), - bend, redLight, gui);
		
		// removeState(0) before
		if( redLight ) {
			// add the roads together to get an alternating redLight switch function
			road1.removeState(1);
			road2.removeState(1);
			curve1.removeState(1);
			curve2.removeState(1);
			tempFuse1 = blockFuse(road1, curve1);
			tempFuse2 = blockFuse(road2, curve2);
			tempCrossing = blockSum(tempFuse1, tempFuse2);
			//tempCrossing = blockSum(road1,road2);
			
		}
		else {
			// fuse the roads together to get a single always open intersection
			tempCrossing = blockFuse(road1,road2, curve1, curve2);
		}
		
		addStateList(tempCrossing.getStateList());
		
	}
	
	@Override
	public void revert() {
		
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.shift();
			stateMatrix.shift();
		}
		
		connectionMatrix.transpose();
		updateRing();
		dir = Direction.antiDir(dir);
		reverted = !reverted;
		
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
