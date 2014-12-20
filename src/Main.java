import java.util.Random;


public class Main {
	
	Matrix<Integer> matrix;

	public static void main(String[] args) {
		
		
		int rows = 2;
		int cols = 3;
		
		Boolean[][] tempBool = new Boolean[rows][cols];
		Boolean[][] tempBool2 = new Boolean[cols][rows];
		Integer[][] tempIntArray = new Integer[rows][cols];
		Integer[][] tempInt2Array = new Integer[cols][rows];
		Integer[][] symIntArray = new Integer[rows][rows];
		Integer[][] symIntArray2 = new Integer[rows][rows];
		Boolean[][] symBoolArray = new Boolean[rows][rows];
		Boolean[][] symBoolArray2 = new Boolean[rows][rows];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				int tempInt = new Random().nextInt(10);
				int tempInt2 = new Random().nextInt(10);
				int tempBoolint = new Random().nextInt(2);
				int tempBoolint2 = new Random().nextInt(2);
				tempIntArray[i][j] = tempInt;
				tempInt2Array[j][i] = tempInt2;
				tempBool[i][j] = ( tempBoolint == 0 );
				tempBool2[j][i] = ( tempBoolint2 == 0 );
			}
			for(int k = 0; k < rows; k++) {
				int tempInt = new Random().nextInt(10);
				int tempInt2 = new Random().nextInt(10);
				int tempBoolint = new Random().nextInt(2);
				int tempBoolint2 = new Random().nextInt(2);
				symIntArray[i][k] = tempInt;
				symIntArray2[i][k] = tempInt2;
				symBoolArray[i][k] = ( tempBoolint == 0 );
				symBoolArray2[i][k] = ( tempBoolint2 == 0 );
			}
		}
		
		/*
		Matrix<Boolean> matrix1 =  new Matrix<>(tempBool);
		Matrix<Boolean> matrix2 =  new Matrix<>(tempBool2);
		Matrix<Boolean> multMatrix = matrix1.matrixMult(matrix2, Matrix.boolOr, Matrix.boolAnd);
		Matrix<Integer> intMatrix1 = new Matrix<>(tempIntArray);
		Matrix<Integer> intMatrix2 = new Matrix<>(tempInt2Array);
		Matrix<Integer> intMultMatrix = intMatrix1.matrixMult(intMatrix2, Matrix.intAdd, Matrix.intMult);
		//Matrix<Boolean> opMatrix = matrix1.directOp(matrix2, Matrix.boolOr);
		*/
		Matrix<Integer> symIntMatrix1 = new Matrix<>(symIntArray);
		Matrix<Integer> symIntMatrix2 = new Matrix<>(symIntArray2);
		Matrix<Integer> intOpMatrix = symIntMatrix1.directOp(symIntMatrix2, Matrix.intAdd);
		Matrix<Boolean> symBoolMatrix1 = new Matrix<>(symBoolArray);
		Matrix<Boolean> symBoolMatrix2 = new Matrix<>(symBoolArray2);
		Matrix<Boolean> boolOpMatrix = symBoolMatrix1.directOp(symBoolMatrix2, Matrix.boolOr);
		
		
		// Matrix test
		
		/*
		System.out.println(intMatrix1);
		System.out.println(intMatrix2);
		System.out.println(intMultMatrix);
		//System.out.println(intOpMatrix);
		intMatrix1.rotate();
		System.out.println(intMatrix1);
		intMatrix1.flip(Direction.NORTH);
		System.out.println(intMatrix1);
		*/
		/*
		System.out.println(symBoolMatrix1);
		System.out.println(symBoolMatrix2);
		System.out.println(boolOpMatrix);
		*/
		
		// Direction Test
		/*
		System.out.println(Direction.dirBend(Direction.NORTH, Direction.LEFT) + " " + Direction.NORTH + " " + Direction.dirBend(Direction.NORTH, Direction.RIGHT) );
		System.out.println(Direction.dirBend(Direction.EAST, Direction.LEFT) + " " + Direction.EAST + " " + Direction.dirBend(Direction.EAST, Direction.RIGHT));
		System.out.println(Direction.dirBend(Direction.SOUTH, Direction.LEFT) + " " + Direction.SOUTH + " " + Direction.dirBend(Direction.SOUTH, Direction.RIGHT));
		System.out.println(Direction.dirBend(Direction.WEST,Direction.LEFT) + " " + Direction.WEST + " " + Direction.dirBend(Direction.WEST,Direction.RIGHT));
		
		System.out.println(" " + Direction.antiDir(Direction.NORTH));
		System.out.println(" " + Direction.antiDir(Direction.EAST));
		System.out.println(" " + Direction.antiDir(Direction.SOUTH));
		System.out.println(" " + Direction.antiDir(Direction.WEST));
		*/
		
		// BuildingBlock and BuildingBlockFactory test
		
		BuildingBlockFactory factory = new BuildingBlockFactory();
		BuildingBlock road = factory.getRoad(Direction.EAST, false);
		BuildingBlock curve = factory.getCurve(Direction.EAST, Direction.LEFT, false);
		BuildingBlock openCrossing = factory.getCrossing(Direction.SOUTH, Direction.EAST, false);
		BuildingBlock redLightCrossing = factory.getCrossing(Direction.EAST, Direction.NORTH, true);
		BuildingBlock T_crossing = factory.getTCrossing(Direction.EAST, Direction.LEFT, true, false);
		
		/*
		System.out.println(road);
		System.out.println(curve);
		System.out.println(openCrossing);
		System.out.println(redLightCrossing);
		System.out.println(T_crossing);
		*/
		/*
		System.out.println(road.getCurrentState());
		System.out.println(curve.getCurrentState());
		System.out.println(openCrossing.getCurrentState());
		System.out.println(redLightCrossing.getCurrentState());
		redLightCrossing.setState(1);
		System.out.println(redLightCrossing.getCurrentState());
		System.out.println(T_crossing.getCurrentState());
		*/
		
		///////// BlockObject test ////////
		Matrix<BuildingBlock> blockMatrix = new Matrix<>(2,2);
		blockMatrix.set(0, 0, road);
		blockMatrix.set(0, 1, curve);
		blockMatrix.set(1, 0, openCrossing);
		blockMatrix.set(1, 1, T_crossing);
		BlockObject blockObj = new BlockObject(blockMatrix);
		
		
		
	}

}
