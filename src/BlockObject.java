
public class BlockObject {

	private Matrix<BuildingBlock> blockMatrix;
	private int rows;
	private int cols;
	private int speedLimit;
	
	public BlockObject(Matrix<BuildingBlock> blockMatrix) {
		
		this.blockMatrix = blockMatrix;
		rows = blockMatrix.rows();
		cols = blockMatrix.cols();
		speedLimit = 0;
		
	}
	
	public BuildingBlock getBlock(Pos pos) {
		
		int row = pos.x;
		int col = pos.y;
		
		if(row < rows && col < cols && row >= 0 && col >= 0) {
			return blockMatrix.get(row,col);
		}
		else {
			System.out.println("Illegal rowCol argument");
			return new BuildingBlock();
		}
	}
	
	public void setBlock(Pos pos, BuildingBlock buildingBlock ) {
		
		int row = pos.x;
		int col = pos.y;
		
		if(row < rows && col < cols && row >= 0 && col >= 0) {
			blockMatrix.set(row, col, buildingBlock);
		}
	}
	
	public int getSpeedLimit() {
		return speedLimit;
	}
	
	public void setSpeedLimit(int speedLimit) {
		this.speedLimit = speedLimit;
	}
	
	
	/* TODO : connect FROM?
	public void connect(Pos pos1, Pos pos2) {
		
		// gets direction
		int dir;
		
		if( pos1.x < rows && pos2.x < rows && pos1.y < cols && pos2.y < cols
				&& pos1.x >= 0 && pos2.x >= 0 && pos1.y >= 0 && pos2.y >= 0 ) {
			if( Pos.distance(pos1,pos2) <= 1) {
				// posVec pointing from pos1 to pos2 (where to create connection IN pos1)
				Pos posVec = pos2.sub(pos1);
				dir = Direction.getDirection(posVec);
			}
		}
		
	}
	*/

}
