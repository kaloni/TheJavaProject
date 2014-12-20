

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
	
	public static int getDirection(Pos pos) {
		// if WEST-EAST binding : x --> 1 - x
		// if NORTH-SOUTH binding : x --> x % 4
		return ( pos.x == 0 ) ? (1 - pos.y): ( dirBend(pos.x, 0) );
		
	}
	

}
