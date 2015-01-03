import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PathFinder {

	private BlockMap<Pos, BuildingBlock> blockMap;
	private Matrix<Float> blockMatrix;
	private HashMap<Integer, Pos> indexMap;
	
	public PathFinder(BlockMap blockMap) {
		
		this.blockMap = blockMap;
		indexMap = new HashMap<>();
		
	}
	
	public void constructMatrix() {
		
		Set<Map.Entry<Pos, BuildingBlock>> entrySet = blockMap.entrySet();
		int counter = 0;
		
		// construct index map
		for(Map.Entry<Pos, BuildingBlock> blockEntry : entrySet) {
			
			indexMap.put(counter, blockEntry.getKey());
			counter++;
		}
		
		for(int i = 0; i < counter; i++) {
			for(int j = 0; j < counter; j++) {
				
				// if close enough
				if( Pos.dist( indexMap.get(i), indexMap.get(j) ) <= Math.sqrt(2) ) {
					
					if( connected(indexMap.get(i), blockMap.get(indexMap.get(i)), indexMap.get(j), blockMap.get(indexMap.get(j))) ) {
						
					}
					
				}
				
			}
		}
		
	}
	
	public boolean connected(Pos pos1, BuildingBlock block1, Pos pos2, BuildingBlock block2) {
		
		DataRing<Boolean> input1 = block1.getInputRing();
		DataRing<Boolean> input2 = block2.getInputRing();
		DataRing<Boolean> output1 = block1.getOutputRing();
		DataRing<Boolean> output2 = block2.getOutputRing();
		
		Pos posDiff = pos1.sub(pos2);
		double dist = posDiff.abs();
		
		if( dist == 1 ) {
	
			int dirDiff = Direction.posToDir(posDiff);
			
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
		else if( dist == Math.sqrt(2) ) {
			
		}
		
		return false;
		
		
		
	}

}
