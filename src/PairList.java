import java.util.ArrayList;
import java.util.Iterator;


public class PairList<T> extends ArrayList<Pair<T>> {

	public PairList() {
		
	}
	
	// add only if current pair does not exist. Don't add pair of one object to itself
	// avoids duplicates
	public boolean add(Pair<T> pair) {
		
		if( pair.first == pair.second ) {
			return false;
		}
		
		for(Pair<T> existingPair : this ) {
			
			if( existingPair.first == pair.first && existingPair.second == pair.second 
					|| existingPair.first == pair.second && existingPair.second == pair.first ) {
				return false;
			}
			
		}
		
		super.add(pair);
		
		return true;
		
	}
	
	// re-links the list : removes all "unnecessary" pairs in the sense that if one pair (A,B) exists and
	// another pair (B,C) exists then (A,C) should be a pair as well.
	// However, the link (A,C) is "unnecessary" and if we want to make "chain reaction coding" we need to
	// get rid of these "unnecessary" pairs
	// The list becomes in some sense "minimally transitive".
	// TODO : does not completely re-links, only parially simplifies the structure
	public void relink() {
		
		ArrayList<Pair<T>> removeList = new ArrayList<>();
		
		for(Pair<T> pairX : this) {
			for(Pair<T> pairY : this) {
				
				if( pairX.second == pairY.first ) {
					
					for(Pair<T> pairZ : this) {
						
						if( pairZ.first == pairX.first && pairZ.second == pairY.second ) {
							
							removeList.add(pairZ);
							
						}
						
					}
					
					break;
					
				}
				
			}
		}
		
		for(Pair<T> removePair : removeList) {
			
			remove(removePair);
			
		}
		
	}
	

}
