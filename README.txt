//////////  BlockObject  //////////

CONSTRUCTOR 
+ BlockObject( Matrix<BuildingBlock> blockMatrix )

PUBLIC METHODS
+ BuildingBlock getBlock(Pos)
+ int getSpeedLimit()
+ void setBlock(Pos, BuildingBlock)
+ void setSpeedLimit()



////////// BuildingBlock implements Comparable<BuildingBlock> ///////

CONSTRUCTOR
+ BuildingBlock()

STATIC METHODS
+ BuildingBlock max(BuildingBlock…)

PUBLIC METHODS
// getters //
+ Matrix<Boolean> getCurrentState()
+ Matrix<Boolean> getState(int)
+ Matrix<Boolean> getConnections()
+ List<Matrix<Boolean>> getStateList()
+ Matrix<Float> getFlow()
+ int maxState()

// setters //
+ void setState(int stateNum)
+ void addState(Matrix<Boolean>)
+ void removeState(int)
+ void setFlow(Matrix<Float>)
+ void rotate(int)
+ void flip(int)
+ void revert()
+ int compareTo(BuildingBlock)

// helpers //
+ Matrix<Boolean> getSymmetricState
+ String toString()


/////////// BuildingBlockFactory //////////

CONSTRUCTOR
+ BuildingBlockFactory()

PUBLIC METHODS
// getters //
+ BuildingBlock getRoad(int,boolean)
+ BuildingBlock getCurve(int,int,boolean)
+ BuildingBlock getCrossing(int,int,boolean)
+ BuildingBlock getTCrossing(int,int,boolean,boolean)
+ BuildingBlock blockFuse(BuildingBlock…)
+ BuildingBlock blockSum(BuildingBlock…)



////////// Matrix<E> //////////

CONSTRUCTOR
+ Matrix(E[][])
+ Matrix(int,int,E[][])
+ Matrix(int,int)

STATIC INTERFACES (functionals)
- BinaryOperation<Boolean> boolOr
- BinaryOperation<Boolean> boolAnd
- BinaryOperation<Integer> intAdd
- BinaryOperation<Integer> intMult
- BinaryOperation<Float> floatAdd
- BinaryOperation<Float> floatMult

STATIC METHODS
+ T[][] toArray(Matrix<T>)
+ String toArray()

PUBLIC METHODS
// getters //
+ int rows()
+ int cols()
+ get(int,int)
+ getRow(int)
+ getCol(int)
+ Matrix<E> clone()
+ Matrix<E> directOp(Matrix<E>,BinaryOperation)
+ Matrix<E> matrixMult(Matrix<E>,BinaryOperation,BinaryOperation)

// setters //
+ void set(int,int,E)
+ void setRow(int,E[])
+ void setCol(int,E[])
+ void rotate()
+ void flip()
+ void transpose()



////////// Direction (static access) //////////
STATIC FIELDS
- int NORTH
- int EAST
- int SOUTH
- int WEST

STATIC METHODS
+ int antiDir(int)
+ int dirBend(int,int)
+ int getDirection(Pos)



////////// Pos //////////

CONSTRUCTOR
+ Pos(int,int)

PUBLIC FIELDS
- int x
- int y

STATIC METHODS
+ int distance(Pos,Pos)

PUBLIC METHODS
+ Pos sub(Pos)
