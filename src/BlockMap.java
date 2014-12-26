import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BlockMap<K,V> extends HashMap {
	
	private K dummyKey;
	private V dummyValue;
	
	public BlockMap(K dummyKey, V dummyValue) {
		
		this.dummyKey = dummyKey;
		this.dummyValue = dummyValue;
		
	}

	// TODO : BlockMap should be parametrized with BlockObject instead of BuildingBlock anyways...
	public void put(Pos pos, BlockObject blockObj) {
		
	}
	
	public V getValue(K key) {
		
		Set<Map.Entry<K, V>> mapSet = entrySet();
		
		if( containsKey(key)  ) {
		
			for(Map.Entry<K, V> entry : mapSet) {
				
				if( entry.getKey().equals(key) ) {

					return entry.getValue();
			
				}
		
			}
			
		}
		
		return dummyValue;
		
	}
	
	public K getKey(V value) {
		
		Set<Map.Entry<K, V>> mapSet = entrySet();
		
		for(Map.Entry<K, V> entry : mapSet) {
			
			if( entry.getValue() == value ) {

				return entry.getKey();
				
			}
		
		}
		
		return dummyKey;
			
	}
	
	public Map.Entry<K, V> getEntry(V value) {
		
		Set<Map.Entry<K, V>> mapSet = entrySet();
		
		for( Map.Entry<K,V> entry : mapSet ) {
			
			if( entry.getValue() == value ) {
				
				return entry;
				
			}
			
		}
		
		return (Map.Entry<K, V>) new Object();
		
	}
	
	public V getDummyValue() {
		return dummyValue;
	}
	
	
}
