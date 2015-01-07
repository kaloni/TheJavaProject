import java.util.ArrayList;
import java.util.List;


public class Road extends BuildingBlock {
	
	public Road(int dir, boolean redLight, GUI gui) {
		
		super(dir, gui);
		this.redLight = redLight;
		cost = 30;
		
		Matrix<Boolean> stateMatrixRedLight = new Matrix<>(4,4,false);
		Matrix<Boolean> stateMatrixGreenLight = new Matrix<>(4,4,false);
		stateMatrixGreenLight.set(Direction.antiDir(dir), dir, true);
		
		addState(stateMatrixGreenLight);
		if(redLight) {
			addState(stateMatrixRedLight);
		}
		
	}
	
	/*
	@Override
	public void connectWith(Pos relPos, BuildingBlock block) {
		
		DataRing<Boolean> input = block.getInputRing();
		DataRing<Boolean> output = block.getOutputRing();
		int dirDiff = Direction.posToDir(relPos);
		
		if( connectionList.contains(block) ) {
			connectionList.remove(block);
		}
		
		// if no block is bended
		if( relPos.abs() == 1 || (relPos.abs() == Math.sqrt(2) && diagonal) ) {
			
			if( dirDiff == dir ) {
				if( input.get(Direction.antiDir(dir)) ) {
					connectionList.add(block);
				}
			}
			else if( dirDiff == Direction.antiDir(dir) ) {
				if( output.get(dir) ) {
					connectionList.add(block);
				}
			}
			
		}
		
	}
	*/
	
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
