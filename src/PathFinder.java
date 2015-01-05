import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections.BidiMap;

import com.google.common.collect.HashBiMap;

public class PathFinder {

	private BlockMap<Pos, BuildingBlock> blockMap;
	private Matrix<Double> blockMatrix;
	//private HashMap<Integer, Pos> indexMap;
	private HashBiMap<Integer, Pos> indexMap;
	
	public PathFinder(BlockMap<Pos, BlockGroup> blockMap) {
		
		blockMap.removeDummy();
		this.blockMap = mapTypeConverter(blockMap);
		indexMap = HashBiMap.create();
		//indexMap = new HashMap<>();
		constructMatrix();
		
	}
	
	public Matrix<Double> getMatrix() {
		return blockMatrix;
	}
	
	/*
	public HashMap<Integer, Pos> getIndexMap() {
		return indexMap;
	}
	*/
	
	public HashBiMap<Integer, Pos> getIndexMap() {
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
	
	// need to set return type of Matrx.getRow as Object[]
	// and then convert to Double[] using Arrays
	// this might be solved by using java reflection
	// Returs a "recursive path" which can be used to get the shortest path to any other block from the "fromPos"
	// A sequence of Pos is retrieved by calling toPath on the "recursive path" returned from this methods
	// with a corresponding destination
	
	//public Integer[] shortestPath(Matrix<Double> connections, Pos fromPos) {
	// blockMatrix --> connections
	public Integer[] shortestPath(Pos fromPos) {
		
		Integer fromNodeNum = indexMap.inverse().get(fromPos);
		Double[] dist = new Double[blockMatrix.cols()];
		Integer[] previous = new Integer[blockMatrix.cols()];
		List<Integer> unvisited = new ArrayList<>();
		
		for(int i = 0; i < blockMatrix.cols(); i++ ) {
			dist[i] = Double.POSITIVE_INFINITY;
			previous[i] = -1;
			unvisited.add(i);
		}
		
		dist[fromNodeNum] = 0d;
		
		while( ! unvisited.isEmpty() ) {
			
			Integer currentNode = minIndex(dist, unvisited);
			// if no more connected nodes, break
			if(currentNode == -1) {
				break;
			}
			
			Object[] nextDistObj = blockMatrix.getRow(currentNode);
			Double[] nextDist = Arrays.copyOf(nextDistObj, nextDistObj.length, Double[].class);
			
			for(int i = 0; i < blockMatrix.cols(); i++) {
				// if neighbor
				if( nextDist[i] < Double.POSITIVE_INFINITY ) {
					
					Double altDist = dist[currentNode] + nextDist[i];
					if( altDist < dist[i] ) {
						
						dist[i] = altDist;
						previous[i] = currentNode;
						
					}
					
				}
				
			}
			
			unvisited.remove(currentNode);
			
		}
		
		return previous;
		
	}
	
	public int minIndex(Double[] distance, List<Integer> restriction) {
		
		int minIndex = -1;
		Double min = Double.POSITIVE_INFINITY;

		for(int i = 0; i < distance.length; i++) {
			
			if( distance[i] < min && restriction.contains(i) ) {
				min = distance[i];
				minIndex = i;
			}
			
		}
		
		return minIndex;
		
	}
	
	public List<Pos> toPath(Integer[] nodePath, Pos dest) {
	
		List<Pos> path = new ArrayList<>();
		Map<Pos, Integer> nodeMap = indexMap.inverse();
		Integer nodeDest = nodeMap.get(dest);
		Stack<Pos> stack = new Stack();
		
		while( nodeDest != -1 ) {
			
			stack.push(indexMap.get(nodeDest));
			nodeDest = nodePath[nodeDest];
			
		}
		
		while( ! stack.isEmpty() ) {
			
			Pos nextPos = stack.pop();
			path.add(nextPos);
			
		}
		
		return path;
		
	}
	
	

}
