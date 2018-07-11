#!/bin/bash
input=$1 #input files dir

t=512

echo "minmin"
for m in 2 4 8 16 ; do
	echo ${m}x${t}
	for i in 0 1 2 3 4 5 6 7 8 9 ; do
		java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost minmin ${input}/machines2/${m}.in ${input}/tasks/${t}_${i}.in last 
	done
	echo ""
done

echo "maxmin"
for m in 2 4 8 16 ; do
	echo ${m}x${t}
	for i in 0 1 2 3 4 5 6 7 8 9 ; do 
		java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost maxmin ${input}/machines2/${m}.in ${input}/tasks/${t}_${i}.in last
	done
	echo ""
done

echo "fcfs"
for m in 2 4 8 16 ; do
	echo ${m}x${t}
	for i in 0 1 2 3 4 5 6 7 8 9 ; do
		java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost fcfs ${input}/machines2/${m}.in ${input}/tasks/${t}_${i}.in last
	done
	echo ""
done

 
