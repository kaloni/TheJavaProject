import java.awt.event.MouseEvent;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import processing.core.PApplet;
import processing.core.PVector;

public class GUI extends PApplet {

	// TODO : Window dimensions in pixels --> pixel-free?
	public final int[] backgroundColor = {0,180,0};
	public final int[] editModeBackgroundColor =  {0, 0, 0,};
	public final int[] clockColor = {128, 255, 0};
	public final int[] effectColor = {204, 204, 0};
	
	private int width;
	private int height;
	private boolean editMode;
	private boolean editSetup;
	private Pos[] editBounds;
	private int numKey;
	
	////////// FINAL CONSTANTS /////////
	
	public float globalScale;
	public PVector globalOrigin;
	public float editGlobalScale;
	public final float editGlobalScaleStandard = 3;
	
	public final float arrowHeadSizeFactor = 5;
	
	public float blockScale = 30f; // any positive float
	public float roadScale = 15f; // between 0 and blockScale
	// actually same as buildScale, using processing scale() for scaling instead..
	public final float editBlockScale = 30f;
	public final float editRoadScale = 20;
	public final float editPointScale = 7;
	// Offset in blocks
	public final Pos editGlobalOffset = new Pos(2,2);
	public Pos editLocalOffset;
	
	public final float bendedRoadFactor = 3f;
	
	public float [] XbendedRoadOffset = {0f, - blockScale/bendedRoadFactor, 0f, 0f};
	public float [] YbendedRoadOffset = {0f, 0f, - blockScale/bendedRoadFactor, 0f};
	public float [] bendedRoadScaleX = {0f, blockScale/bendedRoadFactor, 0f, blockScale/bendedRoadFactor};
	public float [] bendedRoadScaleY = {blockScale/bendedRoadFactor, 0f, blockScale/bendedRoadFactor, 0f};
	
