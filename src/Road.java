
public class Road extends BuildingBlock {

	public Road(int dir, boolean redLight, GUI gui) {
		
		super(dir);
		this.gui = gui;
		
		Matrix<Boolean> stateMatrixRedLight = new Matrix<>(4,4,false);
		Matrix<Boolean> stateMatrixGreenLight = new Matrix<>(4,4,false);
		stateMatrixGreenLight.set(Direction.antiDir(dir), dir, true);
		
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
