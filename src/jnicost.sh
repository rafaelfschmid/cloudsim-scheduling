i=$1
vms=$2
mips=$3
print=$4

if [ "$i" -eq 0 ]; then
	javac -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -h . CloudSimCost.java
elif [ "$i" -eq 1 ]; then
	java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost minmin $vms $mips $print
elif [ "$i" -eq 2 ]; then
	java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost mincpu $vms $mips $print
elif [ "$i" -eq 3 ]; then
	java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost maxmin $vms $mips $print
elif [ "$i" -eq 4 ]; then
	java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost maxcpu $vms $mips $print
else
	java -cp "../jars/cloudsim-3.0.3.jar:../jars/cloudsim-3.0.3-sources.jar:." -Djava.library.path=. CloudSimCost fcfs $vms $mips $print
fi

 
