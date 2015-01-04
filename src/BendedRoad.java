
public class BendedRoad extends Curve {

	private boolean flipped;
	
	public BendedRoad(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, bend, redLight, gui);
		
	}
	
	
	@Override
	public Matrix<Boolean> getOutputPattern() {
		
		Matrix<Boolean> patternMatrix = new Matrix<>(3,3, false);
		
		for(int i = 0; i < 4; i++) {
				
			if( outputRing.get(i) ) {
			
				if( diagonal ) {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate(2);
				patternPos.translate(1, 1);
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				else {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate();
				patternPos.translate(1,1);
				// need to flip around x axis because GUI coordinate basis is weird
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				
			}
		}
		
		return patternMatrix;
		
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
		//outputRing.cycle(-1);
		flipped = !flipped;
		
	}
	
	@Override
	public void flip(int axis) {
		
		super.flip(axis);
		//outputRing.cycle(-1);
		flipped = !flipped;
		
	}
	
	@Override
	public void revert() {
		
		if( reverted ) {
			for(int i = 0; i < 5; i++) {
				rotate();
			}
		}
		else {
			for(int i = 0; i < 3; i++) {
				rotate();
			}
		}
		flip();
		reverted = !reverted;
		
		
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
