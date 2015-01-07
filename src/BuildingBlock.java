import java.util.ArrayList;
import java.util.List;


// TODO : Make all Boolean[][] --> Matrix<Boolean>
class BuildingBlock implements Comparable<BuildingBlock> {

	protected int stateNum; // numbers the current state
	protected int maxState; // current maximum number of states
	protected Matrix<Boolean> stateMatrix; // describes inner connectivity in a specific state
	protected Matrix<Boolean> connectionMatrix; // Holds the set of accessible states = sum(all state matrices);
	protected DataRing<Boolean> connectionRing;
	protected DataRing<Boolean> inputRing;
	protected DataRing<Boolean> outputRing;
	protected List<Matrix<Boolean>> stateList; // holds different possible states
	protected Matrix<Float> flowMatrix; // describes the inner flow of the building block
	protected int speedLimit;
	protected int dir;
	protected int bend; // bend = +-1
	protected boolean reverted;
	protected boolean redLight;
	protected boolean diagonal;
	protected Pos groupOrigin;
	protected int cost;
	
	List<BuildingBlock> connectionList;
	
	protected GUI gui;
	
	////////// CONSTRUCTORS (2) //////////
	
	public BuildingBlock() {
	
		// Ground state
		dir = 0; // undefined
		stateNum = 0;
		maxState = 0;
		connectionMatrix =  new Matrix<>(4,4,false);
		connectionRing = new DataRing<>(4);
		inputRing = new DataRing<>(4);
		outputRing = new DataRing<>(4);
		stateList = new ArrayList<>();
		diagonal = false;
		groupOrigin = new Pos(0,0);
		connectionList = new ArrayList<>();
		
	}
	
	public BuildingBlock(int dir, GUI gui) {
		
		this.gui = gui;
		this.dir = dir;
		// Ground state
		stateNum = 0;
		maxState = 0;
		connectionMatrix =  new Matrix<>(4,4,false);
		connectionRing = new DataRing<>(4);
		inputRing = new DataRing<>(4);
		outputRing = new DataRing<>(4);
		stateList = new ArrayList<>();
		diagonal = false;
		groupOrigin = new Pos(0,0);
		connectionList = new ArrayList<>();
		
	}
	
	////////// ////////// //////////
	
	/////////// PROTECTED METHODS //////////////
	
	/*
	 * blockFuse and blockSum is used to mix different blocks together to create new ones
	 * they are used in the constructors in subclasses
	 */
	
	protected BuildingBlock blockFuse(BuildingBlock... blocks) {
		
		BuildingBlock fusedBlock = new BuildingBlock();
		int maxState = BuildingBlock.max(blocks).maxState();
		// "Fuse" (OR) the matrices together
		for(int stateNum = 0; stateNum < maxState; stateNum++) {
			
			Matrix<Boolean> tempFusedMatrix = new Matrix<Boolean>(4,4,false);
			
			for( BuildingBlock block : blocks ) {
				
				// Sum up a state over all blocks
				tempFusedMatrix = tempFusedMatrix.directOp(block.getState(stateNum), Matrix.boolOr);
				
			}
			
			// add one state, summed over all blocks to the fused block
			fusedBlock.addState(tempFusedMatrix);
			
		}
		
		return fusedBlock;
	}
	
	protected BuildingBlock blockSum(BuildingBlock... blocks) {
		
		BuildingBlock blockSum = new BuildingBlock();
		// "Add" the matrices together
		for( BuildingBlock block : blocks ) {
			for(int stateNum = 0; stateNum < block.maxState(); stateNum++) {
				blockSum.addState(block.getState(stateNum));
			}
		}
		
		return blockSum;
		
	}
	
	protected void updateRing() {
		
		for(int r = 0; r < 4; r++) {
			connectionRing.set(r, connectionMatrix.getRowSum(r, Matrix.boolOr) || connectionMatrix.getColSum(r, Matrix.boolOr));
			inputRing.set(r, connectionMatrix.getRowSum(r, Matrix.boolOr));
			outputRing.set(r, connectionMatrix.getColSum(r, Matrix.boolOr));
			
		}
		
	}
	
	////////// ////////// //////////
	
