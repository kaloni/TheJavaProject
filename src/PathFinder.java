import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PathFinder {

	private BlockMap<Pos, BuildingBlock> blockMap;
	private Matrix<Double> blockMatrix;
	private HashMap<Integer, Pos> indexMap;
	
	public PathFinder(BlockMap<Pos, BlockGroup> blockMap) {
		
		blockMap.removeDummy();
		this.blockMap = mapTypeConverter(blockMap);
		indexMap = new HashMap<>();
		
	}
	
	public Matrix<Double> getMatrix() {
		return blockMatrix;
	}
	
	public HashMap<Integer, Pos> getIndexMap() {
		return indexMap;
	}
	
	public BlockMap<Pos, BuildingBlock> mapTypeConverter(BlockMap<Pos, BlockGroup> blockGroupMap) {
		
		BlockMap<Pos, BuildingBlock> blockMap = new BlockMap<>();
		
		for(Map.Entry<Pos, BlockGroup> blockGroupEntry : blockGroupMap.entrySet()) {
			
			Pos blockGroupPos = blockGroupEntry.getKey();
			BlockGroup blockGroup = blockGroupEntry.getValue();
			
			for(Map.Entry<Pos, BuildingBlock> blockEntry : blockGroup.entrySet()) {
				
				Pos blockInnerPos = blockEntry.getKey();
				blockMap.put(blockGroupPos.add(blockInnerPos), blockEntry.getValue());
				
			}
			
		}
		
		return blockMap;
		
	}
	
	public void constructMatrix() {
		
		Set<Map.Entry<Pos, BuildingBlock>> entrySet = blockMap.entrySet();
		int blocks = 0;
		
		// construct index map
		for(Map.Entry<Pos, BuildingBlock> blockEntry : entrySet) {
			
			indexMap.put(blocks, blockEntry.getKey());
			blocks++;
		}
		
		blockMatrix = new Matrix<>(blocks, blocks);
		
		for(int i = 0; i < blocks; i++) {
			for(int j = 0; j < blocks; j++) {
				
				blockMatrix.set(i, j, Double.POSITIVE_INFINITY);
				
				double dist = Pos.dist( indexMap.get(i), indexMap.get(j));
				if( dist <= Math.sqrt(2) ) {
					
					//if( connected(indexMap.get(i), blockMap.get(indexMap.get(i)), indexMap.get(j), blockMap.get(indexMap.get(j))) ) {
					if( i == j) {
						blockMatrix.set(i, j, 0.0);
					}
					else if( connected( blockMap.get(indexMap.get(i)), blockMap.get(indexMap.get(j)) ) ) {
						
						blockMatrix.set(i, j, dist);
						
					}
					
				}
				
			}
		}
		
	}
	
	public boolean connected(BuildingBlock block1, BuildingBlock block2) {
		
		if( block1.connectedTo(block2) )  {
			return true;
		}
		return false;
		
	}
	
	/*
	public boolean connected(Pos pos1, BuildingBlock block1, Pos pos2, BuildingBlock block2) {
		
		DataRing<Boolean> input1 = block1.getInputRing();
		DataRing<Boolean> input2 = block2.getInputRing();
		DataRing<Boolean> output1 = block1.getOutputRing();
		DataRing<Boolean> output2 = block2.getOutputRing();
		
		Pos posDiff = pos1.sub(pos2);
		double dist = posDiff.abs();
		int dirDiff = Direction.posToDir(posDiff);
		
		// if itself
		if( Pos.dist(pos1, pos2) == 0) {
			return true;
		}
		// if no block is bended
		else if( !(block1 instanceof BendedRoad) && !(block2 instanceof BendedRoad) ) {
			
			if( (dist == Math.sqrt(2) && block1.isDiagonal() && block2.isDiagonal()) || (dist == 1 && !(block1.isDiagonal()) && !(block2.isDiagonal())) ) {
			
				switch(dirDiff) {
				case(Direction.NORTH):
					return (input1.get(Direction.SOUTH) && output2.get(Direction.NORTH)) || (output1.get(Direction.SOUTH) && input2.get(Direction.NORTH));
				case(Direction.EAST):
					return (input1.get(Direction.WEST) && output2.get(Direction.EAST)) || (output1.get(Direction.WEST) && input2.get(Direction.EAST));
				case(Direction.SOUTH):
					return (input1.get(Direction.NORTH) && output2.get(Direction.SOUTH)) || (output1.get(Direction.NORTH) && input2.get(Direction.SOUTH));
				case(Direction.WEST):
					return (input1.get(Direction.EAST) && output2.get(Direction.WEST)) || (output1.get(Direction.EAST) && input2.get(Direction.WEST));
				
				}
			
			}
		
		}
		// else if one is bended
		else if( (block1 instanceof BendedRoad) && !(block2 instanceof BendedRoad) || !(block1 instanceof BendedRoad) && (block2 instanceof BendedRoad) ) {
			
			BuildingBlock bendedBlock = (block1 instanceof BendedRoad) ? block1 : block2;
			BuildingBlock ortoBlock = (block1 instanceof BendedRoad) ? block2 : block1;
			
			if( (dist == Math.sqrt(2) && !(bendedBlock.isDiagonal()) && ortoBlock.isDiagonal()) || (dist == 1 && bendedBlock.isDiagonal() && !(ortoBlock.isDiagonal())) ) {
				
				
				
			}
			
		}
		// else if both are bended
		else if( block1 instanceof BendedRoad && block2 instanceof BendedRoad ) {
			
			// just either may be diagonal
			if( block1.isDiagonal() && !block2.isDiagonal() || !block1.isDiagonal() && block2.isDiagonal() ) {
				
				BuildingBlock diagBlock = block1.isDiagonal() ? block1 : block2;
				BuildingBlock ortoBlock = block1.isDiagonal() ? block2 : block1;
				input1 = diagBlock.getInputRing();
				input2 = ortoBlock.getInputRing();
				output1 = diagBlock.getOutputRing();
				output2 = ortoBlock.getOutputRing();
				
				switch(dirDiff) {
				case(Direction.NORTH):
					return (input1.get(Direction.SOUTH) && output2.get(Direction.NORTH)) || (output1.get(Direction.SOUTH) && input2.get(Direction.NORTH));
				case(Direction.EAST):
					return (input1.get(Direction.WEST) && output2.get(Direction.EAST)) || (output1.get(Direction.WEST) && input2.get(Direction.EAST));
				case(Direction.SOUTH):
					return (input1.get(Direction.NORTH) && output2.get(Direction.SOUTH)) || (output1.get(Direction.NORTH) && input2.get(Direction.SOUTH));
				case(Direction.WEST):
					return (input1.get(Direction.EAST) && output2.get(Direction.WEST)) || (output1.get(Direction.EAST) && input2.get(Direction.WEST));
				
				}
				
			}
			
		}
		
		return false;
		
	}
	*/
	

}
