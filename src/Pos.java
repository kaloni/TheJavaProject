
public class Pos {

	public int x;
	public int y;
	
	public Pos(int x, int y) {
		
		this.x = x;
		this.y = y;
		
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
