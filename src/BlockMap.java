import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BlockMap<K,V> extends HashMap<K, V> {
	
	// Use dummies as default returns instead of casting new objects
	private K dummyKey;
	private V dummyValue;
	private Map.Entry<K, V> dummyEntry;
	private List<Pos> carAreaPosList;
	
	/////////// CONSTRUCTORS (2) ///////////
	
	
	public BlockMap() {
		super();
		carAreaPosList = new ArrayList<>();
	}
	
	
	public BlockMap(K dummyKey, V dummyValue) {
		
		carAreaPosList = new ArrayList<>();
		this.dummyKey = dummyKey;
		this.dummyValue = dummyValue;
		put(dummyKey, dummyValue);
		dummyEntry = getEntry(dummyValue);

	}
	
	/////////// ///////// //////////
	
	// override put to ensure a one cannot put blocks on other blocks
	@Override
	public V put(K key, V value) {
		
		if( !containsKey(key) && !carAreaPosList.contains(key) ) {
			super.put(key, value);
			return value;
		}
		// else
		return null;
		
		
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
	
	public void removeDummy() {
		this.remove(dummyKey);
	}
	
	public boolean addCarAreaPos(Pos pos) {
		
		if( !carAreaPosList.contains(pos) ) {
			
			carAreaPosList.add(pos);
			return true;
		}
		return false;
	}
	
}
