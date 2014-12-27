////////// GUI extends PApplet //////////

METHODS
+ void drawBlock(PVector,BuildingBlock)
+ void drawBackground(Pos)
+ void displayBlock(DataRing<Boolean>, boolean)
+ void displayBendedRoad(DataRing<Boolean>, bool, int, int)
+ void setBlockObj(Pos,BlockObject)
+ PVector posToVec(Pos)
+ Pos vecToPos(PVector)
+ Pos XYtoPos(float,float)
+ int scaleConverter(float)
+ void keyPressed()
+ void mousePressed



////////// BlockMap<K,V> extends HashMap//////////

CONSTRUCTOR
+ BlockMap(K,V)

PUBLIC METHODS
+ K getKey(V)
+ V getValue(K)
+ Map.Entry<K,V> getEntry(V)
+ V getDummyValue



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
+ boolean isDiagonal()
+ int getDir()
+ Matrix<Boolean> getCurrentState()
+ Matrix<Boolean> getState(int)
+ List<Matrix<Boolean>> getStateList()
+ Matrix<Boolean> getConnectionMatrix()
+ Ring<Boolean> getConnectionRing
+ Matrix<Float> getFlowMatrix()
+ int maxState()
+ int compareTo(BuildingBlock)

// setters //
+ void setDiagonal(boolean)
+ void setDir(int)
+ void setState(int stateNum)
+ void addState(Matrix<Boolean>)
+ void removeState(int)
+ void setFlowMatrix(Matrix<Float>)
+ void rotate(int)
+ void flip(int)
+ void revert()

// helpers //
+ String toString()


////////// BuildingBlock subclasses with constructors //////////////

Road(int,boolean,GUI)
BendedRoad(int,int,boolean,GUI)
Curve(int,int,boolean,GUI)
Crossing(int,int,boolean,GUI)
TCrossing(int,int,boolean,boolean,GUI)



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
+ E get(int,int)
+ E[] getRow(int)
+ E[] getCol(int)
+ E getRowSum(int,BinaryOperation)
+ E getColSum(int,BinaryOperation)
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
+ void shift()
+ void exchange()

// helpers //
+ toString()


////////// DataRing<E> /////////////

CONSTRUCTORS (3)
+ DataRing()
+ DataRing(int)
+ DataRing(E[])

STATIC FIELDS
- UnitaryOperation<Integer,Boolean> intEven
- UnitaryOperation<Integer,Boolean> intOdd

PUBLIC METHODS
+ get(int)
+ set(int)
+ add(int)
+ remove(int)
+ void cycle()
+ void modCycle(int)
+ void constraintCycle(UnitaryOperation<Integer,Boolean>)
+ toString()


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
+ Pos dirToPos
+ 


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

// helpers //
+ int hashCode()
+ boolean equals()
