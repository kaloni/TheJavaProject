import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import processing.core.PVector;


public class CarSimulator {

	BlockMap<Pos, BuildingBlock> blockMap; 
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
		this.blockMap = pathFinder.mapTypeConverter(blockMap);
		carList = new ArrayList<>();
		carToRemove = new ArrayList<>();
		time = System.currentTimeMillis();
		
	}
	
	public boolean hasPath(Pos from, Pos to) {
		return pathFinder.hasPath(from, to);
	}
	
	// checks if two floats have the same sign
	public boolean sameSign(float x, float y) {
		
		if( x < 0 ) {
			return y < 0;
		}
		else if( x > 0 ) {
			return y > 0;
		}
		else if( x == 0f ) {
			return y == 0f;
		}
		
		return false;
		
	}
	
	float directionThreshold = 0.1f;
	public boolean parallell(PVector dir1, PVector dir2) {
		
		float deltaX;
		float deltaY;
		
		if( dir2.x != 0 ) {
			deltaX = dir1.x/dir2.x;
		}
		else if( dir1.x == 0 ) {
			deltaX = 0;
		}
		else {
			return false;
		}
		
		if( dir2.y != 0 ) {
			deltaY= dir1.y/dir2.y;
		}
		else if( dir1.y == 0 ) {
			deltaY = 0;
		}
		else {
			return false;
		}
		
		return Math.abs(deltaX - deltaY) < directionThreshold;
		
	}
	// checks if a PVector is "in front" of another, according to the direction of the first
	public boolean hasInfrontOf(PVector myPos, PVector myDir, PVector infrontPos) {
	
		if( myDir.x != 0 && myDir.y != 0 ) {
			//System.out.println(parallell(PVector.sub(infrontPos, myPos), myDir));
			return sameSign(infrontPos.x - myPos.x, myDir.x) && sameSign(infrontPos.y - myPos.y, myDir.y)
					&& parallell(PVector.sub(infrontPos, myPos), myDir);
				//	&& sameSign( (infrontPos.x - myPos.x)/(infrontPos.y - myPos.y), myDir.x/myDir.y );
		}
		else if( myDir.x == 0 ) {
			return sameSign(infrontPos.y - myPos.y, myDir.y) && (infrontPos.x - myPos.x == 0f);
		}
		else if( myDir.y == 0) {
			return sameSign(infrontPos.x - myPos.x, myDir.x) && (infrontPos.y - myPos.y == 0f);
		}
		return false;
	}
	
	private <IN, OUT> OUT checkRule(TrafficRule<IN, OUT> op, IN... args) {
		return op.operation(args);
	}
	
	private interface TrafficRule<IN, OUT> {
		OUT operation(IN... args);
	}
	
	// args[0] = this pos
	// args[1] = pos dir
	// args[2] = pos to check
	// this checks is a pos is in front or to the right (and in front)
	public static TrafficRule<Pos, Boolean> RIGHT_FIRST_RULE = args ->
		//args[0].add(args[1]).equals(args[2]);
		//||
		args[0].add(args[1]).add(Direction.posBend(args[1], Direction.RIGHT)).equals(args[2]) 
		&& args[1].equals(Direction.posBend(args[3], Direction.RIGHT));
		
		
	//public static TrafficRule<Pos, Boolean> LEFT_WAIT_RULE = args ->
	
		//args[0].add(args[1]).add(Direction.posBend(args[1], Direction.LEFT)).equals(args[2]) {
	
	
	// checks if it's ok to enter a block according to it's current allowed directions
	// args[0] is a possible direction in a block
	// args[1] is the car's possible direction
	public static TrafficRule<Pos, Boolean> REDLIGHT = args ->
		args[0].equals(args[1]);
		
	// checks wheter a car is allowed to enter (to) a block or not, coming with a direction dirPos to the block
	public boolean greenLight(Pos dir, Pos to) {
		
		BuildingBlock block = blockMap.get(to);
		Matrix<Boolean> inputMatrix = block.getCurrentInputPattern();
		inputMatrix.totalFlip();
		//System.out.println(inputMatrix);
		Pos matrixDir = dir.clone();
		matrixDir.translate(1,1);
		
		if( inputMatrix.get(matrixDir.y, matrixDir.x) ) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean carInfront(Car thisCar) {
		
		PVector currentFloatPos = thisCar.floatPos();
		Pos dirPos = thisCar.dir();
		PVector floatDir = new PVector(dirPos.x, dirPos.y);
		float minimumDistance = thisCar.minimumDistance();
		float checkDistance = thisCar.checkDistance();
		
		for(Car car : carList) {
			
			if( car != thisCar ) {
				
				PVector diffPos = PVector.sub(car.floatPos(), currentFloatPos);
				 
				// check if cars around according to traffic rules
				if( diffPos.mag() < checkDistance ) {
					
					// TODO : check traffic rule bug
					//System.out.println(diffPos.mag());
					if( checkRule(RIGHT_FIRST_RULE, thisCar.pos(), dirPos, car.pos(), car.dir()) ) {
						return true;
					}
					// if car infront to close
					else if( diffPos.mag() < minimumDistance ) {
						return hasInfrontOf(currentFloatPos, floatDir, car.floatPos());
					}
					//return  hasInfrontOf(currentFloatPos, floatDir, car.floatPos()) || checkRule(RIGHT_FIRST_RULE, thisCar.pos(), dirPos, car.pos(), car.dir());
					/*
					if( checkRule(RIGHT_FIRST_RULE, thisCar.pos(), dirPos, car.pos(), car.dir()) ) {
						return hasInfrontOf(currentFloatPos, floatDir, car.floatPos());
					}
					*/
				}
			
			}
			
		}
		
		return false;
		
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
		
		//List<Pos> carPath = pathFinder.toPath( pathFinder.shortestPath(source.pos()), dest.pos() );
		List<Pos> carPath = null;
		List<List<Pos>> carPathList = new ArrayList<>();
		Pos sourcePos = source.pos();
		BuildingBlock sourceBlock = blockMap.get(sourcePos);
		// create List of possible paths to check which is the shortest
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				
				Pos neighborPos = source.pos().add(new Pos(i,j));
				// if conneced
				if( blockMap.get(neighborPos) != null ) {
					if( sourceBlock.checkConnect(neighborPos.sub(sourcePos), blockMap.get(neighborPos)) ) {
						
						carPath = pathFinder.getPath(source.pos().add(new Pos(i,j)), dest.pos());
						if( carPath != null ) {
							carPathList.add(carPath);
						}
					}
				}
			}
		}
		
		carPath = pathFinder.minPath(carPathList);
		//System.out.println(carPath);
		
		Car car = new Car(carPath, this, gui);
		
		boolean areaJammed = false;
		for(Car otherCar : carList) {
			
			if( otherCar.pos().equals(carPath.get(0)) ) {
				
				if( PVector.dist(otherCar.floatPos(), car.floatPos()) < car.minimumDistance() ) {
				
					areaJammed = true;
					break;
				
				}
			}
			
		}
		if( ! areaJammed ) {
			carList.add(car);
		}
		
	}
	
	public void toRemove(Car car) {
		carToRemove.add(car);
	}
	

}
