
public class Curve extends BuildingBlock {

	// bend = +-1
	int bend;
	
	public Curve(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir);
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
	public void display() {
		
		gui.displayBlock(connectionRing, diagonal);
		
	}

}
