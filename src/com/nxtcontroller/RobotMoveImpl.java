package com.nxtcontroller;

public class RobotMoveImpl implements RobotMove {

	private RobotController controller;

	enum Direction {
		RIGHT, LEFT, TOP, DOWN
	}

	Direction direction = Direction.RIGHT;

	public RobotMoveImpl(RobotController controller) {
		this.controller = controller;
	}

	@Override
	public void incX() {
		switch (direction) {
		case RIGHT:

			break;

		case LEFT:
			controller.right();
			controller.right();
			break;
		case TOP:
			controller.right();
			break;
		case DOWN:
			controller.left();
			break;
		default:
			break;
		}

		controller.ahead();
		direction = Direction.RIGHT;

	}

	@Override
	public void decX() {
		switch (direction) {
		case RIGHT:
			controller.left();
			controller.left();
			break;

		case LEFT:
			break;
		case TOP:
			controller.left();
			break;
		case DOWN:
			controller.right();
			break;
		default:
			break;
		}

		controller.ahead();
		direction = Direction.LEFT;
	}

	@Override
	public void incY() {
		switch (direction) {
		case RIGHT:
			controller.left();
			break;

		case LEFT:
			controller.right();
			break;

		case TOP:
			break;

		case DOWN:
			controller.right();
			controller.right();
			break;
		default:
			break;
		}

		controller.ahead();
		direction = Direction.TOP;
	}

	@Override
	public void decY() {
		switch (direction) {
		case RIGHT:
			controller.right();
			break;

		case LEFT:
			controller.left();
			break;

		case TOP:
			controller.right();
			controller.right();
			break;

		case DOWN:
			break;
		default:
			break;
		}

		controller.ahead();
		direction = Direction.DOWN;
	}

}
