mvn clean compile

for fixed_dirty_prob in 0.00 0.001 0.002 0.003 0.004 0.005 0.006 0.007 0.008 0.009 0.010
#for fixed_dirty_prob in 0.1 0.2 0.3 0.4
do
  export FIXED_DIRTY_PROB=$fixed_dirty_prob
  export K=20
  export NUM_OF_POINTS=50000
  mvn exec:java -Dexec.mainClass=edu.hanyang.kmbr.CacheTestApp > logs/cacheTest/cacheTest_$fixed_dirty_prob.log
done