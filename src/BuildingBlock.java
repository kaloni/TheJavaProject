import java.util.ArrayList;
import java.util.List;

import processing.core.PImage;

// TODO : Make all Boolean[][] --> Matrix<Boolean>
class BuildingBlock implements Comparable<BuildingBlock> {

	private PImage image;
	private int stateNum; // numbers the current state
	private int maxState; // current maximum number of states
	private Matrix<Boolean> stateMatrix; // describes inner connectivity in a specific state
	private Matrix<Boolean> connections; // Holds the set of accessible states = sum(all state matrices);
	private List<Matrix<Boolean>> stateList; // holds different possible states
	private Matrix<Float> flowMatrix; // describes the inner flow of the building block
	private int speedLimit;
	private int dir;
	
	public BuildingBlock() {
	
		// Ground state
		dir = 0; // undefined
		stateNum = 0;
		maxState = 0;
		connections =  new Matrix<>(4,4,false);
		stateList = new ArrayList<>();
	
	}
	
	public BuildingBlock(int dir) {
		
		this.dir = dir;
		// Ground state
		stateNum = 0;
		maxState = 0;
		connections =  new Matrix<>(4,4,false);
		stateList = new ArrayList<>();
	
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
			connections = connections.directOp(newStateMatrix, Matrix.boolOr);
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
			connections.directOp( state, Matrix.boolAnd);
		}
		// OR all together to update the connections
		for( Matrix<Boolean> state : stateList) {
			connections.directOp( state,  Matrix.boolOr);
		}
			
	}
		
	public Matrix<Boolean> getConnections() {
		return connections;
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
		
		// rotate each state
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.shift();
		}
		connections.shift();
		// bend the inner direction when rotating
		dir = Direction.dirBend(dir,-1);
		
	}
	
	// flip block around an axis
	public void flip() {
		
		//flip each state
		for( Matrix<Boolean> stateMatrix : stateList ) {
			stateMatrix.exchange(dir);
		}
		
		connections.exchange(dir);
		
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
	
}
