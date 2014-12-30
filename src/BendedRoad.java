
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
		gui.displayBendedRoadEdit(groupOffset, connectionMatrix, inputRing, outputRing, diagonal, dir, bend);
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
		// TODO : not right here
		
	}
	
	@Override
	public BendedRoad clone() {
		
		BendedRoad bendedRoadClone = new BendedRoad(dir, bend, redLight, gui);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			bendedRoadClone.addState(stateMatrix.clone());
			
		}
		
		return bendedRoadClone;
		
	}
	
	
}
