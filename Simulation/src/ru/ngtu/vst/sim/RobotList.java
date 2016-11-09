package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RobotList {
	// Change this parameter to select strategy of robot allocation:
	private static final boolean ASSIGNED_ROBOTS = true;

	private List<Robot> robotList = new ArrayList<Robot>();

	public RobotList() {
		for (int i = 0; i < 3; i++) {
			if (ASSIGNED_ROBOTS) {
				robotList.add(new Robot(i));
			} else {
				robotList.add(new Robot(0));
			}
		}
	}

	public List<Robot> getRobotList() {
		return this.robotList;
	}

	public Robot getNearestRobot(int position) {
		Robot result = null;
		if (ASSIGNED_ROBOTS) {
			Robot robot = this.robotList.get(position);
			if (!robot.isBusy()) {
				result = robot;
			}
		} else {
			List<Robot> freeRobots = new ArrayList<Robot>();
			for (Robot robot : this.robotList) {
				if (!robot.isBusy()) {
					freeRobots.add(robot);
				}
			}

			if (freeRobots.size() > 0) {
				result = freeRobots.get(0);
				for (Robot robot : freeRobots) {
					if (Math.abs(robot.getPosition() - position) < Math.abs(result.getPosition() - position)) {
						result = robot;
					}
				}
			}
		}

		return result;
	}

	public Robot getRobotWithTarget(int target) {
		Robot result = null;
		for (Robot robot : this.robotList) {
			if (robot.getTarget() == target) {
				result = robot;
				break;
			}
		}
		return result;
	}

	public Queue<Detail> getNearestQueue(int robotPosition, List<Queue<Detail>> queueList) {
		Queue<Detail> result = null;
		if (ASSIGNED_ROBOTS) {
			Queue<Detail> queue = queueList.get((robotPosition - 1) * 2);
			if (queue.size() > 0) {
				result = queue;
			}
		} else {
			List<Integer> queuePositions = new ArrayList<Integer>();
			for (int i = 0; i < queueList.size(); i += 2) {
				Queue<Detail> queue = queueList.get(i);
				if (queue.size() > 0) {
					queuePositions.add(i / 2);
				}
			}

			if (queuePositions.size() > 0) {
				int nearestPosition = queuePositions.get(0);
				for (Integer position : queuePositions) {
					if (Math.abs(robotPosition - position) < Math.abs(robotPosition - nearestPosition)) {
						nearestPosition = position;
					}
				}

				result = queueList.get(nearestPosition * 2);
			}
		}

		return result;
	}
}
