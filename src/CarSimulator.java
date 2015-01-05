import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CarSimulator {

	private List<CarArea> areaList;
	private List<Car> carList;
	private List<Car> carToRemove;
	private PathFinder pathFinder;
	private GUI gui;
	private long time;
	private long passedTime;
	
	public CarSimulator(BlockMap<Pos, BlockGroup> blockMap, GUI gui) {
		
		this.gui = gui;
		areaList = new ArrayList<>();
		pathFinder = new PathFinder(blockMap);
		carList = new ArrayList<>();
		carToRemove = new ArrayList<>();
		time = System.currentTimeMillis();
		
	}
	
	public void addCarArea(CarArea carArea) {
		
		areaList.add(carArea);
		
	}
	
	public void simulate() {
		
		passedTime = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		
		//for(Car car : carList) {
		for(Iterator<Car> carIter = carList.iterator(); carIter.hasNext();) {
			
			Car car = carIter.next();
			
			if( carToRemove.contains(car) ) {
				carIter.remove();
				carToRemove.remove(car);
			}
			else {
				car.drive(passedTime);
			}
			
		}
		
		for(CarArea area : areaList) {
			
			area.produce(passedTime);
			
		}
		
	}
	
	public void produceCar(CarArea source, CarArea dest) {
		
		List<Pos> carPath = pathFinder.toPath( pathFinder.shortestPath(source.pos()), dest.pos() );
		/*
		Integer[] recursePath = pathFinder.shortestPath(source.pos());
		for(int i = 0; i < recursePath.length; i++) {
			System.out.print(recursePath[i] + " ");
		}
		System.out.println();
		for(int i = 0; i < recursePath.length; i++) {
			System.out.print("( " + i + " : " + pathFinder.getIndexMap().get(i) + " )");
		}
		*/
		Car car = new Car(carPath, this, gui);
		carList.add(car);
		
	}
	
	public void toRemove(Car car) {
		carToRemove.add(car);
	}
	

}
