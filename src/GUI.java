import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import processing.core.PApplet;
import processing.core.PVector;

public class GUI extends PApplet {

	// TODO : Window dimensions in pixels --> pixel-free?
	private final int[] backgroundColor = {0,180,0};
	
	private int width;
	private int height;
	private final float blockScale = 30f; // any positive float
	private final float roadScale = 15f; // between 0 and blockScale
	
	////////// FINAL CONSTANTS /////////
	// TODO : Add curvature to the displaying of BendedRoad
	Matrix<Float> bendedRoadCurvature;
	
	private final float bendedRoadFactor = 3f;
	
	private final float [] XbendedRoadOffset = {0f, - blockScale/bendedRoadFactor, 0f, 0f};
	private final float [] YbendedRoadOffset = {0f, 0f, - blockScale/bendedRoadFactor, 0f};
	private final float [] bendedRoadScaleX = {0f, blockScale/bendedRoadFactor, 0f, blockScale/bendedRoadFactor};
	private final float [] bendedRoadScaleY = {blockScale/bendedRoadFactor, 0f, blockScale/bendedRoadFactor, 0f};
	
	private final float[] Xoffsets = {(blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2, 0f};
	private final float[] Yoffsets = {0f, (blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2};
	private final float[] roadScaleX = {roadScale, (blockScale - roadScale)/2, roadScale, (blockScale - roadScale)/2};
	private final float[] roadScaleY = {(blockScale - roadScale)/2, roadScale, (blockScale - roadScale)/2, roadScale};
	
	private final float[] XoffsetsRotated = {(blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2, - blockScale*(sqrt(2) - 1)/2};
	private final float[] YoffsetsRotated  = {- blockScale*(sqrt(2) - 1)/2, (blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2};
	private final float [] roadScaleXRotated = {roadScale, blockScale/sqrt(2) - roadScale/2, roadScale, blockScale/sqrt(2) - roadScale/2};
	private final float [] roadScaleYRotated = {blockScale/sqrt(2) - roadScale/2, roadScale, blockScale/sqrt(2) - roadScale/2, roadScale};
	
	private Pos mousePos;
	private BlockMap<Pos,BuildingBlock> blockMap;
	private Set<Map.Entry<Pos, BuildingBlock>> mapSet;
	private BuildingBlock blockFocus;
	private BuildingBlock newBlockFocus;
	
	///////// ////////// ///////////
	
	// TODO : implement the panel
	private JPanel blockPanel;
	
	public void setup() {
		
		// TODO : This should not be set here, just during testing stage
		width = 1000;
		height = 700;
		size(width,height);
		background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		
		BuildingBlock dummyBlock = new BuildingBlock();
		dummyBlock.addState(new Matrix<>(4,4,false));
		blockMap = new BlockMap<>(new Pos(-1,-1), dummyBlock);
		mapSet = blockMap.entrySet();
		mousePos = new Pos(0,0);
		
		noStroke();
		
	}
	
	public void draw() {
		
		mousePos.x = scaleConverter(mouseX);
		mousePos.y = scaleConverter(mouseY);
		
		
		background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		
		// draw all in blockMap
		for( Map.Entry<Pos, BuildingBlock> blockEntry : mapSet ) {
			
			drawBlock(blockScale*blockEntry.getKey().x, blockScale*blockEntry.getKey().y, blockEntry.getValue());
			
		}
		
		// mark focused block
		if( blockFocus != null ) {
			
			fill(100,0,255,70);
			rect(blockScale*blockMap.getKey(blockFocus).x, blockScale*blockMap.getKey(blockFocus).y, blockScale, blockScale);
			
		}
		
		// draw new focus, and mark which grid position it can be put at
		if( newBlockFocus != null) {
					
			drawBlock(mouseX - blockScale/2, mouseY - blockScale/2, newBlockFocus);
			fill(100,0,255,70);
			rect(blockScale*(mousePos.x - 1/2), blockScale*(mousePos.y - 1/2), blockScale, blockScale);
			
		}
		
		
	}
	
	public void drawBlock(float X, float Y, BuildingBlock block) {
		
		drawBackground(X,Y);
		fill(120);
		pushMatrix();
		translate(X, Y);
		block.display();
		popMatrix();
			
	}
	
	private void drawBackground(float X, float Y) {
		
		fill(0,0,0,0);
		rect(X, Y, blockScale, blockScale);
		
	}
	
	public PVector posToVec(Pos pos) {
		
		return new PVector(blockScale*pos.x, blockScale*pos.y);
		
	}
	
	public Pos vecToPos(PVector vec) {
		
		int posX = scaleConverter(vec.x);
		int posY = scaleConverter(vec.y);
		return new Pos(posX, posY);
		
	}
	
	public Pos XYtoPos(float X, float Y) {
		
		int posX = (int) Math.floor(X/blockScale);
		int posY = (int) Math.floor(Y/blockScale);
		return new Pos(posX, posY);
		
	}
	
	// Scales and snaps to grid
	public int scaleConverter(float vecScale) {
		
		return (int) Math.floor(vecScale/blockScale);
		
	}
	
	private void setBlockObj(Pos pos, BlockObject blockObj) {
		
		blockMap.put(pos, blockObj);
		
	}
	
	////////// ACTION EVENTS //////////
	
	public void keyPressed() {
		
		// instantiate
		switch(key) {
		case('q'):
			//newBlockFocus = blockFactory.getRoad(Direction.EAST,false);
			newBlockFocus = new Road(Direction.EAST, false, this);
			// TODO : newBlockFocus.setParent(this);
			break;
		case('w'):
			//newBlockFocus = blockFactory.getCurve(Direction.EAST, Direction.LEFT,false);
			newBlockFocus = new Curve(Direction.EAST, Direction.LEFT, false, this);
			break;
		case('e'):
			//newBlockFocus = blockFactory.getCrossing(Direction.EAST, Direction.NORTH,false);
			newBlockFocus = new Crossing(Direction.EAST, Direction.NORTH, false, this);
			break;
		case('t'):
			//newBlockFocus = blockFactory.getTCrossing(Direction.EAST, Direction.LEFT,true,false);
			newBlockFocus = new TCrossing(Direction.EAST, Direction.LEFT, true, false, this);
			break;
		case('y'):
			//newBlockFocus = blockFactory.getFork(Direction.EAST,Direction.LEFT,false);
			newBlockFocus = new BendedRoad(Direction.EAST, Direction.LEFT, false, this);
			break;
		}
		
		// modify
		if( newBlockFocus != null ) {
			
			blockFocus = null;
			
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
	
	
	////// BUILDING BLOCK DISPLAY METHODS ////////
	
	public void displayBlock(DataRing<Boolean> connectionRing, boolean diagonal) {
		
		if( diagonal ) {
			
			translate(blockScale/2, blockScale/2);
			rotate(PI/4);
			
			rect(Xoffsets[0] - blockScale/2, Yoffsets[1] - blockScale/2, roadScale, roadScale);
			
			
			for(int i = 0; i < 4; i++) {
				
				if( connectionRing.get(i) ) {
					
					rect(XoffsetsRotated[i] - blockScale/2, YoffsetsRotated[i] - blockScale/2, roadScaleXRotated[i], roadScaleYRotated[i]);
					
				}
				
			}
			
		}
		else {
			
			rect(Xoffsets[0], Yoffsets[1], roadScale, roadScale);
			
			for(int i = 0; i < 4; i++) {
				
				if( connectionRing.get(i) ) {
					
					rect(Xoffsets[i], Yoffsets[i], roadScaleX[i], roadScaleY[i]);
					
				}
				
			}
			
			
		}
		
	}
	
	public void displayBendedRoad(DataRing<Boolean> connectionRing, boolean diagonal, int dir, int bend) {
		
		int bendDir = Direction.dirBend(dir,bend);
		
		if( diagonal ) {
			
			translate(blockScale/2, blockScale/2);
			rotate(PI/4);
			
			rect(Xoffsets[0] - blockScale/2, Yoffsets[1] - blockScale/2, roadScale, roadScale);
			
			
			for(int i = 0; i < 4; i++) {
				
				if( i == bendDir ) {
					
					rotate(-bend*PI/4);
					translate(- blockScale/2, - blockScale/2);
					rect(Xoffsets[i] + XbendedRoadOffset[i], Yoffsets[i] + YbendedRoadOffset[i],
							roadScaleX[i] + bendedRoadScaleX[i], roadScaleY[i]+  bendedRoadScaleY[i]);
					translate(blockScale/2, blockScale/2);
					rotate(bend*PI/4);
					
				}
				
				else if( connectionRing.get(i) ) {
					
					rect(XoffsetsRotated[i] - blockScale/2, YoffsetsRotated[i] - blockScale/2, roadScaleXRotated[i], roadScaleYRotated[i]);
					
				}
				
				
			}
			
		}
		else {
			
			rect(Xoffsets[0], Yoffsets[1], roadScale, roadScale);
			
			for(int i = 0; i < 4; i++) {
				
				if( i == bendDir ) {
					// do temporary diagonal drawing
					translate(blockScale/2, blockScale/2);
					rotate(-bend*PI/4);
					rect(XoffsetsRotated[i] + XbendedRoadOffset[i] - blockScale/2, YoffsetsRotated[i] + YbendedRoadOffset[i] - blockScale/2,
							roadScaleXRotated[i] + bendedRoadScaleX[i], roadScaleYRotated[i] + bendedRoadScaleY[i]);
					rotate(bend*PI/4);
					translate(- blockScale/2,- blockScale/2);
					
				}
				
				else if( connectionRing.get(i) ) {
					
					rect(Xoffsets[i], Yoffsets[i], roadScaleX[i], roadScaleY[i]);
					
				}
				
				
			}
			
			
		}
		
	}

	
}
