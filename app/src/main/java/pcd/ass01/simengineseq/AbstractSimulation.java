package pcd.ass01.simengineseq;

import java.util.ArrayList;
import java.util.List;

import pcd.ass01.framework.Master;
import pcd.ass01.framework.MasterImpl;

/**
 * Base class for defining concrete simulations
 * 
 */
public abstract class AbstractSimulation {

	/* environment of the simulation */
	private AbstractEnvironment env;

	/* list of the agents */
	private List<AbstractAgent> agents;

	/* simulation listeners */
	private List<SimulationListener> listeners;

	/* logical time step */
	private int dt;

	/* initial logical time */
	private int t0;

	/* in the case of sync with wall-time */
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;

	/* for time statistics */
	private long currentWallTime;
	private long startWallTime;
	private long endWallTime;
	private long averageTimePerStep;
	private boolean isRunning = false;
	private int steps = 0;
	private int currentStep = 0;
	private boolean terminate = false;
	private Master master;
	private boolean isShut = false;

	protected AbstractSimulation() {
		agents = new ArrayList<AbstractAgent>();
		listeners = new ArrayList<SimulationListener>();
		toBeInSyncWithWallTime = false;
		this.master = new MasterImpl(20);
	}

	/**
	 * 
	 * Method used to configure the simulation, specifying env and agents
	 * 
	 */
	public abstract void setup();

	/**
	 * Method running the simulation for a number of steps,
	 * using a sequential approach
	 * 
	 * @param numSteps
	 */
	public void run() {

		startWallTime = System.currentTimeMillis();

		/* initialize the env and the agents inside */
		int t = t0;

		env.init();
		for (var a : agents) {
			a.init(env);
		}

		this.notifyReset(t, agents, env);

		while (true) {
			while (!isRunning || this.currentStep > this.steps) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			currentWallTime = System.currentTimeMillis();

			/* make a step */

			env.step(dt);
			for (var agent : agents) {
				agent.setDt(dt);
				this.master.submitTask(agent::step);
			}

			t += dt;

			notifyNewStep(t, agents, env);

			this.currentStep++;

			if (toBeInSyncWithWallTime) {
				syncWithWallTime();
			}
		}
	}

	public void run(int numSteps) {

		startWallTime = System.currentTimeMillis();

		/* initialize the env and the agents inside */
		int t = t0;

		env.init();
		for (var a : agents) {
			a.init(env);
		}

		this.notifyReset(t, agents, env);

		int nSteps = 0;
		long timePerStep = 0;

		while (nSteps < numSteps) {

			currentWallTime = System.currentTimeMillis();

			/* make a step */

			env.step(dt);
			for (var agent : agents) {
				agent.setDt(dt);
				master.submitTask(agent::step);
			}

			t += dt;

			notifyNewStep(t, agents, env);

			nSteps++;
			timePerStep += System.currentTimeMillis() - currentWallTime;

			if (toBeInSyncWithWallTime) {
				syncWithWallTime();
			}
		}

		endWallTime = System.currentTimeMillis();
		this.averageTimePerStep = timePerStep / nSteps;
		System.out.println("Average time per step: " + this.averageTimePerStep);
		this.master.shutdown();
	}

	public boolean isShut() {
		return this.isShut;
	}

	public long getSimulationDuration() {
		return endWallTime - startWallTime;
	}

	public long getAverageTimePerCycle() {
		return averageTimePerStep;
	}

	public void start(int steps) {
		isRunning = true;
		this.steps = steps;
		this.currentStep = 0;
	}

	public void shutdown() {
		endWallTime = System.currentTimeMillis();
		this.master.shutdown();
		System.out.println(Thread.activeCount());
	}

	public void stop() {
		isRunning = false;
	}

	public void reset() {
		isRunning = false;
		steps = 0;
		startWallTime = 0;
		endWallTime = 0;
		averageTimePerStep = 0;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void elaborateSteps(int steps) {
		isRunning = true;
		this.steps = steps;
		this.currentStep = 0;
	}

	/* methods for configuring the simulation */

	protected void setupTimings(int t0, int dt) {
		this.dt = dt;
		this.t0 = t0;
	}

	protected void syncWithTime(int nCyclesPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nCyclesPerSec;
	}

	protected void setupEnvironment(AbstractEnvironment env) {
		this.env = env;
	}

	protected void addAgent(AbstractAgent agent) {
		agents.add(agent);
	}

	/* methods for listeners */

	public void addSimulationListener(SimulationListener l) {
		this.listeners.add(l);
	}

	private void notifyReset(int t0, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l : listeners) {
			l.notifyInit(t0, agents, env);
		}
	}

	private void notifyNewStep(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		for (var l : listeners) {
			l.notifyStepDone(t, agents, env);
		}
	}

	/* method to sync with wall time at a specified step rate */

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {
		}
	}
}