	// max compares blocks by their amount of states
	public static BuildingBlock max(BuildingBlock... blocks) {
		
		// Create smallest possible block (according to compareTo)
		BuildingBlock maxBlock = new BuildingBlock();
		// Search for biggest block (most accessible states)
		for( BuildingBlock block : blocks ) {
			// if block > maxBlock
			if( block.compareTo(maxBlock) == 1 ) {
				maxBlock = block;
			}
		}
		
		return maxBlock;
		
	}
	
	public BuildingBlock clone() {
		
		BuildingBlock blockClone = new BuildingBlock(dir, gui);
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			blockClone.addState(stateMatrix.clone());
			
		}
		
		return blockClone;
		
	}
	
	public int compareTo(BuildingBlock block) {
		
		int otherMaxState = block.maxState();
		int compareInt = (maxState < otherMaxState) ? -1 : 1;
		if( maxState == otherMaxState ) { compareInt = 0;}
		
		return compareInt;
		
	}
	
	public Pos getGroupOrigin() {
		
		return groupOrigin;
		
	}
	
	public void translateGroupOrigin(Pos groupOrigin) {
		
		this.groupOrigin = groupOrigin;
		
	}
	
	public int cost() {
		return diagonal ? 7*cost/5 : cost;
	}
	
	// this is only to ensure that one can call display on an arbitrary BuildingBlock
	// Implementation is given in each subclass
	public void display(Pos groupOffset) {
		gui.displayBlock(groupOffset, inputRing, outputRing, diagonal);
	}
	
	public void displayEdit(Pos groupOffset) {
		gui.displayBlockEdit(groupOffset, connectionMatrix, stateMatrix, inputRing, outputRing, diagonal);
	}
	
	public boolean isDiagonal() {
		return diagonal;
	}
	
	public void setDiagonal(boolean bool) {
		diagonal = bool;
	}
	
	public int dir() {
		return dir;
	}
	
	public void setDir(int dir) {
		
		if( dir < 4 && dir >= 0 ) {
			
			this.dir = dir;
			
		}
		
	}
	
	public Matrix<Boolean> getCurrentState() {
		return stateMatrix;
	}
	
	public Matrix<Boolean> getState(int stateNum) {
		if( 0 <= stateNum && stateNum < maxState) {
			return stateList.get(stateNum);
		}
		else {
			System.out.println("Can not get state : index out of bounds");
			return new Matrix<Boolean>(4,4,false);
		}
	}
	
	public int currentStateNumber() {
		return stateNum;
	}
	
	public List<Matrix<Boolean>> getStateList() {
		return stateList;
	}
	
	// TODO : this should be used in getInputPattern and getOutputPattern as well
	private Matrix<Boolean> getPatternMatrix(DataRing<Boolean> ring) {
		
		Matrix<Boolean> patternMatrix = new Matrix<>(3,3, false);
		
		for(int i = 0; i < 4; i++) {
			
			if( ring.get(i) ) {
			
				if( diagonal ) {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate();
				patternPos.translate(1, 1);
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				else {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.translate(1,1);
				// need to flip around x axis because GUI coordinate basis is weird
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				
			}
		}
		
		return patternMatrix;
		
	}
	
	public Matrix<Boolean> getCurrentInputPattern() {
		
		Matrix<Boolean> patternMatrix = new Matrix<>(3,3, false);
		DataRing<Boolean> currentInputRing = new DataRing<>(4);
		
		for(int i = 0; i < 4; i++) {
			currentInputRing.set(i, stateMatrix.getRowSum(i, Matrix.boolOr));
		}
		
		return getPatternMatrix(currentInputRing);
		
	}
	
	// I/O patters is used for checking connectivity with other blocks
	public Matrix<Boolean> getInputPattern() {
		
		Matrix<Boolean> patternMatrix = new Matrix<>(3,3, false);
		
		for(int i = 0; i < 4; i++) {
				
			if( inputRing.get(i) ) {
			
				if( diagonal ) {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate();
				patternPos.translate(1, 1);
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				else {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.translate(1,1);
				// need to flip around x axis because GUI coordinate basis is weird
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				
			}
		}
		
		return patternMatrix;
		
	}
	
	public Matrix<Boolean> getOutputPattern() {
		
		Matrix<Boolean> patternMatrix = new Matrix<>(3,3, false);
		
		for(int i = 0; i < 4; i++) {
				
			if( outputRing.get(i) ) {
			
				if( diagonal ) {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.setRotationBounds(-1, 1, -1, 1);
				patternPos.rotate();
				patternPos.translate(1, 1);
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				else {
					
				Pos patternPos = Direction.dirToPos(i);
				patternPos.translate(1,1);
				// need to flip around x axis because GUI coordinate basis is weird
				patternMatrix.set(patternPos.y, patternPos.x, true);
					
				}
				
			}
		}
		
		return patternMatrix;
		
	}
	
	public boolean hasOutputConnection(Pos pos) {
		
		Pos matrixPos = pos.clone();
		matrixPos.translate(1, 1);
		Matrix<Boolean> outputPattern = getOutputPattern();
		
		if( outputPattern.get(matrixPos.y, matrixPos.x) ) {
			return true;
		}
		return false;
		
	}
	
	public boolean checkConnect(Pos relPos, BuildingBlock block) {
		
		Matrix<Boolean> outputPattern = getOutputPattern();
		Matrix<Boolean> inputPattern = block.getInputPattern();
		inputPattern.totalFlip();
		relPos.translate(1, 1);
		
		if( connectionList.contains(block) ) {
			connectionList.remove(block);
		}
		
		Matrix<Boolean> connectPattern = outputPattern.directOp(inputPattern, Matrix.boolAnd);
		boolean connected = connectPattern.get(relPos.y, relPos.x);
		
		return connected;
		
	}
	
	public boolean connectedTo(BuildingBlock block) {
		
		if( connectionList.contains(block) ) {
			return true;
		}
		return false;
		
	}
	
	public void connectWith(Pos relPos, BuildingBlock block) {
		
		if( checkConnect(relPos, block) ) {
			connectionList.add(block);
		}
		
	}
	
	public void clearConnections() {
		connectionList.clear();
	}
	public int connections() {
		return connectionList.size();
	}
	public void removeConnection(BuildingBlock block) {
		connectionList.remove(block);
	}
	
	// adds a state, makes sure that the connections is updated
	public void addState(Matrix<Boolean> newStateMatrix) {
		if( newStateMatrix.rows() == 4 && newStateMatrix.cols() == 4) {
			maxState++;
			stateList.add(newStateMatrix);
			// If no state yet, set a ground state
			if( maxState == 1) {
				stateMatrix = newStateMatrix;
			}
			connectionMatrix = connectionMatrix.directOp(newStateMatrix, Matrix.boolOr);
			updateRing();
		}
		else {
			System.out.println("Cannot add new state : state dimension mismatch");
		}
			
	}
	
	public void setState(int stateNum) {
		if( 0 <= stateNum && stateNum < maxState) {
			this.stateNum = stateNum;
			stateMatrix = stateList.get(stateNum);
			System.out.println(stateMatrix);
		}
		else {
			System.out.println("Cannot set state : state index out of bounds");
		}
	}
	
	public void addStateList(List<Matrix<Boolean>> stateList) {
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			addState(stateMatrix);
			
		}
		
	}
	
	// removes a state, makes sure that the connections is updated
	public void removeState(int stateNum) {
		stateList.remove(stateNum);
		maxState--;
			
		// AND all remaining states together to get rid of any non-true connections
		for( Matrix<Boolean> state : stateList ) {
			connectionMatrix.directOp( state, Matrix.boolAnd);
		}
		// OR all together to update the connections
		for( Matrix<Boolean> state : stateList) {
			connectionMatrix.directOp( state,  Matrix.boolOr);
		}
		
		updateRing();
			
	}
		
	public Matrix<Boolean> getConnectionMatrix() {
		return connectionMatrix;
	}
	
	public DataRing<Boolean> getConnectionRing() {
		return connectionRing;
	}
	public DataRing<Boolean> getInputRing() {
		return inputRing;
	}
	public DataRing<Boolean> getOutputRing() {
		return outputRing;
	}
	
	public int maxState() {
		return maxState;
	}
	
	public Matrix<Float> getFlowMatrix() {
		return flowMatrix;
	}
	
	public void setFlowMatrix(Matrix<Float> flowMatrix) {
		this.flowMatrix = flowMatrix;
	}
	
	// rotates the block 45 degrees
	public void rotate() {
		
		if( diagonal ) {
			
			// rotate each state
			for( Matrix<Boolean> stateMatrix : stateList ) {
				stateMatrix.shift();
			}
			connectionMatrix.shift();
			// bend the inner direction when rotating
			dir = Direction.dirBend(dir,+1);
			connectionRing.cycle(-1);
			inputRing.cycle(-1);
			outputRing.cycle(-1);
			
		}
		
		diagonal = !diagonal;
		
	}
	
	// flip block around the blocks axis
	public void flip() {
		
		
		//flip each state
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.exchange(dir);
		}
		
		connectionMatrix.exchange(dir);
		connectionRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		inputRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		outputRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		
	}
	
	public void flip(int axis) {
		
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.exchange(axis);
		}
				
		connectionMatrix.exchange(axis);
		connectionRing.constraintCycle( (axis % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		inputRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		outputRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		
	}
	
	// reverts all current connections
	public void revert() {
		
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.transpose();
		}
		connectionMatrix.transpose();
		updateRing();
		dir = Direction.antiDir(dir);
		reverted = !reverted;
		
	}
	
	
	////////// HELPER METHODS //////////
	
	// Helper method in toString().
	// Returns a symmetric version of the current state, that is if there is a connection
	// NORTH --> EAST the symmetric version also contains the connection EAST --> NORTH
	private Matrix<Boolean> getSymmetricState() {
		
		Matrix<Boolean> symmetricMatrix = stateMatrix.clone();
		
		symmetricMatrix.transpose();
		//symmetricMatrix = symmetricMatrix.directOp(stateMatrix.clone(), Matrix.boolOr);
		symmetricMatrix = symmetricMatrix.directOp(stateMatrix, Matrix.boolOr);
		return symmetricMatrix;
		
	}
	
	// Some quite messy hard coded  toString. Very useful during coding to check if one's messy
	// algoritms gives the results expected
	@Override
	public String toString() {
		
		String blockString = new String();
		Matrix<Boolean> symState = getSymmetricState();
		
		blockString = blockString + "   ";
		blockString = blockString + ( (stateMatrix.getColSum(Direction.NORTH, Matrix.boolOr)) ? "*" : " ");
		blockString = blockString +  "\n";
		
		blockString = blockString + "  ";
		blockString = blockString + ( ( symState.get(Direction.NORTH, Direction.WEST) ) ? "/" : " " );
		blockString = blockString + ( ( symState.get(Direction.NORTH, Direction.SOUTH) )? "|" : "" );
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.EAST) ? "\\" : " " );
		blockString = blockString + " \n";
		
		blockString = blockString + ( (stateMatrix.getColSum(Direction.WEST, Matrix.boolOr)) ? "*" : " ");
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.WEST) ? "/" : "" );
		blockString = blockString + ( symState.get(Direction.WEST, Direction.EAST) ? "__" : "  " );
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.SOUTH) ? "|" : "" );
		blockString = blockString + ( symState.get(Direction.WEST, Direction.EAST) ? "__" : "  " );
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.EAST) ? "\\" : "" );
		blockString = blockString + ( (stateMatrix.getColSum(Direction.EAST, Matrix.boolOr)) ? "*" : " ");
		blockString = blockString + "\n";
		
		blockString = blockString + " ";
		blockString = blockString + ( symState.get(Direction.WEST, Direction.SOUTH) ? "\\ " : "  " );
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.SOUTH) ? "|" : "" );
		blockString = blockString + ( symState.get(Direction.EAST, Direction.SOUTH) ? " /" : "  " );
		blockString = blockString + "\n";
		
		blockString = blockString + " ";
		blockString = blockString + ( symState.get(Direction.WEST, Direction.SOUTH) ? " \\" : "  " );
		blockString = blockString + ( symState.get(Direction.NORTH, Direction.SOUTH) ? "|" : "" );
		blockString = blockString + ( symState.get(Direction.EAST, Direction.SOUTH) ? "/ " : "  " );
		blockString = blockString +  "\n";
		
		blockString = blockString + "   ";
		blockString = blockString + ( (stateMatrix.getColSum(Direction.SOUTH, Matrix.boolOr)) ? "*" : "  ");
		
		return blockString;
		
	}
	
}