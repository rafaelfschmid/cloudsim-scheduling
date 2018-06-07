i=$1
vms=$2
mips=$3

if [ "$i" -eq 0 ]; then
	javac -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -h . MinSortCloudSim.java
elif [ "$i" -eq 1 ]; then
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim mingpu $vms $mips
elif [ "$i" -eq 2 ]; then
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim mincpu $vms $mips
elif [ "$i" -eq 3 ]; then
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim maxcpu $vms $mips
else
	java -cp "/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3.jar:/home/rafael/workspace/cloudsim-3.0.3/jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. MinSortCloudSim fcfs $vms $mips
fi

 
