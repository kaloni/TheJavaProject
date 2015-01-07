
public class Curve extends BuildingBlock {
	
	public Curve(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, gui);
		this.gui = gui;
		this.bend = bend;
		cost = 30;
		
		Matrix<Boolean> stateMatrixRedLight = new Matrix<>(4,4,false);
		Matrix<Boolean> stateMatrixGreenLight = new Matrix<>(4,4,false);
		stateMatrixGreenLight.set(Direction.antiDir(dir), Direction.dirBend(dir, bend), true);
		
		addState(stateMatrixGreenLight);
		if(redLight) {
			addState(stateMatrixRedLight);
		}
		
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
		if( reverted ) {
			dir = Direction.dirBend(dir, Direction.LEFT);
		}
		else {
			dir = Direction.dirBend(dir, Direction.RIGHT);
		}
		bend = -bend;
		
	}
	
	@Override
	public Curve clone() {
		
		Curve curveClone = new Curve(dir, bend, redLight, gui);
		curveClone.setDiagonal(diagonal);
		curveClone.setReverted(reverted);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			curveClone.addState(stateMatrix.clone());
			
		}
		
		return curveClone;
		
	}

}
