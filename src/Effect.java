import processing.core.PVector;


public class Effect {

	private PVector pos;
	private BlockGroup input, output;
	
	public Effect(PVector pos, BlockGroup input, BlockGroup output) {
		
		this.pos = pos;
		this.input = input;
		this.output = output;
		
	}
	
	public PVector pos() {
		return pos;
	}
	
	public void performEffect() {
		
	}


}
