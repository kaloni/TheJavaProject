import java.util.ArrayList;
import java.util.List;

import processing.core.PImage;

// TODO : Make all Boolean[][] --> Matrix<Boolean>
class BuildingBlock implements Comparable<BuildingBlock> {

	protected int stateNum; // numbers the current state
	protected int maxState; // current maximum number of states
	protected Matrix<Boolean> stateMatrix; // describes inner connectivity in a specific state
	protected Matrix<Boolean> connectionMatrix; // Holds the set of accessible states = sum(all state matrices);
	protected List<Matrix<Boolean>> stateList; // holds different possible states
	protected Matrix<Float> flowMatrix; // describes the inner flow of the building block
	protected int speedLimit;
	protected int dir;
	protected boolean diagonal;
	
	public BuildingBlock() {
	
		// Ground state
		dir = 0; // undefined
		stateNum = 0;
		maxState = 0;
		connectionMatrix =  new Matrix<>(4,4,false);
		stateList = new ArrayList<>();
		diagonal = false;
		connectionRing = new DataRing<>(4);
		
	}
	
	public BuildingBlock(int dir) {
		
		this.dir = dir;
		// Ground state
		stateNum = 0;
		maxState = 0;
		connectionMatrix =  new Matrix<>(4,4,false);
		stateList = new ArrayList<>();
		diagonal = false;
		connectionRing = new DataRing<>(4);
		
	}
	
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
	
	public boolean isDiagonal() {
		return diagonal;
	}
	
	public void setDiagonal(boolean bool) {
		diagonal = bool;
	}
	
	public int getDir() {
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
	
	public List<Matrix<Boolean>> getStateList() {
		return stateList;
	}
	
	public void addStateList(List<Matrix<Boolean>> stateList) {
		
		for(Matrix<Boolean> stateMatrix : stateList) {
			
			addState(stateMatrix);
			
		}
		
	}
	
	public void setState(int stateNum) {
		if( 0 <= stateNum && stateNum < maxState) {
			this.stateNum = stateNum;
			stateMatrix = stateList.get(stateNum);
		}
		else {
			System.out.println("Cannot set state : state index out of bounds");
		}
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
			
	}
		
	public Matrix<Boolean> getConnections() {
		return connectionMatrix;
	}
	
	public int maxState() {
		return maxState;
	}
	
	public void setFlow(Matrix<Float> flowMatrix) {
		this.flowMatrix = flowMatrix;
	}
	
	public Matrix<Float> getFlow() {
		return flowMatrix;
	}
	
	// rotates 90 degrees anti-clockwise a specified number of times
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
			
		}
		
		diagonal = !diagonal;
	
		
	}
	
	// flip block around an axis
	public void flip() {
		
		//flip each state
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.exchange(dir);
		}
		
		connectionMatrix.exchange(dir);
		connectionRing.constraintCycle( (dir % 2) == 0 ? DataRing.intOdd : DataRing.intEven);
		
	}
	
	// reverts all current connections
	public void revert() {
		
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.transpose();
		}
		
	}
	
	public int compareTo(BuildingBlock block) {
		
		int otherMaxState = block.maxState();
		int compareInt = (maxState < otherMaxState) ? -1 : 1;
		if( maxState == otherMaxState ) { compareInt = 0;}
		
		return compareInt;
		
	}
	
	// mostly helper method in toString().
	// returns a symmetric version of the current state, that is if there is a connection
	// NORTH --> EAST the symmetric version also contains the connection EAST --> NORTH
	public Matrix<Boolean> getSymmetricState() {
		
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
	
	
	////// GUI TESTING ////////
	
	protected GUI gui;
	protected DataRing<Boolean> connectionRing;

	public void blockSetup() {
		
		connectionRing = new DataRing<>(4);
		
		for(int r = 0; r < 4; r++) {
			connectionRing.set(r, connectionMatrix.getRowSum(r, Matrix.boolOr) || connectionMatrix.getColSum(r, Matrix.boolOr));
		}
		
	}
	
	protected void updateRing() {
		
		for(int r = 0; r < 4; r++) {
			connectionRing.set(r, connectionMatrix.getRowSum(r, Matrix.boolOr) || connectionMatrix.getColSum(r, Matrix.boolOr));
		}
		
	}
	
	public void display() {
		
	}
	
	public DataRing getConnectionRing() {
		return connectionRing;
	}
	
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
	
	/*
	public void setParent(GUI gui) {
		this.gui = gui;
	}
	
	public void display() {
		
		gui.displayBlock(dir, diagonal);
		
	}
	*/
	
}