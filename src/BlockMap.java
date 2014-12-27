import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BlockMap<K,V> extends HashMap<K, V> {
	
	// Use dummies as default returns instead of casting new objects
	private K dummyKey;
	private V dummyValue;
	private Map.Entry<K, V> dummyEntry;
	
	public BlockMap(K dummyKey, V dummyValue) {
		
		this.dummyKey = dummyKey;
		this.dummyValue = dummyValue;
		put(dummyKey, dummyValue);
		dummyEntry = getEntry(dummyValue);
		
	}

	// TODO : BlockMap should be parametrized with BlockObject instead of BuildingBlock anyways...
	public void put(Pos pos, BlockObject blockObj) {
		
	}
	
	// override put to ensure a one-to-one relationship between keys and values
	@Override
	public V put(K key, V value) {
		
		if( !containsKey(key) ) {
			super.put(key, value);
			return value;
		}
		// else
		return getValue(key);
		
		
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
		
		return dummyEntry;
		
	}
	
	public V getDummyValue() {
		return dummyValue;
	}
	
	
}