	public float[] Xoffsets = {(blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2, 0f};
	public float[] Yoffsets = {0f, (blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2};
	public float[] roadScaleX = {roadScale, (blockScale - roadScale)/2, roadScale, (blockScale - roadScale)/2};
	public float[] roadScaleY = {(blockScale - roadScale)/2, roadScale, (blockScale - roadScale)/2, roadScale};
	
	public float[] XoffsetsRotated = {(blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2, - blockScale*(sqrt(2) - 1)/2};
	public float[] YoffsetsRotated  = {- blockScale*(sqrt(2) - 1)/2, (blockScale - roadScale)/2, (blockScale + roadScale)/2, (blockScale - roadScale)/2};
	public float [] roadScaleXRotated = {roadScale, blockScale/sqrt(2) - roadScale/2, roadScale, blockScale/sqrt(2) - roadScale/2};
	public float [] roadScaleYRotated = {blockScale/sqrt(2) - roadScale/2, roadScale, blockScale/sqrt(2) - roadScale/2, roadScale};
	
	private Pos mousePos;
	private float mouseX_draggedStart;
	private float mouseY_draggedStart;
	private float mouseX_draggedEnd;
	private float mouseY_draggedEnd;
	private boolean mouseDraggedMarking;
	// these are used in edit mode
	private Pos editMousePos;
	private Pos buildMousePos;
	boolean newLink;
	
	private BlockMap<Pos,BlockGroup> blockMap;
	private Set<Map.Entry<Pos, BlockGroup>> mapSet;
	private BlockGroup currentFocus;
	private BlockGroup newFocus;
	private BlockGroup editFocus;
	
	private int editFocusDir;
	private PairList<BlockGroup> linkedPairList;
	private List<Pair<DataRing<Boolean>>> linkedRingPairList;
	private BlockMap<Pos, BlockGroup> focusMap;
	private BlockMap<Pos, BlockGroup> editFocusMap;
	private Set<Map.Entry<Pos, BlockGroup>> focusSet;
	private Set<Map.Entry<Pos, BlockGroup>> editFocusSet;
	
	// functional maps 
	private DataRing<Float> editArrowMapX;
	private DataRing<Float> editArrowMapY;
	private DataRing<Float> editArrowDiagonalMapX;
	private DataRing<Float> editArrowDiagonalMapY;
	
	private DataRing<Float> editBendedArrowMapX;
	private DataRing<Float> editBendedArrowMapY;
	private DataRing<Float> editBendedDiagonalArrowMapX;
	private DataRing<Float> editBendedDiagonalArrowMapY;
	
	//// EDIT MODIFIERS ////
	
	private HashMap<Pos, Clock> clockMap;
	private HashMap<Pos, Effect> effectMap;
	private HashMap<BlockGroup, Clock> clockLinkMap;
	private List<Clock> clockList;
	private List<Effect> effectList;
	private Clock clockFocus;
	private boolean newClockLink;
	private List<BlockGroup> blockChangedStateList;
	
	///////// ////////// ///////////
	
	// TODO : implement the panel
	private SidePanel sidePanel;
	
	public void setup() {
		
		width = 1000;
		height = 1000;
		globalScale = 1;
		editGlobalScale = editGlobalScaleStandard;
		globalOrigin = new PVector(0,0);
		size(width,height);
		numKey = 1;
		editMode = false;
		editSetup = false;
		editBounds = new Pos[2];
		editFocusDir = 0;
		focusMap = new BlockMap<>();
		editFocusMap = new BlockMap<>();
		editFocusSet = editFocusMap.entrySet();
		linkedPairList = new PairList<>();
		linkedRingPairList = new ArrayList<>();
		focusSet = focusMap.entrySet();
		background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
		
		BuildingBlock dummyBlock = new BuildingBlock(Direction.EAST, this);
		dummyBlock.addState(new Matrix<>(4,4,false));
		BlockGroup dummyBlockGroup = new BlockGroup(dummyBlock);
		blockMap = new BlockMap<>(new Pos(-10,-10), dummyBlockGroup);
		mapSet = blockMap.entrySet();
		// Static reference to this GUI and the blockMap for all groups
		BlockGroup.initGroups(this, blockMap);
		mousePos = new Pos(0,0);
		
		// functional maps
		editArrowMapY = new DataRing<>(4);
		editArrowMapY.set(0, 0.0f);
		editArrowMapY.set(1, 0.5f);
		editArrowMapY.set(2, 1.0f);
		editArrowMapY.set(3, 0.5f);
		editArrowMapX = editArrowMapY.clone();
		editArrowMapX.cycle(1);
		
		editArrowDiagonalMapY = new DataRing<>(4);
		editArrowDiagonalMapY.set(0, -0.5f);
		editArrowDiagonalMapY.set(1, 0f);
		editArrowDiagonalMapY.set(2, 0.5f);
		editArrowDiagonalMapY.set(3, 0.f);
		editArrowDiagonalMapX = editArrowDiagonalMapY.clone();
		editArrowDiagonalMapX.cycle(1);
		
		editBendedArrowMapY = new DataRing<>(4);
		editBendedArrowMapY.set(0, 0f);
		editBendedArrowMapY.set(1, 1f);
		editBendedArrowMapY.set(2, 1f);
		editBendedArrowMapY.set(3, 0f);
		editBendedArrowMapX = editBendedArrowMapY.clone();
		editBendedArrowMapX.cycle(1);
		
		editBendedDiagonalArrowMapY = editArrowMapY.clone();
		editBendedDiagonalArrowMapY.cycle(1);
		editBendedDiagonalArrowMapX = editBendedDiagonalArrowMapY.clone();
		editBendedDiagonalArrowMapX.cycle(1);
		
		addMouseWheelListener(new java.awt.event.MouseWheelListener() { 
		    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) { 
		      mouseWheel(evt.getWheelRotation());
		  }}); 
		
		noStroke();
		
		// effects //
		clockMap = new HashMap<Pos, Clock>();
		effectMap = new HashMap<Pos, Effect>();
		clockLinkMap = new HashMap<BlockGroup, Clock>();
		blockChangedStateList = new ArrayList<>();
		
		Clock masterClock = new Clock(new Pos(0,0), 100);
		clockList = new ArrayList<>();
		clockList.add(masterClock);
		clockMap.put(masterClock.pos(), masterClock);
		
		
	}
	
	public void draw() {
		
		// maybe some scaling alternatives would be nice?
		/*
		translate(globalOrigin.x, globalOrigin.y);
		scale(globalScale);
		translate(- globalOrigin.x, - globalOrigin.y);
		*/
		
		mousePos.x = scaleConverter(mouseX);
		mousePos.y = scaleConverter(mouseY);
		if( editMode ) {
			
			// do some calculations on how to put the blocks in edit mode, depending on their positions
			if( editSetup ) {
				
				List<Pos> posList = new ArrayList<>();
				
				for(Map.Entry<Pos, BlockGroup> groupEntry : focusSet) {
					
					Pos groupPos = groupEntry.getKey();
					posList.add(groupPos.clone());

				}
				
				if( !posList.isEmpty() ) {
					
					Pos[] posArray = new Pos[posList.size()];
					posArray = posList.toArray(posArray);
					editBounds = Pos.minMax(posArray);
					
					if( editGlobalScaleStandard*editBlockScale*( 1 + max( editBounds[1].x - editBounds[0].x, editBounds[1].y - editBounds[0].y ) ) < min(width, height) ) {
						editGlobalScale = editGlobalScaleStandard;
					}
					else {
						
						float oversize = editBlockScale*( 1 + max( editBounds[1].x - editBounds[0].x, editBounds[1].y - editBounds[0].y ) );
						editGlobalScale = min(width, height)/oversize;
						
					}
					
				}
				else {
					
					editBounds = new Pos[2];
					editBounds[0] = new Pos(0,0);
					editBounds[1] = new Pos(0,0);
					
				}
				
				// offset from upper left corner:  of some reason doesn't work... suing editBounds - editGlobalOffset instead?
				
				editLocalOffset = editBounds[0].sub(editGlobalOffset);
				
				editSetup = false;
				
			}
			
			scale(editGlobalScale);
			background(editModeBackgroundColor[0], editModeBackgroundColor[1], editModeBackgroundColor[2]);
			drawEditClocks();
			translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
			
			for(Map.Entry<Pos, BlockGroup> groupEntry : focusSet) {
				
				Pos groupPos = groupEntry.getKey();
				drawGroupEdit(editBlockScale*groupPos.x, editBlockScale*groupPos.y, groupEntry.getValue());
				
			}
			
			displayEditFocus();
			displayClockFocus();
			
			for(Pair<BlockGroup> groupPair : linkedPairList) {
				
				if( focusMap.containsValue(groupPair.first) && focusMap.containsValue(groupPair.second)) {
					drawLink(focusMap.getKey(groupPair.first), focusMap.getKey(groupPair.second));
				}
				
			}
			for(Map.Entry<BlockGroup, Clock> clockBlockEntry : clockLinkMap.entrySet()) {
				drawClockLink(focusMap.getKey(clockBlockEntry.getKey()), clockBlockEntry.getValue().pos());
			}
			if( newLink ) {
				drawNewLink();
			}
			else if( newClockLink ) {
				drawNewClockLink();
			}
			
			blockChangedStateList.clear();
			
		}
		else {
			
			background(backgroundColor[0],backgroundColor[1],backgroundColor[2]);
			
			// draw all in blockMap
			for( Map.Entry<Pos, BlockGroup> blockEntry : mapSet ) {
				
				drawBlock(blockScale*blockEntry.getKey().x, blockScale*blockEntry.getKey().y, blockEntry.getValue());
				
			}
			
			// mark focused block
			if( currentFocus != null ) {
				
				pushMatrix();
				translate(blockScale*blockMap.getKey(currentFocus).x, blockScale*blockMap.getKey(currentFocus).y);
				//drawFocus(currentFocus);
				drawCurrentFocus();
				popMatrix();
				
			}
			
			// draw new focus, and mark which grid position it can be put at
			else if( newFocus != null) {
						
				drawBlock(mouseX - blockScale/2, mouseY - blockScale/2, newFocus);
				pushMatrix();
				translate(toGrid(mouseX), toGrid(mouseY));
				drawFocus(newFocus);
				popMatrix();
				
			}
			
			drawFocusGroups();
			
			if( mouseDraggedMarking ) {
				drawBuildMarking();
			}
			
		}
		
		// same in both build and edit
		
	}
	
	public void updateOffsets() {
		
		for(int i = 0; i < 3; i++) {
			if( i % 2 == 0) {
				Xoffsets[i] = (blockScale - roadScale)/2;
				Yoffsets[3 - i] = (blockScale - roadScale)/2;
				roadScaleX[i] = roadScale;
				roadScaleY[i] = (blockScale - roadScale)/2;
				roadScaleX[3 - i] = (blockScale - roadScale)/2;
				roadScaleY[3 - i] = roadScale;
				XoffsetsRotated[i] = (blockScale - roadScale)/2;
				XoffsetsRotated[3 - i] = (blockScale + roadScale)/2;
				YoffsetsRotated[3 - i] = (blockScale - roadScale)/2;
				YoffsetsRotated[i] = (blockScale + roadScale)/2;
				roadScaleXRotated[i] = roadScale;
				roadScaleYRotated[i] = blockScale/sqrt(2) - roadScale/2;
				roadScaleXRotated[3 - i] = blockScale/sqrt(2) - roadScale/2;
				roadScaleYRotated[3 - i] = roadScale;
			}
		}
		
		Xoffsets[1] = (blockScale + roadScale)/2;
		Yoffsets[2] = (blockScale + roadScale)/2;
		XoffsetsRotated[3] = - blockScale*(sqrt(2) - 1)/2;
		YoffsetsRotated[0] = - blockScale*(sqrt(2) - 1)/2;
		
		XbendedRoadOffset[1] = - blockScale/bendedRoadFactor;
		YbendedRoadOffset[2] = - blockScale/bendedRoadFactor;
		bendedRoadScaleX[1] = blockScale/bendedRoadFactor;
		bendedRoadScaleX[3] = blockScale/bendedRoadFactor;
		bendedRoadScaleY[0] = blockScale/bendedRoadFactor;
		bendedRoadScaleY[2] = blockScale/bendedRoadFactor;
		
	}
	
	public void drawEditClocks() {
		
		noFill();
		
		for(Clock clock : clockList) {
			translate(editBlockScale*clock.pos().x, editBlockScale*clock.pos().y);
			drawClock(clock);
			clock.count();
			translate(- editBlockScale*clock.pos().x, - editBlockScale*clock.pos().y);
		}
		
		noStroke();
		
	}
	
	public void drawClock(Clock clock) {
		
		stroke(clockColor[0], clockColor[1], clockColor[2]);
		rect(clock.pos().x, clock.pos().y, editBlockScale, editBlockScale);
		stroke(255);
		translate(editBlockScale/2, editBlockScale/2);
		ellipse(0, 0, editBlockScale, editBlockScale);
		drawArrow(0, 0, editBlockScale*sin( 2*PI*((float) clock.time())/clock.switchTime() )/2, - editBlockScale*cos( 2*PI*((float) clock.time())/clock.switchTime() )/2 );
		translate(- editBlockScale/2, - editBlockScale/2);
		
	}
	
	public void drawClockLink(Pos blockPos, Pos clockPos) {
		
		translate(- editBlockScale*(editGlobalOffset.x - editBounds[0].x), - editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
		editMousePos = XYtoPos(mouseX/editGlobalScale, mouseY/editGlobalScale);
		buildMousePos = editMousePos.add(editLocalOffset);
		Pos editOffsetSource = clockPos.add(new Pos(1,1));
		Pos editOffsetDest = blockPos.sub(editLocalOffset);
		float Xstart = editBlockScale*editOffsetSource.x/editGlobalScale;
		float Ystart = editBlockScale*editOffsetSource.y/editGlobalScale;
		float controlPointX = 0;
		float controlPointY = 0;
		float Xend = editBlockScale*editOffsetDest.x;
		float Yend = editBlockScale*editOffsetDest.y;
		
		Pos dirPos = blockPos.sub(clockPos);
		dirPos.normalize();
		int dir = Direction.posToDir(dirPos);
		
		if( dir == Direction.EAST || dir == Direction.WEST ) {
			if( dir == Direction.EAST ) {
				Xstart = Xstart + editRoadScale;
			}
			else if( dir == Direction.WEST ) {
				Xend = Xend + editRoadScale;
			}
			Ystart = Ystart + editRoadScale/2;
			Yend = Yend + editRoadScale/2;
			controlPointX = editBlockScale*editOffsetDest.x/editGlobalScale;
			controlPointY = 0f;
		}
		else if( dir == Direction.NORTH || dir == Direction.SOUTH ){
			if( dir == Direction.SOUTH ) {				
				Ystart = Ystart + editRoadScale;
			}
		else if( dir == Direction.NORTH ) {
				Yend = Yend + editRoadScale;
			}
			Xstart = Xstart + editRoadScale/2;
			Xend = Xend + editRoadScale/2;
			controlPointX = 0f;
			controlPointY = editBlockScale*editOffsetDest.y/editGlobalScale;
		}
		
		noFill();
		stroke(173,255,47);
		curve(Xstart, Ystart, Xstart, Ystart, Xend, Yend, controlPointX, controlPointY);
		noStroke();
		
		translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
	}
	public void drawNewClockLink() {
		translate(- editBlockScale*(editGlobalOffset.x - editBounds[0].x), - editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
		editMousePos = XYtoPos(mouseX/editGlobalScale, mouseY/editGlobalScale);
		buildMousePos = editMousePos.add(editLocalOffset);
			
		float Xstart = editBlockScale*clockFocus.pos().x;
		float Ystart = editBlockScale*clockFocus.pos().y;
		float controlPointX = 0;
		float controlPointY = 0;
		float Xend;
		float Yend;
			
		Xend = mouseX/editGlobalScale;
		Yend = mouseY/editGlobalScale;
			
		Pos dirPos = editMousePos.sub(clockFocus.pos());
		dirPos.normalize();
		int dir = Direction.posToDir(dirPos);
		
		if( dir == Direction.EAST || dir == Direction.WEST ) {
			if( dir == Direction.EAST ) {
				Xstart = Xstart + editRoadScale;
			}
			Ystart = Ystart + editRoadScale/2;
				controlPointX = mouseX/editGlobalScale;
				controlPointY = 0f;
			}
			else if( dir == Direction.NORTH || dir == Direction.SOUTH ){
				if( dir == Direction.SOUTH ) {
					Ystart = Ystart + editRoadScale;
				}
				Xstart = Xstart + editRoadScale/2;
				controlPointX = 0f;
				controlPointY = mouseY/editGlobalScale;
			}
		
		noFill();
		stroke(0,128,255);
		curve(Xstart, Ystart, Xstart, Ystart, Xend, Yend, controlPointX, controlPointY);
		noStroke();
		translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
	}
	
	public void drawEditEffects() {
		
		for(Effect effect : effectList) {
			
			drawEffect(effect);
			
		}
		
	}
	
	public void drawEffect(Effect effect) {
		
		fill(effectColor[0], effectColor[1], effectColor[2]);
		rect(effect.pos().x, effect.pos().y, editBlockScale, editBlockScale);
		
	}
	
	public void drawGroupEdit(float X, float Y, BlockGroup blockGroup) {
		
		drawBackground(X,Y);
		translate(X, Y);
		stroke(255);
		fill(0);
		blockGroup.displayEdit();
		translate(-X, -Y);
		
		
	}
	
	public void drawBlock(float X, float Y, BlockGroup blockGroup) {
		
		drawBackground(X,Y);
		pushMatrix();
		translate(X, Y);
		if( blockGroup.focused() ) {
			drawFocus(blockGroup);
		}
		noStroke();
		fill(120);
		blockGroup.display();
		popMatrix();
			
	}
	
	public void drawFocusGroups() {
		
		for(Map.Entry<Pos, BlockGroup> groupEntry : focusSet) {
			
			pushMatrix();
			translate(blockScale*groupEntry.getKey().x, blockScale*groupEntry.getKey().y);
			drawFocus(groupEntry.getValue());
			popMatrix();
			
		}
		
	}
	
	public void drawFocus(BlockGroup blockGroup) {
		
		fill(100,0,255,70);
		
		for(Map.Entry<Pos, BuildingBlock> blockEntry : blockGroup.entrySet() ) {
			
			rect(blockScale*blockEntry.getKey().x, blockScale*blockEntry.getKey().y, blockScale, blockScale);
			
		}
		
	}
	
	public void drawCurrentFocus() {
		
		if( currentFocus != null) {
			
			stroke(255,128,0);
			strokeWeight(2);
			noFill();
			rect(0, 0, blockScale, blockScale);
			strokeWeight(1);
			noStroke();
		}
		
	}
	
	private void drawBackground(float X, float Y) {
		
		fill(0,0,0,0);
		rect(X, Y, blockScale, blockScale);
		
	}
	
	private void drawArrow(float fromX, float fromY, float toX, float toY) {
		
		float dX = toX - fromX;
		float dY = toY - fromY;
		float deltaDist = sqrt(dX*dX + dY*dY);
		line(fromX, fromY, toX, toY);
		float angle = atan2(dY, dX);
		pushMatrix();
		translate(toX,toY);
		rotate(angle);
		
		line(0, 0, -deltaDist/arrowHeadSizeFactor, deltaDist/arrowHeadSizeFactor);
		line(0, 0, -deltaDist/arrowHeadSizeFactor, -deltaDist/arrowHeadSizeFactor);
		
		popMatrix();
		
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
	
	public float toGrid(float vecScale) {
		
		return blockScale*floor(vecScale/blockScale);
		
	}
	
	// Scales and snaps to grid
	public int scaleConverter(float vecScale) {
		
		return (int) floor(vecScale/blockScale);
		
	}
	
	////////// ACTION EVENTS //////////
	
	public void keyPressed() {
		
		if( editMode ) {
			editKeyPressed();
			
		}
		else {
			buildKeyPressed();
		}
		
	}
	
	public void editKeyPressed() {
		
		if( keyCode == TAB ) {
			editFocus = null;
			clockFocus = null;
			editFocusMap.clear();
			editMode = false;
			newLink = false;
			newClockLink = false;
		}
		
		if( clockFocus != null ) {
			
			switch(key) {
			case('q'):
				newClockLink = !newClockLink;
				break;
			case('d'):
				for(Iterator<Map.Entry<BlockGroup, Clock>> iter = clockLinkMap.entrySet().iterator(); iter.hasNext();) {
					
					Map.Entry<BlockGroup, Clock> clockLink = iter.next();
					
					if( editFocusMap.containsValue(clockLink.getKey()) ) {
						
						clockLink.getValue().removeLink(clockLink.getKey());
						iter.remove();
						
					}
					
				}
			}
			
		}
		else if( editFocus != null ) {
			
			switch(key) {
			case('s'):
				editFocus.changeState();
				break;
			case('q'):
					newLink = !newLink;
			break;
			case('r'):
				linkedPairList.relink();
			break;
			case('d'):
				for(Iterator<Pair<BlockGroup>> iter = linkedPairList.iterator(); iter.hasNext();) {
					
					Pair<BlockGroup> linkedPair = iter.next();
						
					if( editFocusMap.containsValue(linkedPair.first) && editFocusMap.containsValue(linkedPair.second) ) {
							
						/*
						//editFocusMap.remove(linkedPair.first);
						//editFocusMap.remove(linkedPair.second);
						*/
						linkedPair.first.removeLink(linkedPair.second);
						linkedPair.second.removeLink(linkedPair.first);
						iter.remove();
							
					}
					
				}
				
			}
			
		}
		
	}
	
	public void buildKeyPressed() {
		
		// if pressing anything except SHIFT, TAB or D makes all focus disappear
		if( keyCode != SHIFT && keyCode != TAB && keyCode != UP && key != 'd' && key != 'r' && key != 'f' && key != 'v' && key != ' ') {
			focusMap.clear();
		}
		if( keyCode == TAB ) {
			editMode = !editMode;
			editSetup = true;
		}
		if( keyCode == UP ) {
			blockScale = 1.1f*blockScale;
			roadScale = 1.1f*roadScale;
			updateOffsets();
		}
		if( keyCode == DOWN ) {
			blockScale = blockScale/1.1f;
			roadScale = roadScale/1.1f;
			updateOffsets();
		}
		if( key == '1' || key == '2' || key == '3' || key == '4' ) {
			numKey = Character.getNumericValue(key);
		}
		
		// instantiators
		switch(key) {
		case('q'):
			newFocus = BlockGroup.newLongRoad(numKey, Direction.EAST, true);
			break;
		case('l'):
			newFocus = BlockGroup.newLaneRoad(numKey, Direction.EAST, true);
			break;
		case('w'):
			newFocus = BlockGroup.newCurve(numKey, true);
			break;
		case('e'):
			newFocus = new BlockGroup( new Crossing(Direction.EAST, Direction.LEFT, true, this) );
			break;
		case('t'):
			newFocus = new BlockGroup( new TCrossing(Direction.EAST, Direction.LEFT, true, true, this) );
			break;
		case('y'):
			newFocus = BlockGroup.newBendedRoad(numKey, Direction.EAST, Direction.LEFT, true);
			break;
		case('c'):
			if( currentFocus != null ) {
				newFocus = currentFocus.clone();
			}
			break;
		}
		
		// modify
		if( newFocus != null ) {
			
			currentFocus = null;
			
			switch(key) {
			case('r'):
				newFocus.rotate();
				break;
			case('f'):
				newFocus.flip();
				break;
			case('v'):
				newFocus.revert();
				break;
			case('d'):
				newFocus = null;
				break;
			}
	
		}
		else {
			
			if( currentFocus != null ) {
				switch(key) {
					case('r'):
						currentFocus.rotate();
						break;
					case('f'):
						currentFocus.flip();
						break;
					case('v'):
						currentFocus.revert();
						break;
						
				}	
			}
			if( key == 'd' ) {
				
				if( currentFocus != blockMap.getDummyValue() ) {
					
					for(Map.Entry<Pos, BlockGroup> groupFocus : focusSet) {
						
						mapSet.remove(groupFocus);
					}
					currentFocus = null;
					focusMap.clear();
				}
				
			}
			
		}
	
		
	}
	
	
	// Works ok...
	public void blockStateChanged(BlockGroup block) {
	
		for(Pair<BlockGroup> blockPair : linkedPairList) {
			
			if( blockPair.first == block && ! blockChangedStateList.contains(block) ) {
				
				blockChangedStateList.add(block);
				blockPair.second.changeState();
				
			}
			
		}
		
	}
	
	public void drawLink(Pos source, Pos dest) {
		
		translate(- editBlockScale*(editGlobalOffset.x - editBounds[0].x), - editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
		Pos editOffsetSource = source.sub(editLocalOffset);
		Pos editOffsetDest = dest.sub(editLocalOffset);
		float Xstart = editBlockScale*editOffsetSource.x;
		float Ystart = editBlockScale*editOffsetSource.y;
		float Xend = editBlockScale*editOffsetDest.x;
		float Yend = editBlockScale*editOffsetDest.y;
		float controlPointX = 0;
		float controlPointY = 0;
		
		
		Pos dirPos = dest.sub(source);
		dirPos.normalize();
		int dir = Direction.posToDir(dirPos);
		
		if( dir == Direction.EAST || dir == Direction.WEST ) {
			if( dir == Direction.EAST ) {
				Xstart = Xstart + editRoadScale;
			}
			else if( dir == Direction.WEST ) {
				Xend = Xend + editRoadScale;
			}
			Ystart = Ystart + editRoadScale/2;
			Yend = Yend + editRoadScale/2;
			controlPointX = editBlockScale*editOffsetDest.x/editGlobalScale;
			controlPointY = 0f;
		}
		else if( dir == Direction.NORTH || dir == Direction.SOUTH ){
			if( dir == Direction.SOUTH ) {				
				Ystart = Ystart + editRoadScale;
			}
		else if( dir == Direction.NORTH ) {
				Yend = Yend + editRoadScale;
			}
			Xstart = Xstart + editRoadScale/2;
			Xend = Xend + editRoadScale/2;
			controlPointX = 0f;
			controlPointY = editBlockScale*editOffsetDest.y/editGlobalScale;
		}
		
		noFill();
		stroke(173,255,47);
		curve(Xstart, Ystart, Xstart, Ystart, Xend, Yend, controlPointX, controlPointY);
		noStroke();
		
		translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
	}
	
	public void drawNewLink() {
		
		translate(- editBlockScale*(editGlobalOffset.x - editBounds[0].x), - editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
		for(Map.Entry<Pos, BlockGroup> editFocusEntry : editFocusMap.entrySet() ) {
			
			Pos focusPos = editFocusMap.getKey(editFocusEntry.getValue());
			editMousePos = XYtoPos(mouseX/editGlobalScale, mouseY/editGlobalScale);
			buildMousePos = editMousePos.add(editLocalOffset);
			
			float Xstart = editBlockScale*focusPos.x;
			float Ystart = editBlockScale*focusPos.y;
			float controlPointX = 0;
			float controlPointY = 0;
			float Xend;
			float Yend;
			
				Xend = mouseX/editGlobalScale;
				Yend = mouseY/editGlobalScale;
				
				Pos dirPos = editMousePos.sub(focusPos);
				dirPos.normalize();
				int dir = Direction.posToDir(dirPos);
				
				if( dir == Direction.EAST || dir == Direction.WEST ) {
					if( dir == Direction.EAST ) {
						Xstart = Xstart + editRoadScale;
					}
					Ystart = Ystart + editRoadScale/2;
					controlPointX = mouseX/editGlobalScale;
					controlPointY = 0f;
				}
				else if( dir == Direction.NORTH || dir == Direction.SOUTH ){
					if( dir == Direction.SOUTH ) {
						Ystart = Ystart + editRoadScale;
					}
					Xstart = Xstart + editRoadScale/2;
					controlPointX = 0f;
					controlPointY = mouseY/editGlobalScale;
				}
			
			noFill();
			stroke(173,255,47);
			curve(Xstart, Ystart, Xstart, Ystart, Xend, Yend, controlPointX, controlPointY);
			noStroke();
			
		}
		
		translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
	}
	
	public void mousePressed() {
		if( editMode ) { 
			editMousePressed();
		}
		else { 
			mouseX_draggedStart = mouseX;
			mouseY_draggedStart = mouseY;
			buildMousePressed(); 
		}
	}
	
	public void editMousePressed() {
		
		translate(- editBlockScale*(editGlobalOffset.x - editBounds[0].x), - editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		editMousePos = XYtoPos(mouseX/editGlobalScale, mouseY/editGlobalScale);
		buildMousePos = editMousePos.add(editLocalOffset);
		//System.out.println(editMousePos + " " + editBounds[1].sub(editLocalOffset));
		//if( editMousePos.compareTo(editGlobalOffset) >= 0 && editMousePos.compareTo(editBounds[1].sub(editLocalOffset)) <= 0 ) {
		
		if( focusMap.containsKey(buildMousePos) ) {
			
			if( (keyPressed && keyCode == SHIFT) ) {
					
				editFocus = blockMap.get(buildMousePos);
				editFocusMap.put(editMousePos, editFocus);
					
			}
			else if( newLink ) {
					
				BlockGroup blockToBeLinked = blockMap.get(buildMousePos);
				
				for(BlockGroup focusBlock : editFocusMap.values()) {
					
					linkedPairList.add(Pair.of(focusBlock, blockToBeLinked));
					focusBlock.addLink(blockToBeLinked);
					blockToBeLinked.addLink(focusBlock);
					
				}
				/*
				for(Map.Entry<Pos, BlockGroup> focusEntry : editFocusMap.entrySet() ) {
					linkedPairList.add(Pair.of(focusEntry.getValue(), blockMap.get(buildMousePos)));
					focusEntry.getValue().addLink(blockMap.get(buildMousePos));
					blockMap.get(buildMousePos).addLink(focusEntry.getValue());
				}
				*/
					
				newLink = false;
					
			}
			else if( newClockLink ) {
				
				clockFocus.addLink(blockMap.get(buildMousePos));
				clockLinkMap.put(blockMap.get(buildMousePos), clockFocus);
				newClockLink = false;
				
			}
			else {
					
				editFocusMap.clear();
				editFocus = blockMap.get(buildMousePos);
				editFocusMap.put(editMousePos, editFocus);
				clockFocus = null;
					
			}
				
		}
		else if( clockMap.containsKey(editMousePos) ) {
			
			if( !(keyPressed && keyCode == SHIFT) ) {
				editFocusMap.clear();
			}
			clockFocus = clockMap.get(editMousePos);
			
		}
			
		else {
			
			editFocusMap.clear();
			editFocus = null;
			clockFocus = null;
			newClockLink = false;
			
		}
		
		translate(editBlockScale*(editGlobalOffset.x - editBounds[0].x), editBlockScale*(editGlobalOffset.y - editBounds[0].y));
		
	}
	
	public void buildMousePressed() {
		
		if( newFocus != null) {
	
			Pos mousePos = XYtoPos(mouseX, mouseY);
			if( blockMap.put(mousePos, newFocus) != null ) {
				
				if( keyPressed && keyCode == SHIFT ) {
					newFocus = newFocus.clone();
				}
				else {
					
					currentFocus = newFocus;
					focusMap.put(mousePos,currentFocus);
					newFocus = null;
					
				}
				
			}
			
		}
		else {
			
			Pos mousePos = XYtoPos(mouseX, mouseY);

				
			if( blockMap.containsKey(mousePos) ) {
					
				if( !( keyPressed && keyCode == SHIFT  ) ) {
						
					focusMap.clear();
						
				}
					
				if( focusMap.containsKey(mousePos) ) {
					focusMap.remove(mousePos);
				}
				else {
					currentFocus = blockMap.getValue(mousePos);
					focusMap.put(mousePos, currentFocus);
				}
					
			}
			else {
					
				currentFocus = null;
				focusMap.clear();
					
			}
				
			
		}
		
	}
	
	public void mouseDragged() {
		if( editMode ) { editMouseDragged(); }
		else { buildMouseDragged(); }
	}
	
	public void editMouseDragged() {
		
	}
	
	public void buildMouseDragged() {
		
		if( keyPressed && key == ' ' ) {
			mouseDraggedMarking = true;
			mouseX_draggedEnd = mouseX;
			mouseY_draggedEnd = mouseY;
		}
		
	}
	
	public void drawBuildMarking() {
		
		fill(100,0,255,30);
		stroke(255, 128, 0, 70);
		strokeWeight(3);
		rect(mouseX_draggedStart, mouseY_draggedStart, mouseX_draggedEnd - mouseX_draggedStart, mouseY_draggedEnd - mouseY_draggedStart);
		strokeWeight(1);
		noStroke();
		
	}
	
	public void mouseReleased() {
		
		if( mouseDraggedMarking ) {
			
			mouseDraggedMarking = false;
			Pos mousePosDraggedStart = XYtoPos(mouseX_draggedStart, mouseY_draggedStart);
			Pos mousePosDraggedEnd = XYtoPos(mouseX_draggedEnd, mouseY_draggedEnd);
			// allow marking to be drawn in any direction, get upper left corner and lower right corner
			Pos[] minMaxPos = Pos.minMax(mousePosDraggedStart, mousePosDraggedEnd);
			System.out.println(mousePosDraggedStart);
			System.out.println(mousePosDraggedEnd);
			
			focusMap.clear();
			
			for(Map.Entry<Pos, BlockGroup> blockEntry : mapSet) {
				
				// look for blocks inside the marking
				if( minMaxPos[0].compareTo(blockEntry.getKey()) <= 0 && blockEntry.getKey().compareTo(minMaxPos[1]) <= 0 ) {
					System.out.println(blockEntry.getKey());
					focusMap.put(blockEntry.getKey(), blockEntry.getValue());
					
				}
				
			}
			
		}
		
	}
	
	
	public void mouseWheel(int scroll) {
		
		globalOrigin.x = globalOrigin.x + 0.01f*(mouseX - globalOrigin.x);
		globalOrigin.y = globalOrigin.y + 0.01f*(mouseY - globalOrigin.y);
		globalScale = globalScale - scroll*0.01f;
		translate(globalOrigin.x, globalOrigin.y);
		scale(globalScale);
		translate(- globalOrigin.x, - globalOrigin.y);
		//translate(mouseX,mouseY); // use translate around scale
		/*
		float tempBlockScale = blockScale;
		blockScale = blockScale - scroll*0.1f;
		roadScale = roadScale - (roadScale/tempBlockScale)*scroll*0.1f;
		updateOffsets();
		*/
		//translate(-mouseX, -mouseY);
		
	}
	
	
	////// BUILDING BLOCK DISPLAY METHODS ////////
	
	public void displayClockFocus() {
		
		if( clockFocus != null) {
			
			strokeWeight(2);
			stroke(255,165,0);
			noFill();
			rect(editBlockScale*(clockFocus.pos().x + editLocalOffset.x), editBlockScale*(clockFocus.pos().y + editLocalOffset.y), editBlockScale, editBlockScale);
			noStroke();
			strokeWeight(1);
		}
		
	}
	
	public void displayEditFocus() {
		
		for(Map.Entry<Pos, BlockGroup> editFocusEntry : editFocusMap.entrySet() ) {
			
			if( editFocusEntry.getValue() != null) {
				stroke(255,165,0);
				noFill();
				rect(editBlockScale*(editFocusEntry.getKey().x + editLocalOffset.x), editBlockScale*(editFocusEntry.getKey().y + editLocalOffset.y), editRoadScale, editRoadScale);
			}
			
		}
		
		noStroke();
		
	}
	
	// EDIT MODE 
	public void displayBlockEdit(Pos groupOffset, Matrix<Boolean> connectionMatrix, Matrix<Boolean> stateMatrix, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, boolean diagonal) {
		
		translate(editBlockScale*groupOffset.x, editBlockScale*groupOffset.y);
		rect(0, 0, editRoadScale, editRoadScale);
		fill(255,0,0);
		
		if( diagonal ) {
			
			translate(editRoadScale/2, editRoadScale/2);
			rotate(PI/4);
			
			for(int i = 0; i < 4; i++) {
				
				if( inputRing.get(i) || outputRing.get(i) ) {
					/*
					if( inputRing.get(i) ) {
						fill(0,0,255);
					}
					else {
						fill(255,0,0);
					}
					*/
					noFill();
					
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						ellipse(0, (i - 1)*editRoadScale/sqrt(2), editPointScale, editPointScale);
					}
					else {
						ellipse((2 - i)*editRoadScale/sqrt(2), 0, editPointScale, editPointScale);
					}
				
				}
				
			}
			
			stroke(0, 150, 255);
			strokeWeight(2);
			
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i, j) ) {
						
						drawArrow(editRoadScale*editArrowDiagonalMapX.get(i), editRoadScale*editArrowDiagonalMapY.get(i),
								editRoadScale*editArrowDiagonalMapX.get(j), editRoadScale*editArrowDiagonalMapY.get(j) );
							
					}
				}
			}
			
			noStroke();
			strokeWeight(1);
			
			// redLights
			
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i,j) && stateMatrix.get(i, j) ) {
						fill(0, 255, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/sqrt(2), - editRoadScale*Direction.dirToPos(i).y/sqrt(2), editRoadScale/5, editRoadScale/5);
					}
					else if( connectionMatrix.get(i,j) ) {
						fill(255, 0, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/sqrt(2), - editRoadScale*Direction.dirToPos(i).y/sqrt(2), editRoadScale/5, editRoadScale/5);
					}
				}
			}
			
			rotate(-PI/4);
			translate(- editRoadScale/2, - editRoadScale/2);
			
		}
		else {
			
			for(int i = 0; i < 4; i++) {
				
				if( inputRing.get(i) || outputRing.get(i) ) {
					/*
					if( inputRing.get(i) ) {
						fill(0,0,255);
					}
					else {
						fill(255,0,0);
					}
					*/
					noFill();
					
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						ellipse(editRoadScale/2, i*editRoadScale/2, editPointScale, editPointScale);
					}
					else {
						ellipse(editRoadScale*(3 - i)/2, editRoadScale/2, editPointScale, editPointScale);
					}
					
				}
				
			}
			
