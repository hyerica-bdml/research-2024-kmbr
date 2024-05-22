mvn clean compile

for k in 10 20 30
do
  for num_of_points in 50000
  do
    export K=$k
    export NUM_OF_POINTS=$num_of_points
    mvn exec:java -D exec.mainClass=edu.hanyang.kmbr.TimeTestApp
  done
done
