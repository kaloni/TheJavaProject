
public class BendedRoad extends Curve {

	public BendedRoad(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, bend, redLight, gui);
		
	}
	
	@Override
	public void display(Pos groupOffset) {
		gui.displayBendedRoad(groupOffset, inputRing, outputRing, diagonal, dir, bend);
	}
	
	@Override
	public void displayEdit(Pos groupOffset) {
		gui.displayBendedRoadEdit(groupOffset, connectionMatrix, stateMatrix, inputRing, outputRing, diagonal, dir, bend);
	}
	
	
	@Override
	public void flip() {
		
		super.flip();
		// TODO : outputRing.cycle(-1)
		
	}
	
	@Override
	public void flip(int axis) {
		
		super.flip(axis);
		// TODO : outputRing.cycle(-1)
		
	}
	
	@Override
	public void revert() {
		
		super.revert();
		//reverted = !reverted;
		diagonal = !diagonal;
		
		
	}
	
	@Override
	public BendedRoad clone() {
		
		BendedRoad bendedRoadClone = new BendedRoad(dir, bend, redLight, gui);
		bendedRoadClone.setDiagonal(diagonal);
		bendedRoadClone.setReverted(reverted);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			bendedRoadClone.addState(stateMatrix.clone());
			
		}
		
		return bendedRoadClone;
		
	}
	
	
}
