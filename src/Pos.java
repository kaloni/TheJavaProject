import java.util.Objects;


public class Pos {

	public int x;
	public int y;
	
	public Pos(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
	
	@Override
	public boolean equals(Object o) {
		if( !(o instanceof Pos) ) {
			return false;
		}
		return Objects.equals( ((Pos) o).x, x) && Objects.equals( ((Pos) o).y, y);
	}
	
	public static int distance(Pos pos1, Pos pos2) {
		
		int x = Math.abs(pos1.x - pos2.x);
		int y = Math.abs(pos1.y - pos2.y);
		
		return x + y;
		
	}
	
	public Pos sub(Pos pos) {
		int newX = x - pos.x;
		int newY = y - pos.y;
		return new Pos(newX,newY);
	}

}
