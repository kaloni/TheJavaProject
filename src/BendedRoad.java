
public class BendedRoad extends Curve {

	public BendedRoad(int dir, int bend, boolean redLight, GUI gui) {
		
		super(dir, bend, redLight, gui);
		
	}
	
	public void display() {
		
		gui.displayBendedRoad(connectionRing, diagonal, dir, bend);
	}
	
	@Override
	public void flip() {
		
		super.flip();
		bend = -bend;
		
		
	}
	
}
