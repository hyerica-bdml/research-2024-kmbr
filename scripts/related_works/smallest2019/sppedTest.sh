mvn clean compile

#for k in 5 10 15 20 25 30
#do
#  for num_of_points in 10000 50000 100000 500000 1000000
#  do
#    export K=$k
#    export NUM_OF_POINTS=$num_of_points
#    mvn exec:java -D exec.mainClass=edu.hanyang.smallest2019.BaseSpeedTestApp
#  done
#done

for k in 5 10 15 20 25 30
do
  for num_of_points in 10000 50000 100000 500000 1000000
  do
    export K=$k
    export NUM_OF_POINTS=$num_of_points
    mvn exec:java -D exec.mainClass=edu.hanyang.smallest2019.SpeedTestApp
  done
done
