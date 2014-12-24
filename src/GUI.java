import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import processing.core.PApplet;
import processing.core.PVector;

public class GUI extends PApplet {

	// TODO : Windom dimensions in pixels --> pixel-free?
	public static final int DOUBLE_CLICK_MAX_TIME = 100;
	private final int[] backgroundColor = {0,180,0};
	private int width;
	private int height;
	private float blockScale; // any positive float
	private float roadScale; // between 0 and 1
	
	private BlockMap<Pos,BuildingBlock> blockMap;
	private Set<Map.Entry<Pos, BuildingBlock>> mapSet;
	private final BuildingBlockFactory blockFactory = new BuildingBlockFactory();
	private BuildingBlock blockFocus;
	private BuildingBlock newBlockFocus;
	
	// TODO : implement the panel
	private JPanel blockPanel;
	
	public void GUI(int width, int height) {
		
		this.width = width;
		this.height = height;
		
	}
	
	public void setup() {
		
		// TODO : This should not be set here, just during testing stage
		width = 1000;
		height = 700;
		blockScale = 30;
		roadScale = 0.5f;
		
		size(width,height);
		background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		blockMap = new BlockMap(blockFactory.getDummyBlock());
		mapSet = blockMap.entrySet();
		
	}
	
	public void draw() {
		
		background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		// draw current focus
		if( newBlockFocus != null) {
			drawBlock(new PVector(mouseX, mouseY), newBlockFocus);
		}
		
		// draw all in blockMap
		for( Map.Entry<Pos, BuildingBlock> blockEntry : mapSet ) {
			
			// convert Pos --> PVector and draw
			drawBlock(new PVector( blockScale*(blockEntry.getKey().x ), blockScale*( blockEntry.getKey().y) ), blockEntry.getValue());
			
		}
		
	}

	private void drawBlock(PVector pos, BuildingBlock block) {
		
		Matrix<Boolean> blockMatrix = block.getConnections();
		float Xoffset = blockScale*(1 - roadScale)/2;
		float Yoffset = blockScale*(1 - roadScale)/2;
		float Xscale = blockScale*roadScale;
		float Yscale = blockScale*roadScale;
		// road gray color
		noStroke();
		fill(120);
		rect(pos.x + Xoffset, pos.y + Yoffset, Xscale, Yscale);
		
		// Draw the north, east, west, south connections
		for(int r = 0; r < blockMatrix.rows(); r++) {
			
			boolean connected = blockMatrix.getRowSum(r, Matrix.boolOr);
			connected = connected || blockMatrix.getColSum(r, Matrix.boolOr);
			
			if( connected ) {
				
				switch(r) {
				case(Direction.NORTH):
					Xoffset = blockScale*(1 - roadScale)/2;
					Yoffset = 0f;
					Xscale = blockScale*roadScale;
					Yscale = blockScale*(1 - roadScale)/2;
					break;
				case(Direction.EAST):
					Xoffset = blockScale*(1 + roadScale)/2;
					Yoffset = blockScale*(1 - roadScale)/2;
					Xscale = blockScale*(1 - roadScale)/2;
					Yscale = blockScale*roadScale;
					break;
				case(Direction.SOUTH):
					Xoffset = blockScale*(1 - roadScale)/2;
					Yoffset = blockScale*(1 + roadScale)/2;
					Xscale = blockScale*roadScale;
					Yscale = blockScale*(1 - roadScale)/2;
					break;
				case(Direction.WEST):
					Xoffset = 0f;
					Yoffset = blockScale*(1 - roadScale)/2;
					Xscale = blockScale*(1 - roadScale)/2;
					Yscale = blockScale*roadScale;
					break;
				}
				
				rect(pos.x + Xoffset, pos.y + Yoffset, Xscale, Yscale);
				
			}
			
		}
		
		
	}
	
	
	private void drawBackground(Pos pos) {
		
		fill(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		rect(blockScale*pos.x, blockScale*pos.y, blockScale, blockScale);
		
	}
	
	private void setBlockObj(Pos pos, BlockObject blockObj) {
		
		blockMap.put(pos, blockObj);
		
	}
	
	private PVector posToVec(Pos pos) {
		
		return new PVector(blockScale*pos.x, blockScale*pos.y);
		
	}
	
	private Pos vecToPos(PVector vec) {
		
		int posX = (int) Math.floor(vec.x/blockScale);
		int posY = (int) Math.floor(vec.y/blockScale);
		return new Pos(posX, posY);
		
	}
	
	private Pos XYtoPos(float X, float Y) {
		
		int posX = (int) Math.floor(X/blockScale);
		int posY = (int) Math.floor(Y/blockScale);
		return new Pos(posX, posY);
		
	}
	
	////////// ACTION EVENTS //////////
	
	public void keyPressed() {
		
		// instantiators
		switch(key) {
		case('q'):
			newBlockFocus = blockFactory.getRoad(Direction.EAST,false);
			break;
		case('w'):
			newBlockFocus = blockFactory.getCurve(Direction.EAST, Direction.LEFT,false);
			break;
		case('e'):
			newBlockFocus = blockFactory.getCrossing(Direction.EAST, Direction.NORTH,false);
			break;
		case('t'):
			newBlockFocus = blockFactory.getTCrossing(Direction.EAST, Direction.LEFT,true,false);
			break;
		}
		
		// modifiers
		if( newBlockFocus != null ) {
			switch(key) {
			case('r'):
				newBlockFocus.rotate();
				break;
			case('f'):
				newBlockFocus.flip();
				break;
			}
		}
		else if( blockFocus != null ) {
			switch(key) {
				case('r'):
					blockFocus.rotate();
					System.out.println(blockFocus);
					break;
				case('f'):
					blockFocus.flip();
					System.out.println(blockFocus);
					break;
				case('d'):
					if( blockFocus != blockMap.getDummyValue() ) {
						blockMap.remove(blockFocus);
						mapSet.remove(blockMap.getEntry(blockFocus));
						blockFocus = null;
					}
					break;
					
			}	
		}
		
	}
	
	public void mousePressed() {
		
		if( newBlockFocus != null) {
	
			Pos mousePos = XYtoPos(mouseX, mouseY);
			blockMap.put(mousePos, newBlockFocus);
			blockFocus = newBlockFocus;
			newBlockFocus = null;
			System.out.println(blockFocus);
		}
		else {
			
			Pos mousePos = XYtoPos(mouseX, mouseY);
			blockFocus = blockMap.getValue(mousePos);
			System.out.println(blockFocus);
			
		}
		
	}
	
}
