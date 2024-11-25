mvn clean compile

for grid_size in 0.01 0.04 0.07 0.1 0.13 0.16
do
  for num_of_points in 10000 50000 100000 500000 1000000
  do
    export GRID_SIZE=$grid_size
    export NUM_OF_POINTS=$num_of_points
    mvn exec:java -D exec.mainClass=edu.hanyang.gridshift.GridShiftApp
  done
done
