#!/bin/bash
input=$1 #input files dir
output=$2 #output files dir

for m in 2 4 8 16 32 64 ; do
	((t = $m * 32))

	echo ${m}x${t}

	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim minmin ${input}/machines/${m}.in ${input}/tasks/${t}_0.in all > ${output}/minmin_${m}x${t}.out
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim maxmin ${input}/machines/${m}.in ${input}/tasks/${t}_0.in all > ${output}/maxmin_${m}x${t}.out
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim fcfs ${input}/machines/${m}.in ${input}/tasks/${t}_0.in all > ${output}/fcfs_${m}x${t}.out

done

 
