

/*
 * Holds the global constants NORTH, EAST, WEST, SOUTH (NEWS)
 */

public class Direction {

	// some universal constants
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	public static final int RIGHT = 1;
	public static final int LEFT = -1;
	
	public static final Pos NORTH_POS = new Pos(0,-1);
	public static final Pos EAST_POS = new Pos(1,0);
	public static final Pos SOUTH_POS = new Pos(0,1);
	public static final Pos WEST_POS = new Pos(-1,0);
	
	// returns opposite direction
	public static int antiDir(int dir) {
		return (dir + 2) % 4;
	}
	
	// bends a direction, -1 for left bend, 1 for right bend
	public static int dirBend(int dir, int bend) {
		int bendedDir = (dir + bend) % 4;
		if(bendedDir < 0) {
			bendedDir = 4 + bendedDir;
		}
		return bendedDir;
	}
	
	public static int posToDir(Pos pos) {
		// if WEST-EAST binding : x --> 1 - x
		// if NORTH-SOUTH binding : x --> x % 4
		//return ( pos.x == 0 ) ? (1 - pos.y) : ( dirBend(pos.x, 0) );
		if( pos.x == 0 || pos.y == 0) {
			return ( pos.x == 0 ) ? (1 + pos.y) : ( dirBend(pos.x, 0) );
		}
		else if( (pos.x == 1 || pos.x == -1) && (pos.y == 1 || pos.y == -1) ) {
			return ( pos.x == 1 ) ? (pos.x + pos.y)/2 : (3 - (pos.y - pos.x)/2);
		}
		return -1;
		
	}
	

	
	// returns the "arrow" Pos corresponding to the specified direction dir
	public static Pos dirToPos(int dir) {
			
		Pos pos;
			
		switch(dir) {
		case 0:
			pos = NORTH_POS.clone();
			break;
		case 1:
			pos = EAST_POS.clone();
			break;
		case 2:
			pos = SOUTH_POS.clone();
			break;
		case 3:
			pos = WEST_POS.clone();
			break;
		default:
			pos = new Pos(0,0);
		}
			
		return pos;
			
	}
	

}
