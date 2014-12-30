
public class Curve extends BuildingBlock {

	public Curve(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, gui);
		this.gui = gui;
		this.bend = bend;
		
		Matrix<Boolean> stateMatrixRedLight = new Matrix<>(4,4,false);
		Matrix<Boolean> stateMatrixGreenLight = new Matrix<>(4,4,false);
		stateMatrixGreenLight.set(Direction.antiDir(dir), Direction.dirBend(dir, bend), true);
		
		if(redLight) {
			addState(stateMatrixRedLight);
		}
		addState(stateMatrixGreenLight);
		
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
	public Curve clone() {
		
		Curve curveClone = new Curve(dir, bend, redLight, gui);
		curveClone.setDiagonal(diagonal);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			curveClone.addState(stateMatrix.clone());
			
		}
		
		return curveClone;
		
	}

}
