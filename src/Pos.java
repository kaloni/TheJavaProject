import java.util.Objects;


public class Pos implements Comparable<Pos> {

	public int x;
	public int y;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private boolean onX;
	private boolean alternate;
	private boolean rotateCheck;
	private int rotateJump;
	//private Pos origin;
	//public static boolean NO_ORIGIN = false;
	
	
	
	public Pos(int x, int y) {
		
		this.x = x;
		this.y = y;
		alternate = false;
		rotateCheck = false;
		rotateJump = 0;
		minX = 0;
		minY = 0;
		maxX = 0;
		maxY = 0;
		//origin = new Pos(0,0,Pos.NO_ORIGIN);
		
	}
	/*
	public Pos(int x, int y, boolean origin) {
		
		this.x = x;
		this.y = y;
		
	}
	*/
	
	public int compareTo(Pos pos) {
		
		if( x > pos.x || y > pos.y ) {
			return 1;
		}
		else if( x == pos.x && y == pos.y ) {
			return 0;
		}
		
		return -1;
		
	}
	
	
	// returns a pair of Pos telling what is the minimum Pos and maximum Pos in X and Y
	// for the arbitrary length input
	public static Pos[] minMax(Pos... pos) {
		
		Pos minPos = pos[0].clone();
		Pos maxPos = pos[0].clone();
		
		for( Pos somePos : pos ) {
			
			if( somePos.x < minPos.x ) {
				System.out.println("minX = : " + minPos.x + " --> " + somePos.x);
				minPos.x = somePos.x;
			}
			if( somePos.x > maxPos.x ) {
				System.out.println("maxX = : " + maxPos.x + " --> " + somePos.x);
				maxPos.x = somePos.x;
			}
			if( somePos.y < minPos.y ) {
				System.out.println("minY = : " + minPos.y + " --> " + somePos.y);
				minPos.y = somePos.y;
			}
			if( somePos.y > maxPos.y ) {
				System.out.println("maxY = : " + maxPos.y + " --> " + somePos.x);
				maxPos.y = somePos.y;
			}
			
		}
		
		Pos[] minMaxPos = new Pos[2];
		minMaxPos[0] = minPos;
		minMaxPos[1] = maxPos;
		
		return minMaxPos;
		
	}
	
	public Pos clone() {
		
		Pos posClone = new Pos(x,y);
		posClone.setAlternate(alternate);
		posClone.setRotateCheck(rotateCheck);
		posClone.setRotateJump(rotateJump);
		posClone.setRotationBounds(minX, maxX, minY, maxY);
		
		return posClone;
		
	}
	
	public static int distance(Pos pos1, Pos pos2) {
		
		int x = Math.abs(pos1.x - pos2.x);
		int y = Math.abs(pos1.y - pos2.y);
		
		return x + y;
		
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
	
	public int[] getMinMax() {
		
		int[] minMax = new int[2];
		minMax[0] = Math.min(x,y);
		minMax[1] = Math.max(x,y);
		
		return minMax;
		
	}
	
	// this is used in the rotation of blockGroup
	public void setRotationBounds(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		onX = (y == minX || y == maxY) ? true : false;
	}
	
	// checks if this is on a edge
	public boolean posCheck(int bounds) {
		
		return x == bounds || y == bounds;
		
	}
	
	public void setAlternate(boolean bool) {
		alternate = bool;
	}
	
	public void setRotateCheck(boolean bool) {
		rotateCheck = bool;
	}
	
	public void setRotateJump(int i) {
		rotateJump = i;
	}
	
	/*
	public void setOrigin(Pos origin) {
		this.origin = origin;
	}
	
	public void translateOrigin(Pos pos) {
		origin = origin.add(pos);
	}
	
	public Pos getPosOrigin() {
		return origin;
	}
	
	public void rotateWithOrigin() {
		
		x = x - origin.x;
		y = y - origin.y;
		
		
		
		minX = minX - origin.x;
		maxX = maxX - origin.x;
		minY = minY - origin.y;
		maxY = maxY - origin.y;
		
		
		rotateWithCheck();
		
		x = x + origin.x;
		y = y + origin.y;
		
		minX = minX + origin.x;
		maxX = maxX + origin.x;
		minY = minY + origin.y;
		maxY = maxY + origin.y;
		
		
	}
	*/
	
	public void rotateWithCheck() {
		
		if( alternate ) {
			
			if( rotateCheck ) {
				
				for(int i = 0; i < rotateJump; i++) {
					rotate();
				}
			
			}
			
			rotateCheck = !rotateCheck;
			
		}
		else {
			rotate();
		}
		
	}
	
	public void rotate() {
			
		if( onX == true ) {
				
			x = x - y;
				
			if( x <= minX || x >= maxX ) {
				int deltaX = ( x <= minX ) ? (x - minX) : (x - maxX);
				x = ( x <= minX ) ? minX : maxX;
				y = y + deltaX;
				onX = false;
			}
				
		}
		else {
			// onY
			y = y + x;
				
			if( y <= minY || y >= maxY ) {
				int deltaY = ( y <= minY ) ? (minY - y) : (maxY - y);
				y = ( y <= minY ) ? minY : maxY;
				x = x + deltaY;
				onX = true;
			}
				
		}
		
	}
	
	public void flip(int axis) {
		
		if( axis == Direction.NORTH || axis == Direction.SOUTH ) {
			
			x = -x;
			
		}
		else if( axis == Direction.EAST || axis == Direction.WEST ) {
			
			y = -y;
			
		}
		
	}
	
	public void normalize() {
		
		if( x != 0 ) {
			x = (x < 0) ? -1 : 1;
		}
		if( y != 0) {
			y = (y < 0) ? -1 : 1;
		}
		
	}
	
	public Pos add(Pos pos) {
		
		int newX = x + pos.x;
		int newY = y + pos.y;
		return new Pos(newX,newY);
		
	}
	
	public Pos sub(Pos pos) {
		int newX = x - pos.x;
		int newY = y - pos.y;
		return new Pos(newX,newY);
	}
	
	public static double dist(Pos pos1, Pos pos2) {
		
		Pos posDiff = pos1.sub(pos2);
		
		return Math.sqrt(posDiff.x*posDiff.x + posDiff.y*posDiff.y);
		
	}
	
	public double abs() {
		
		return Math.sqrt(x*x + y*y);
		
	}
	
	
	@Override
	public String toString() {
		
		String string = Integer.toString(x) + " " + Integer.toString(y);
		
		return string;
		
	}

}
