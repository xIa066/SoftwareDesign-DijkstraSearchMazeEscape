package mycontroller;

import world.Car;
import world.WorldSpatial.Direction;

public class MoveCar {
	
	private static final float CAR_MAX_SPEED = 1;
	private Car car;
	
	public MoveCar(Car car) {
		
		this.car = car;
		
	}
	
	public void carMove(Direction direction) {
		switch (car.getOrientation()) {
		case EAST:
			eastTurn(direction);
			break;
		case WEST:
			westTurn(direction);
			break;
		case NORTH:
			northTurn(direction);
			break;
		case SOUTH:
			southTurn(direction);
			break;
		}
	}
	
	private void eastTurn(Direction direction) {
		switch (direction) {
		case EAST:
			car.applyForwardAcceleration();
			break;
		case WEST:
			car.applyReverseAcceleration();
			break;
		case NORTH:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnLeft();
			}
			break;
		case SOUTH:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnRight();
			}
			break;
		}
	}
	private void westTurn(Direction direction) {
		switch (direction) {
		case WEST:
			car.applyForwardAcceleration();
			break;
		case EAST:
			car.applyReverseAcceleration();
			break;
		case SOUTH:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnLeft();
			}
			break;
		case NORTH:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnRight();
			}
			break;
		}
	}
	private void northTurn(Direction direction) {
		switch (direction) {
		case NORTH:
			car.applyForwardAcceleration();
			break;
		case SOUTH:
			car.applyReverseAcceleration();
			break;
		case WEST:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnLeft();
			}
			break;
		case EAST:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnRight();
			}
			break;
		}
	}
	private void southTurn(Direction direction) {
		switch (direction) {
		case SOUTH:
			car.applyForwardAcceleration();
			break;
		case NORTH:
			car.applyReverseAcceleration();
			break;
		case EAST:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnLeft();
			}
			break;
		case WEST:
			if (car.getSpeed() == CAR_MAX_SPEED) {
				car.turnRight();
			}
			break;
		}
	}

	
}
