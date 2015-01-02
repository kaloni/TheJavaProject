import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;


public class Clock {

	private PVector pos;
	private int switchTime;
	private int time;
	private List<BlockGroup> blockLinkList;
	
	public Clock(PVector pos, int switchTime) {
		
		this.pos = pos;
		this.switchTime = switchTime;
		time = 0;
		blockLinkList = new ArrayList<>();
	}
	
	
	public PVector pos() {
		return pos;
	}
	
	public int time() {
		return time;
	}
	public int switchTime() {
		return switchTime;
	}
	// count up the clock, change the state of all connected blocks when reacing switchTime
	public void count() {
		
		time = time + 1;
		if( (time % switchTime) == 0) {
			time = 0;
			for(BlockGroup block : blockLinkList) {
				block.changeState();
			}
		}
		
	}
	
	public void setSwitchTime(int switchTime) {
		this.switchTime = switchTime;
	}

}