			stroke(0, 150, 255);
			strokeWeight(2);
			// arrows
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i, j) ) {
						
						drawArrow(editRoadScale*editArrowMapX.get(i), editRoadScale*editArrowMapY.get(i),
								editRoadScale*editArrowMapX.get(j), editRoadScale*editArrowMapY.get(j) );
							
					}
				}
			}
			noStroke();
			strokeWeight(1);
			
			// redLights
			translate(editRoadScale/2, editRoadScale/2);
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i,j) && stateMatrix.get(i, j) ) {
						fill(0, 255, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/2, - editRoadScale*Direction.dirToPos(i).y/2, editRoadScale/5, editRoadScale/5);
					}
					else if( connectionMatrix.get(i,j) ) {
						fill(255, 0, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/2, - editRoadScale*Direction.dirToPos(i).y/2, editRoadScale/5, editRoadScale/5);
					}
				}
			}
			translate(- editRoadScale/2, - editRoadScale/2);
			
		}
		
		translate(- editBlockScale*groupOffset.x, - editBlockScale*groupOffset.y);
		
	}
	
	public void displayBendedRoadEdit(Pos groupOffset, Matrix<Boolean> connectionMatrix, Matrix<Boolean> stateMatrix, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, boolean diagonal, int dir, int bend) {
		System.out.println(outputRing);
		translate(editBlockScale*groupOffset.x, editBlockScale*groupOffset.y);
		rect(0, 0, editRoadScale, editRoadScale);
		fill(255,0,0);
		
		if( diagonal ) {
			
			noFill();
			for(int i = 0; i < 4; i++) {
				
				if( inputRing.get(i) ) {
					
					//fill(0,0,255);
					ellipse(editRoadScale*editBendedArrowMapX.get(i), editRoadScale*editBendedArrowMapY.get(i), editPointScale, editPointScale);
					
				}
				if( outputRing.get(i) ) {
					
					//fill(255,0,0);
					ellipse(editRoadScale*editBendedDiagonalArrowMapX.get(i), editRoadScale*editBendedDiagonalArrowMapY.get(i), editPointScale, editPointScale);
					
				}
				
			}
			stroke(0, 150, 255);
			strokeWeight(2);
			
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( inputRing.get(i) && outputRing.get(j) ) {
						
						drawArrow(editRoadScale*editBendedArrowMapX.get(i), editRoadScale*editBendedArrowMapY.get(i),
								editRoadScale*editBendedDiagonalArrowMapX.get(j), editRoadScale*editBendedDiagonalArrowMapY.get(j) );
							
					}
				}
			}
			noStroke();
			strokeWeight(1);
			
			translate(editRoadScale/2, editRoadScale/2);
			rotate(PI/4);
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i,j) && stateMatrix.get(i, j) ) {
						fill(0, 255, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/sqrt(2), - editRoadScale*Direction.dirToPos(i).y/sqrt(2), editRoadScale/5, editRoadScale/5);
					}
					else if( connectionMatrix.get(i,j) ) {
						fill(255, 0, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/sqrt(2), - editRoadScale*Direction.dirToPos(i).y/sqrt(2), editRoadScale/5, editRoadScale/5);
					}
				}
			}
			rotate(- PI/4);
			translate(- editRoadScale/2, - editRoadScale/2);
			
		}
		else {
			noFill();
			for(int i = 0; i < 4; i++) {
				
				
				if( inputRing.get(i) ) {
					
					//fill(0,0,255);
					ellipse(editRoadScale*editArrowMapX.get(i), editRoadScale*editArrowMapY.get(i), editPointScale, editPointScale);
					
				}
				else if( outputRing.get(i) ) {
					
					//fill(255,0,0);
					ellipse(editRoadScale*editBendedArrowMapX.get(i), editRoadScale*editBendedArrowMapY.get(i), editPointScale, editPointScale);
					
				}
					
			}
			
			stroke(0, 150, 255);
			strokeWeight(2);
			
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( inputRing.get(i) && outputRing.get(j) ) {
						
						drawArrow(editRoadScale*editArrowMapX.get(i), editRoadScale*editArrowMapY.get(i),
								editRoadScale*editBendedArrowMapX.get(j), editRoadScale*editBendedArrowMapY.get(j) );
							
					}
				}
			}
			
			noStroke();
			strokeWeight(1);
			
			translate(editRoadScale/2, editRoadScale/2);
			
			for(int i = 0; i < 4; i++) {
				for(int j = 0; j < 4; j++) {
					if( connectionMatrix.get(i,j) && stateMatrix.get(i, j) ) {
						fill(0, 255, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/2, - editRoadScale*Direction.dirToPos(i).y/2, editRoadScale/5, editRoadScale/5);
					}
					else if( connectionMatrix.get(i,j) ) {
						fill(255, 0, 0);
						ellipse(editRoadScale*Direction.dirToPos(i).x/2, - editRoadScale*Direction.dirToPos(i).y/2, editRoadScale/5, editRoadScale/5);
					}
				}
			}
			translate(- editRoadScale/2, - editRoadScale/2);
			
		}
	
		translate(- editBlockScale*groupOffset.x, - editBlockScale*groupOffset.y);
		
	}
	
	// BUILD MODE
	
	public void displayBlock(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, boolean diagonal) {
		
		pushMatrix();
		translate(blockScale*groupOffset.x, blockScale*groupOffset.y);
		
		if( diagonal ) {
			
			translate(blockScale/2, blockScale/2);
			rotate(PI/4);
			
			rect(Xoffsets[0] - blockScale/2, Yoffsets[1] - blockScale/2, roadScale, roadScale);
			
			for(int i = 0; i < 4; i++) {
				
				if( inputRing.get(i) || outputRing.get(i) ) {
					
					rect(XoffsetsRotated[i] - blockScale/2, YoffsetsRotated[i] - blockScale/2, roadScaleXRotated[i], roadScaleYRotated[i]);
					
				}
					
			}
			
			drawBlockDiagonalArrows(groupOffset, inputRing, outputRing, blockScale);
			
		}
		else {
			
			rect(Xoffsets[0], Yoffsets[1], roadScale, roadScale);
			
			for(int i = 0; i < 4; i++) {
				
				if( inputRing.get(i) || outputRing.get(i) ) {
					
					rect(Xoffsets[i], Yoffsets[i], roadScaleX[i], roadScaleY[i]);
					
				}
				
			}
			
			drawBlockArrows(groupOffset, inputRing, outputRing, blockScale);
			
		}
		
		popMatrix();
		
	}
	
	public void displayBendedRoad(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, boolean diagonal, int dir, int bend) {
		
		int bendDir = Direction.dirBend(dir,bend);
		translate(groupOffset.x, groupOffset.y);
		
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
					
					
					stroke(255);
					
					if( outputRing.get(i) ) {
						
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + i/2));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + ((i + 1) % 4)/2), blockScale*(groupOffset.y + 0.5f));
						}
					
					}
					// if inputRing
					else {
						 
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + i/2),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + ((i + 1) % 4)/2), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f));
						}
						
					}
					
					noStroke();
					
					translate(blockScale/2, blockScale/2);
					rotate(bend*PI/4);
					
				}
				
				else if( inputRing.get(i) || outputRing.get(i) ) {
					
					rect(XoffsetsRotated[i] - blockScale/2, YoffsetsRotated[i] - blockScale/2, roadScaleXRotated[i], roadScaleYRotated[i]);
					
					// DIAGONAL ARROW
					stroke(255);
					if( outputRing.get(i) ) {
						
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x ), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x), blockScale*(groupOffset.y + (i - 1.0f)/sqrt(2) ));
						}
						else {
							drawArrow(blockScale*(groupOffset.x), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x + (2 - i)/sqrt(2) ), blockScale*(groupOffset.y));
						}
					
					}
					// if inputRing
					else {
						 
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x), blockScale*(groupOffset.y + (i - 1)/sqrt(2) ),
									blockScale*(groupOffset.x ), blockScale*(groupOffset.y));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + (2 - i)/sqrt(2) ), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x), blockScale*(groupOffset.y));
						}
						
					}
					noStroke();
					
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
					
					// DIAGONAL ARROW
					stroke(255);
					if( outputRing.get(i) ) {
						
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x ), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x), blockScale*(groupOffset.y + (i - 1.0f)/sqrt(2) ));
						}
						else {
							drawArrow(blockScale*(groupOffset.x), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x + (2 - i)/sqrt(2) ), blockScale*(groupOffset.y));
						}
					
					}
					// if inputRing
					else {
						 
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x), blockScale*(groupOffset.y + (i - 1)/sqrt(2) ),
									blockScale*(groupOffset.x ), blockScale*(groupOffset.y));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + (2 - i)/sqrt(2) ), blockScale*(groupOffset.y),
									blockScale*(groupOffset.x), blockScale*(groupOffset.y));
						}
						
					}
					noStroke();
					
					rotate(bend*PI/4);
					translate(- blockScale/2,- blockScale/2);
					
				}
				
				else if( inputRing.get(i) || outputRing.get(i) ) {
					
					rect(Xoffsets[i], Yoffsets[i], roadScaleX[i], roadScaleY[i]);
					
					stroke(255);
					if( outputRing.get(i) ) {
						
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + i/2));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + ((i + 1) % 4)/2), blockScale*(groupOffset.y + 0.5f));
						}
					
					}
					// if inputRing
					else {
						 
						if( i == Direction.NORTH || i == Direction.SOUTH ) {
							drawArrow(blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + i/2),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f));
						}
						else {
							drawArrow(blockScale*(groupOffset.x + ((i + 1) % 4)/2), blockScale*(groupOffset.y + 0.5f),
									blockScale*(groupOffset.x + 0.5f), blockScale*(groupOffset.y + 0.5f));
						}
						
					}
					noStroke();
					
				}
				
			}
			
		}
		
	}
	
