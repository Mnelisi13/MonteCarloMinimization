package MonteCarloMini;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class MonteCarloMinimizationParallel {
	static final boolean DEBUG = false;
	static final int SEQUENTIAL_CUTOFF = 500;

	static long startTime = 0;
	static long endTime = 0;

	private static void tick() {
		startTime = System.currentTimeMillis();
	}

	private static void tock() {
		endTime = System.currentTimeMillis();
	}

	public static void main(String[] args) {

		int rows, columns; // grid size
		double xmin, xmax, ymin, ymax; // x and y terrain limits
		TerrainArea terrain; // object to store the heights and grid points visited by searches
		double searches_density; // Density - number of Monte Carlo searches per grid position - usually less than 1!

		int num_searches; // Number of searches
		SearchParallel[] searches; // Array of searches
		Random rand = new Random(); // the random number generator

		if (args.length != 7) {
			System.out.println("Incorrect number of command line arguments provided.");
			System.exit(0);
		}
		/* Read argument values */
		rows = Integer.parseInt(args[0]);
		columns = Integer.parseInt(args[1]);
		xmin = Double.parseDouble(args[2]);
		xmax = Double.parseDouble(args[3]);
		ymin = Double.parseDouble(args[4]);
		ymax = Double.parseDouble(args[5]);
		searches_density = Double.parseDouble(args[6]);

		if (DEBUG) {
			/* Print arguments */
			System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
			System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax);
			System.out.printf("Arguments, searches_density: %f\n", searches_density);
			System.out.printf("\n");
		}

		// Initialize
		terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
		num_searches = (int) (rows * columns * searches_density);
		searches = new SearchParallel[num_searches];
		for (int i = 0; i < num_searches; i++)
			searches[i] = new SearchParallel(i + 1, rand.nextInt(rows), rand.nextInt(columns), terrain);

		if (DEBUG) {
			/* Print initial values */
			System.out.printf("Number searches: %d\n", num_searches);
			// terrain.print_heights();
		}

		// Start timer
		tick();

		// Parallel computation using Fork/Join framework
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		int min = forkJoinPool.invoke(new SearchParallelTask(searches, 0, num_searches));

		// End timer
		tock();

		if (DEBUG) {
			/* print final state */
			terrain.print_heights();
			terrain.print_visited();
		}

		System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax);
		System.out.printf("\t Search density: %f (%d searches)\n", searches_density, num_searches);

		/* Total computation time */
		System.out.printf("Time: %d ms\n", endTime - startTime);
		int tmp = terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0, "%");
		tmp = terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0, "%");

		/* Results */
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min,
				terrain.getXcoord(searches[0].getPos_row()), terrain.getYcoord(searches[0].getPos_col()));

	}

	static class SearchParallelTask extends RecursiveTask<Integer> {
		private final SearchParallel[] searches;
		private final int start;
		private final int end;

		SearchParallelTask(SearchParallel[] searches, int start, int end) {
			this.searches = searches;
			this.start = start;
			this.end = end;
		}

		@Override
		protected Integer compute() {
			// If the range is small enough, solve sequentiall
			if (end - start < SEQUENTIAL_CUTOFF) {
				int min = Integer.MAX_VALUE;
				int localMin = Integer.MAX_VALUE;
				for (int i = start; i < end; i++) {
					localMin = searches[i].find_valleys();//Perform search!
					if (!searches[i].isStopped() && localMin < min) {
						min = localMin;//Update if new minimum is found!
					}
				}
				return min;
			} else {
				int median = (start + end) / 2;//Seperate the range into two tasks and solve them in parallel!
				SearchParallelTask left = new SearchParallelTask(searches, start, median);
				SearchParallelTask right = new SearchParallelTask(searches, median, end);
				left.fork();//Commence task on left asynchronously!
				int right_min = right.compute();//Find right half in this light process/thread!
				int left_min = left.join();//await for left's results!
				return Math.min(right_min, left_min);//combine results!
			}
		}
	}
}
