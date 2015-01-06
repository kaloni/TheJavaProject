
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
			
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				
				if( reverted && flipped ) {
					//patternPos.rotate(6);
					//patternPos.rotate(0);
				}
				else if( (reverted && ! flipped) || flipped ) {
					patternPos.rotate(6);
				}
				
				if( diagonal ) {
				
				//Pos patternPos = Direction.dirToPos(i);
				//patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate(2);
				patternPos.translate(1, 1);
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				
				else {
					
				//Pos patternPos = Direction.dirToPos(i);
				//patternPos.setRotationBounds(-1, 1, -1, 1);
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
		
		
		if( flipped ) {
			
			if( reverted ) {
				for(int i = 0; i < 3; i++) {
					rotate();
				}
			}
			else {
				for(int i = 0; i < 5; i++) {
					rotate();
				}
			}
			flip();
			flipped = true;
			reverted = !reverted;
			
			
		}
		
		else {
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
			flipped = false;
			reverted = !reverted;
		}
		
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
