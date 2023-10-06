# Monte Carlo Minimization Parallel Program

This program implements the Monte Carlo Minimization algorithm using parallel computation. It uses the Fork/Join framework to achieve parallelization and find the global minimum in a terrain grid.

## Prerequisites

- Java Development Kit (JDK) installed on your machine.

## How to Run

1. Open a command prompt or terminal.

2.1 Find the MonteCarlo folder
2.2 Navigate to the directory where the `MonteCarloMinimizationParallel.class`,'SearchParallel.class` and `TerrainArea.class` files are located.

3. Run the program using the following command:/uou can also make use of the Makefile by running make so it compiles and make run on linux. and inserting the argument the way it is shown below.

   ```bash
   java MonteCarloMinimizationParallel <rows> <columns> <xmin> <xmax> <ymin> <ymax> <searchDensity>
   #Include MonteCarloMini.MonteCarloMinimizationParallel if it cant locate the files from the directory they are in.
