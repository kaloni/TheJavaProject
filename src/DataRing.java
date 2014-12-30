
public class DataRing<E> {

	private E[] ring;
	private int size;
	
	public DataRing() {
		
		ring = null;
		size = 0;
		
	}
	
	public DataRing(int size) {
		
		this.size = size;
		ring = (E[]) new Object[size];
		
	}
	
	public DataRing(E[] array) {
		
		ring = array;
		size = ring.length;
		
	}
	
	//////////// functional interfaces //////////
	
	private <K, V> V uniOp(K key, UnitaryOperation<K,V> op) {
		return op.operation(key);
	}
	
	private interface UnitaryOperation<K,V> {
		V operation(K key);
	}
	
	// Uniary check operations for iterations over indices with constraints
	public static UnitaryOperation<Integer,Boolean> intEven = key -> ( key % 2 == 0) ? true : false;
	public static UnitaryOperation<Integer,Boolean> intOdd = key -> ( key % 2 == 0) ? false : true;
	
	////////// ////////// ////////// //////////
	
	public int size() {
		return size;
	}
	
	// cycle the ring
	public void cycle(int shift) {
		
		E[] tempRing = (E[]) new Object[size];
		
		if( shift != 0 ) {
			
			if( shift > 0) {
				
				for(int i = 0; i < size; i++) {
						
					tempRing[i] = ring[ (i+shift) % size ];
						
				}
				
			}
			
			else if( shift < 0 ) {
				
				for(int i = 0; i < size; i++) {
					
					tempRing[ (i - shift) % size ] = ring[i];
					
				}
				
			}
			
			ring = tempRing;
			
		}
		
	}
	
	// cycle only indices i % mod == 0
	public void modCycle(int mod) {
		
		if( mod > 0 ) {
			
			E[] tempRing = (E[]) new Object[size];
			
			for(int i = 0; i < size; i++) {
				
				if( i % mod == 0) {
					
					tempRing[i] = ring[ (i+mod) % size];
					
				}
				else {
					
					tempRing[i] = ring[i];
					
				}
				
			}
			
			ring = tempRing;
			
		}
		
	}
	
	// cycle with any constraint on indices
	public void constraintCycle(UnitaryOperation<Integer,Boolean> op) {
		
		E[] tempRing = (E[]) new Object[size];
		
		int counter = 0;
		
		for(int i = 0; i < size; i++) {
			
			if( uniOp(i, op) ) {
				
				tempRing[counter] = ring[i];
				counter++;
				
			}
			
		}
		
		int tempSize = counter;
		counter = 0;
		
		for(int i = 0; i < size; i++) {
			
			if( uniOp(i, op) ) {
				
				ring[i] = tempRing[ (counter + 1) % tempSize];
				counter++;
				
			}
			
		}
		
	}
	
	public E get(int i) {
		
		return ring[ i % size ];
		
	}
	
	public void set(int i, E ele) {
		
		if( ring != null ) {
			
			if( ele == null ) {
				
				ring[ i % size] = null;
				
				for(int j = (i % size) ; j < (size - 1) ; j++) {
					
					ring[j] = ring[ (j + 1) % size];
					
				}
				
				size--;
				
			}
			else {
			
			ring[ i % size ] = ele;
			
			}
			
		}
		
	}
	
	// adds element to index (gets pushed in between current elements)
	public void add(int i, E ele) {
		
		int modi = i % (size+1);
		E[] newRing = (E[]) new Object[size+1];
		
		
		for(int j = 0; j < modi; j++) {
			newRing[j] = ring[j];
		}
		for(int j = modi; j < size; j++) {
			newRing[j+1] = ring[j];
		}
		
		newRing[modi] = ele;
		ring = newRing;
		size++;
		
	}
	
	public void remove(int i) {
		
		set(i, null);
		
	}
	
	public DataRing<E> clone() {
		
		DataRing<E> tempRing = new DataRing<>();
		
		for(int i = 0; i < size; i++) {
			
			tempRing.add(i, get(i));
			
		}
		
		return tempRing;
		
	}
	
	@Override
	public String toString() {
		
		String string = new String();
		
		for(int i = 0; i < size; i++) {
			
			string = string + " " + ring[i];
			
		}
		
		return string;
		
	}
	

}
