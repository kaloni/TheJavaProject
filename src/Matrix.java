import java.util.Iterator;
import java.util.NoSuchElementException;


public class Matrix<E> implements Iterable<E> {
	
	private E[][] innerMatrix;
	private int rows;
	private int cols;

	
	////////////// MATRIX CONSTRUCTORS (3) ///////////////
	
	// constructs a new matrix according to some array pattern
	public Matrix(E[][] innerMatrix) {
		this.innerMatrix = innerMatrix;
		rows = innerMatrix.length;
		cols = innerMatrix[0].length;
	}
	
	// constructs a rows * cols matrix with default value
	public Matrix(int rows, int cols, E defaultValue) {
		
		this.rows = rows;
		this.cols = cols;
		innerMatrix = (E[][]) new Object[rows][cols];
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				innerMatrix[r][c] = defaultValue;
			}
		}
		
	}
	
	// Constructs a rows x cols (undef) matrix
	public Matrix(int rows, int cols) {
		
		this.rows = rows;
		this.cols = cols;
		
		innerMatrix = (E[][]) new Object[rows][cols];
		
	}
	
	///////////////////////////////////////////////////////
	
	/////////  functional passing using lambda expressions ///////////////
	
	private E binaryOp(E a, E b, BinaryOperation<E> op) {
		return op.operation(a, b);
	}
	
	private interface BinaryOperation<E> {
		E operation(E a, E b);
	}
	
	// STATIC OPERATIONS
	public static BinaryOperation<Boolean> boolOr = (a,b) -> a || b;
	public static BinaryOperation<Boolean> boolAnd = (a,b) -> a && b;
	public static BinaryOperation<Integer> intAdd = (a,b) -> a + b;
	public static BinaryOperation<Float> floatAdd= (a,b) -> a + b;
	public static BinaryOperation<Integer> intMult= (a,b) -> a * b;
	public static BinaryOperation<Float> floatMult= (a,b) -> a * b;
	
	///////////////////////////////////////////////////////////////////////
	
	public MatrixIterator iterator() {
		return new MatrixIterator();
	}
	
	class MatrixIterator implements Iterator<E> {
		
		int matrixSize = rows*cols -1;
		
		private int currentIndex;
		
		MatrixIterator() {
			currentIndex = -1;
		}
		
		public boolean hasNext() {
			
			if( currentIndex < matrixSize ) {
				return true;
			}
			return false;
		}
		
		public E next() throws NoSuchElementException {
			if( hasNext() ) {
				currentIndex++;
				return innerMatrix[currentIndex/cols % rows][currentIndex % cols];
			}
			else {
				throw new NoSuchElementException();
			}
		}
		
	}
	
	
	public int rows() {
		return rows;
	}
	
	public int cols() {
		return cols;
	}
	
	public E get(int i, int j) {
		if( i < rows && j < cols && i >= 0 && j >= 0) {
			return innerMatrix[i][j];
		}
		else {
			System.out.println("Index out of bound");
			return (E) new Object();
		}
	}
	
	public void set(int i, int j, E val) {
		if( i < rows && j < cols && i >= 0 && j >= 0) {
			innerMatrix[i][j] = val;
		}
		else {
			System.out.println("Index out of bounds");
		}
	}

	public E[] getRow(int r) {
		E[] row = (E[]) new Object[cols];
		if( r < rows && r >= 0) {
			for(int c = 0; c < cols; c++) {
				row[c] = get(r,c);
			}
		}
		else {
			System.out.println("Row index out of bounds");
		}
		return row;
	}

	public E[] getCol(int c) {
		E[] col = (E[]) new Object[rows];
		if( c < cols && c >= 0) {
			for(int r = 0; r < rows; r++) {
				col[r] = get(r,c);
			}
		}
		else {
			System.out.println("Column index out of bounds");
		}
		return col;
	}
	
	// Sums up a row with corresponding operation
	public E getRowSum(int row, BinaryOperation<E> opAdd) {
		
		// init for !null
		E accum;
		
		if( row < rows && row >= 0) {
			accum = getRow(row)[0];
			for(int c = 1; c < cols; c++) {
				accum = binaryOp( accum, get(row,c), opAdd);
			}
			return accum;
		}
		else {
			System.out.println("Row index out of bounds");
		}
		
		return (E) new Object();
		
	}
	
	// Sums up a column with corresponding operation
	public E getColSum(int col, BinaryOperation<E> opAdd) {
		
		// init for !null
		E accum;
		
		if( col < cols && col >= 0) {
			accum = getCol(col)[0];
			for(int r = 1; r < rows; r++) {
				accum = binaryOp( accum, get(r,col), opAdd);
			}
			return accum;
		}
		else {
			System.out.println("Column index out of bounds");
		}
		
		return (E) new Object();
		
	}
	
	public void setRow(int r, E[] newRow) {
		if( r < rows && r >= 0 && newRow.length == cols ) {
			if( newRow.length == cols ) {
				for(int c = 0; c < cols; c++) {
					innerMatrix[r][c] = newRow[c];
				}
			}
		}
		else {
			System.out.println("Row size mismatch");
		}
	}
	
	public void setCol(int c, E[] newCol) {
		if( c < cols && c >= 0 && newCol.length == rows ) {
			if( newCol.length == rows ) {
				for(int r = 0; r < cols; r++) {
					innerMatrix[r][c] = newCol[r];
				}
			}
		}
		else {
			System.out.println("Column size mismatch");
		}
	}
	
	// "clones" the matrix (up to identical inner matrices)
	public Matrix<E> clone() {
			
		E[][] innerMatrixClone = cloneInnerMatrix();
		return new Matrix<E>(innerMatrixClone);
			
	}
	
	// Generic matrix direct operation
	// Returns this is dimensions mismatch
	public Matrix<E> directOp(Matrix<E> matrix, BinaryOperation<E> directOp) {
		
		E[][] tempArray = (E[][]) new Object[rows][cols];
		
		if( rows == matrix.rows() && cols == matrix.cols() ) {
			Matrix<E> opMatrix = new Matrix<E>(tempArray);
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					opMatrix.set(r,c, binaryOp( this.get(r,c) , matrix.get(r,c) , directOp ));
				}
			}
			return opMatrix;
		}
		else {
			System.out.println("Matrix dimensions does not match");
			return this;
		}
		
	}
	
	 // Generic matrix multiplication, returns this if dimensions mismatch
	 // Returns this is dimensions mismatch
	public Matrix<E> matrixMult(Matrix<E> matrix, BinaryOperation<E> opAdd, BinaryOperation<E> opMult) {
		
		if( cols == matrix.rows() ) {
			
			int newCols = matrix.cols;
			E[][] tempArray = (E[][]) new Object[rows][newCols];
			Matrix<E> multMatrix = new Matrix<E>(tempArray);
			
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < newCols; c++) {
					// init first element in each iteration to avoid null
					multMatrix.set(r, c, binaryOp( get(r,0), matrix.get(0,c), opMult));
					// iteratie through the rest, addiding results together (requires first element not null)
					for(int i = 1; i < cols; i++) {
						multMatrix.set(r, c, binaryOp( multMatrix.get(r,c) , binaryOp( get(r,i), matrix.get(i,c), opMult) , opAdd));
					}
				}
			}
			return multMatrix;
		}
		else {
			System.out.println("Matrix dimensions does not match");
			return this;
		}
	}
	
	public Matrix<Float> floatMult(Float number) {
		
		Matrix<Float> tempMatrix = new Matrix<>(rows, cols);
		
		if( get(0,0) instanceof Float ) {
						
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					tempMatrix.set(r, c, number*( (Float) get(r,c) ) );
				}
			}
			
		}
		else if( get(0,0) instanceof Integer ) {
			
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					tempMatrix.set(r, c, number*( (Integer) get(r,c) ) );
				}
			}
			
		}
		
		return tempMatrix;
		
	}
	
	// anti-clockwise rotation of matrix
	public void rotate() {
		
		Matrix<E> tempMatrix = new Matrix<E>(cols,rows);
			
		for(int c = 0; c < cols; c++) {
			tempMatrix.setRow(c, getCol(cols - 1 - c));
		}
		
		setInnerMatrix( tempMatrix.cloneInnerMatrix() );
			
	}
	
	// flip matrix around axis
	public void flip(int axis) {
		
		Matrix<E> tempMatrix = clone();
		
		if( axis == Direction.EAST || axis == Direction.WEST ) {
			for(int i = 0; i < rows; i++) {
				tempMatrix.setRow(i, getRow(rows - 1 - i));
			}
		}
		else if( axis == Direction.NORTH || axis == Direction.SOUTH ) {
			for(int i = 0; i < cols; i++) {
				tempMatrix.setCol(i, getCol(cols - 1 - i));
			}
		}
		else {
			System.out.println("Illegal axis argument : Matrix.flip(int axis)");
		}
		
		innerMatrix = tempMatrix.cloneInnerMatrix();
		
	}
	
	public void transpose() {
		
		Matrix<E> tempMatrix = new Matrix<E>(cols,rows);
		
		for(int c = 0; c < cols; c++) {
			tempMatrix.setRow(c, getCol(c));
		}
		
		innerMatrix = tempMatrix.cloneInnerMatrix();
		
	}
	
	// methods that realizes the rotation of a building block
	// maps (r,c) --> (r + 1 % 4, c + 1 & 4)
	public void shift() {
		
		Matrix<E> tempMatrix = clone();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				set(r, c, tempMatrix.get(Direction.dirBend(r,-1), Direction.dirBend(c,-1)));
			}
		}
	}
	
	// exchange realizes the flip of a block around an axis
	// maps if(axis == EAST||WEST) : (EW,NS) --> (EW,SN) && (NS,EW) --> (SN,EW)
	// else : (EW,NS) --> (WE,NS) && (NS,EW) --> (NS, WE)
	// Very messy code, did not find any other way to do this...
	public void exchange(int axis) {
		
		Matrix<E> tempMatrix = clone();
		
		if( axis == Direction.EAST || axis == Direction.WEST ) {
		
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					
					if( r != c ) {
					
						if( r == Direction.NORTH || r == Direction.SOUTH ) {
							if( r == Direction.antiDir(c) ) {
								set(r, c, tempMatrix.get(c, r));
							}
							else {
								set(r, c, tempMatrix.get(Direction.antiDir(r), c));
							}
						}
						else {
							if( !(r == Direction.antiDir(c)) ) {
								set(r, c, tempMatrix.get(r, Direction.antiDir(c)));
							}
							
						}
					}
					
				}
			}
			
		}
		else if( axis == Direction.NORTH || axis == Direction.SOUTH ) {
				
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					
					if( r != c ) {
						
						if( r == Direction.NORTH || r == Direction.SOUTH ) {
							if( !(r == Direction.antiDir(c)) && r != c) {
								set(r, c, tempMatrix.get(r, Direction.antiDir(c)));
							}
						}
						else {
							if( r == Direction.antiDir(c) ) {
								set(r, c, tempMatrix.get(c, r));
							}
							else {
								set(r, c, tempMatrix.get(Direction.antiDir(r), c));
							}
						}
						
					}
						
				}
			}
			
		}
	}
	
	
	///////// HELPER METHODS ////////
	
	// Returns a DataRing structure version of this matrix, made row wise
	public DataRing<E> toDataRing() {
		
		DataRing<E> tempRing = new DataRing<E>();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				tempRing.add(r*cols+c, get(r,c));
			}
		}
		
		return tempRing;
		
	}
	
	// returns a matrix with a incrementing sequence of integers, filling in row by row
	public static Matrix<Integer> newIndexMatrix(int rows, int cols) {
		
		Matrix<Integer> indexMatrix = new Matrix<>(rows, cols);
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				indexMatrix.set(r, c, r*cols + c);
			}
		}
		
		return indexMatrix;
		
	}
	
	// makes an array version copy of the matrix
	public static <T> T[][] toArray(Matrix<T> matrix) {
		
		return matrix.cloneInnerMatrix();
		
	}
		
	// toString for printing
	@Override
	public String toString() {

		String stringMatrix = new String();
		
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				stringMatrix = stringMatrix + " " + get(r,c).toString();
			}
			stringMatrix = stringMatrix + "\n";
		}
		
		return stringMatrix;
		
	}
	
	/*
	 *  cloneInnerMatrix and setInnerMatrix are actually equivalent operations, just different usages
	 *  cloneInnerMatrix return a reference free version of the innerMatrix
	 *  setInnerMatrix sets innerMatrix to another E[][] array and removes references.
	 */
	// clones the innerMatrix, making the new clone reference free from innerMatrix
	private E[][] cloneInnerMatrix() {

		E[][] innerMatrixClone = (E[][]) new Object[rows][cols];
				
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				innerMatrixClone[r][c] = innerMatrix[r][c];
			}
		}
				
		return innerMatrixClone;
				
	}
	
	private void setInnerMatrix(E[][] innerMatrix) {
		this.innerMatrix = innerMatrix;
		rows = innerMatrix.length;
		cols = innerMatrix[0].length;
	}
	
	
}
