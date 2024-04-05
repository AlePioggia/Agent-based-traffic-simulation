package pcd.ass01.simtrafficexamples;

public class RunTrafficSimulationMassiveTest {

	public static void main(String[] args) {

		int numCars = 5_000;
		int nSteps = 100;

		var simulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(numCars);
		simulation.setup();

		log("Running the simulation: " + numCars + " cars, for " + nSteps + " steps ...");

		simulation.run(nSteps);

		long d = simulation.getSimulationDuration();
		log("Completed in " + d + " ms - average time per step: " + simulation.getAverageTimePerCycle() + " ms");

		int activeThreadCount = Thread.activeCount();
		System.out.println("Active threads: " + activeThreadCount);
	}

	private static void log(String msg) {
		System.out.println("[ SIMULATION ] " + msg);
	}
}
