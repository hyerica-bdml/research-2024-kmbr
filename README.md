# Algorithm for computing k Minimum Bounding Rectangle



**Problem Definition**: Finding the smallest \(k\)-Minimum Bounding Rectangle (\(k\)-MBR) in the precense of moving object, given a spatial dataset

## Project structure
- `edu.hanyang.kmbr`
- `edu.hanyang.enclosing1998` [1]
- `edu.hanyang.smallest2019` [2]
- `edu.hanyang.voronoi` (not used)
- `edu.hanyang.voronoi1991` [3] (not used)

### `edu.hanyang.kmbr`
The package containing main codes of our idea.

### `edu.hanyang.enclosing1998` [1]
**Related work implementation**: Unofficial implementation of [1]

### `edu.hanyang.smallest2019` [2]
**Related work implementation**: Unofficial implementation of [2]

### edu.hanyang.voronoi
High-order voronoi diagram implementation

### edu.hanyang.voronoi1991 [3]
**Related work implementation**: Unofficial implementation of [3] (not used in paper)

## Execution
The source code contains several execution modes as follows:
- kMBR execution modes (ours)
  - Interactive mode
  - Simulation mode (visualization)
  - Speed test mode
  - Louisville scooter simulation mode
  - Covid-19 simulation mode
  - Citibike simulation mode
- Enclosing1998 execution modes
  - Louisville scooter simulation mode
  - Covid-19 simulation mode
  - Citibike simulation mode
- Smallest2019 execution modes
  - Louisville scooter simulation mode
  - Covid-19 simulation mode
  - Citibike simulation mode

### Common prerequisite
- Java 11 (higher than 8)
- Maven
- JUnit 4.13.2
- Intellij (optional)

### Execution
If you are using Intellij, follow instructions:
1. Right click a class file that contains `main` function. See below which file contains a main function for each mode.
2. Select 'Run XXX.main()'

If you are using maven, run the following command in a terminal:
```bash
mvn clean compile exec:java -Dexec.mainClass=edu.hanyang.kmbr.XXX
```
You can exclude "clean compile" if you already built the project. See below which file contains a main function for each mode. When run the command, please exclude extension `.java`.
For instance, you can run kMBR interactive mode as follows:
```bash
mvn clean compile exec:java -Dexec.mainClass=edu.hanyang.kmbr.KMBRApp
```

**Class files that contains main functions for each mode**
- kMBR (ours)
  - `src/main/java/edu/hanyang/kmbr/KMBRApp.java` (Interactive mode)
  - `src/main/java/edu/hanyang/kmbr/SimulationApp.java` (Simulation mode; visualization)
  - `src/main/java/edu/hanyang/kmbr/SpeedTestApp.java` (Speed test mode)
  - `src/main/java/edu/hanyang/kmbr/CityEscooterSimulationApp.java` (Louisville scooter simulation mode)
  - `src/main/java/edu/hanyang/kmbr/CovidSImulationApp.java` (Covid-19 simulation mode)
  - `src/main/java/edu/hanyang/kmbr/CitibikeSimulationApp.java` (Citibike simulation mode)
- Enclosing1998
  - `src/main/java/edu/hanyang/enclosing1998/CityEscooterSimulationApp.java` (Louisville scooter simulation mode)
  - `src/main/java/edu/hanyang/enclosing1998/CovidSImulationApp.java` (Covid-19 simulation mode)
  - `src/main/java/edu/hanyang/enclosing1998/CitibikeSimulationApp.java` (Citibike simulation mode)
- Smallest2019
  - `src/main/java/edu/hanyang/smallest2019/CityEscooterSimulationApp.java` (Louisville scooter simulation mode)
  - `src/main/java/edu/hanyang/smallest2019/CovidSImulationApp.java` (Covid-19 simulation mode)
  - `src/main/java/edu/hanyang/smallest2019/CitibikeSimulationApp.java` (Citibike simulation mode)


**Manual of Interactive mode:**
- `generate NUM_OF_POINTS NUM_OF_CLUSTERS`: Generate `NUM_OF_POINTS` points distributed across `NUM_OF_CLUSTERS` clusters **(Note: previously generated points will be removed.)**.
- `insert NUM_OF_POINTS`: Generate additional `NUM_OF_POINTS` points without removing previously generated points.
- `insert one X Y`: Generate one point with coordinate of `(X, Y)`.
- `move NUMBER_OF_MIN_MOVE NUMBER_OF_MAX_MOVE`: Randomly select points from `NUMBER_OF_MIN_MOVE` to `NUMBER_OF_MAX_MOVE`, and move.
- `compute`: Compute the smallest \(k\)-MBR, including update caching plan.
- `update`: Update caching plan. This command is already included in `compute` command. If you want to update caching plan only, use this command.
- `print tree`: Print binary tree structure.
- `print height`: Print height information of the tree.
- `print cache`: Print caching status, involving which node has been cached.
- `print cachebits`: Print caching plan (caching bits) for all nodes.
- `print dirty`: Print dirty probability for all leaf nodes.
- `clear`: Remove all data points.
- `exit`: Exit the process.

Example of Interactive mode:
```bash
INPUT> generate 10000 10 # generate 10,000 points distributed across 10 clusters.
INPUT> print tree # print tree status.
INPUT> insert 10000 # create 10,000 points, and insert them into the tree.
INPUT> print cache # print caching status.
INPUT> compute # compute without caching, as it is the first computation (no available previously computed cache).
INPUT> compute # compute with caching.
INPUT> clear # clear all data points.
INPUT> exit # exit.
```

## Reference
[1] Segal, Michael, and Klara Kedem. "Enclosing k points in the smallest axis parallel rectangle." Information Processing Letters 65.2 (1998): 95-99. <br/>
[2] Chan, Timothy M., and Sariel Har-Peled. "Smallest k-enclosing rectangle revisited." Discrete & Computational Geometry 66.2 (2021): 769-791. <br/>
[3] Aggarwal, Alok, et al. "Finding k points with minimum diameter and related problems." Journal of algorithms 12.1 (1991): 38-56.