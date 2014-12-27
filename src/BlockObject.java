
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

}
