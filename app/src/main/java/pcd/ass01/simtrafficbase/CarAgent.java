package pcd.ass01.simtrafficbase;

import java.util.Optional;

import pcd.ass01.simengineseq.*;

/**
 * 
 * Base class modeling the skeleton of an agent modeling a car in the traffic
 * environment
 * 
 */
public abstract class CarAgent extends AbstractAgent {

	/* car model */
	protected double maxSpeed;
	protected double currentSpeed;
	protected double acceleration;
	protected double deceleration;

	/* percept and action retrieved and submitted at each step */
	protected CarPercept currentPercept;
	protected Optional<Action> selectedAction;

	public CarAgent(String id, RoadsEnv env, Road road,
			double initialPos,
			double acc,
			double dec,
			double vmax) {
		super(id);
		this.acceleration = acc;
		this.deceleration = dec;
		this.maxSpeed = vmax;
		this.env = env;
		env.registerNewCar(this, road, initialPos);
	}

	/**
	 * 
	 * Basic behaviour of a car agent structured into a sense/decide/act structure
	 * 
	 */
	public void step() {
		AbstractEnvironment env = this.getEnv();
		var readWriteMonitor = env.getReadWriteMonitor();
		readWriteMonitor.requestRead();
		try {
			/* sense */
			currentPercept = (CarPercept) env.getCurrentPercepts(getObjectId());

		} finally {
			readWriteMonitor.releaseRead();
		}

		/* decide */

		selectedAction = Optional.empty();

		decide(dt);

		// /* act */
		readWriteMonitor.requestWrite();
		try {
			if (selectedAction.isPresent()) {
				env.doAction(getObjectId(), selectedAction.get());
			}
		} finally {
			readWriteMonitor.releaseWrite();
		}
	}

	/**
	 * 
	 * Base method to define the behaviour strategy of the car
	 * 
	 * @param dt
	 */
	protected abstract void decide(int dt);

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	protected void log(String msg) {
		System.out.println("[CAR " + this.getObjectId() + "] " + msg);
	}
}