private void drawBlockArrows(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, float scale) {
		
		stroke(255);
		
		for(int i = 0; i < 4; i++) {
			
			if( inputRing.get(i) || outputRing.get(i) ) {
				
				if( outputRing.get(i) ) {
					
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2));
					}
					else {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + ((i + 1) % 4)/2), scale*(groupOffset.y + 0.5f));
					}
				
				}
				// if inputRing
				else {
					 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
					else {
						drawArrow(scale*(groupOffset.x + ((i + 1) % 4)/2), blockScale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
					
				}
				
			}
			
		}
		
		noStroke();
		
	}
	
	private void drawBlockDiagonalArrows(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, float scale) {
		
		stroke(255);
		
		for(int i = 0; i < inputRing.size(); i++) {
			
			if( inputRing.get(i) || outputRing.get(i) ) {
				
				if( outputRing.get(i) ) {
					
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y + (i - 1.0f)/sqrt(2) ));
					}
					else {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y),
								scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y));
					}
				
				}
				// if inputRing
				else {
					 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y + (i - 1)/sqrt(2) ),
								scale*(groupOffset.x ), scale*(groupOffset.y));
					}
					else {
						drawArrow(scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y));
					}
					
				}
				
			}
			
		}
		
		noStroke();
		
	}
	
	public void drawBendedBlockArrows(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, float scale, int dir, int bend) {
		
		int bendDir = Direction.dirBend(dir, bend);
		
		stroke(255);
		
		for(int i = 0; i < 4; i++) {
				
			if( i == bendDir ) {
					
				translate(scale/2, scale/2);
				rotate(-bend*PI/4);
					
				if( outputRing.get(i) ) {
						
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y + (i - 1.0f)/sqrt(2) ));
					}
					else {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y),
								scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y));
					}
					
				}
				// if inputRing
				else if( inputRing.get(i) ){
					 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y + (i - 1)/sqrt(2) ),
								scale*(groupOffset.x ), scale*(groupOffset.y));
					}
					else {
						drawArrow(scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y));
					}
						
				}
					
				rotate(bend*PI/4);
				translate(- scale/2, scale/2);
				
			}
			else {
					
				if( outputRing.get(i) ) {
					
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2));
					}
					else {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + ((i + 1) % 4)/2), scale*(groupOffset.y + 0.5f));
					}
					
				}
				// if inputRing
				else if( inputRing.get(i) ){
						 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
					else {
						drawArrow(scale*(groupOffset.x + ((i + 1) % 4)/2), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
						
				}
					
			}
			
		}
		
		noStroke();
		
	}
	
	public void drawBendedBlockDiagonalArrows(Pos groupOffset, DataRing<Boolean> inputRing, DataRing<Boolean> outputRing, float scale, int dir, int bend) {
		
		int bendDir = Direction.dirBend(dir, bend);
		
		stroke(255);
		
		for(int i = 0; i < 4; i++) {
			
			if( i == bendDir ) {
				
				rotate(-bend*PI/4);
				translate(- scale/2, - scale/2);
					
				if( outputRing.get(i) ) {
						
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2));
					}
					else {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + ((i + 1) % 4)/2), scale*(groupOffset.y + 0.5f));
					}
					
				}
					// if inputRing
				else if( inputRing.get(i) ) { 
						 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + i/2),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
					else {
						drawArrow(scale*(groupOffset.x + ((i + 1) % 4)/2), scale*(groupOffset.y + 0.5f),
								scale*(groupOffset.x + 0.5f), scale*(groupOffset.y + 0.5f));
					}
						
				}
				
				translate(scale/2,scale/2);
				rotate(bend*PI/4);
					
			}
			else {
					
				if( outputRing.get(i) ) {
						
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y + (i - 1.0f)/sqrt(2) ));
						}
					else {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y),
								scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y));
					}
					
				}
					// if inputRing
				else if( inputRing.get(i) ){
						 
					if( i == Direction.NORTH || i == Direction.SOUTH ) {
						drawArrow(scale*(groupOffset.x), scale*(groupOffset.y + (i - 1)/sqrt(2) ),
								scale*(groupOffset.x ), scale*(groupOffset.y));
					}
					else {
						drawArrow(scale*(groupOffset.x + (2 - i)/sqrt(2) ), scale*(groupOffset.y),
								scale*(groupOffset.x), scale*(groupOffset.y));
					}
						
				}
					
			}
			
		}
		
		noStroke();
		
	}

	
}
