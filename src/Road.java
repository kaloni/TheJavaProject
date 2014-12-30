
public class Road extends BuildingBlock {
	
	public Road(int dir, boolean redLight, GUI gui) {
		
		super(dir, gui);
		this.redLight = redLight;
		
		Matrix<Boolean> stateMatrixRedLight = new Matrix<>(4,4,false);
		Matrix<Boolean> stateMatrixGreenLight = new Matrix<>(4,4,false);
		stateMatrixGreenLight.set(Direction.antiDir(dir), dir, true);
		
		if(redLight) {
			addState(stateMatrixRedLight);
		}
		addState(stateMatrixGreenLight);
		
	}
	
	@Override
	public Road clone() {
		
		Road roadClone = new Road(dir, redLight, gui);
		roadClone.setDiagonal(diagonal);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			roadClone.addState(stateMatrix.clone());
			
		}
		
		return roadClone;
		
	}
	
}
