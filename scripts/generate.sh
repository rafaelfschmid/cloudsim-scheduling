#!/bin/bash
dir=$1

t=64
while [ $t -le 2048 ]
do
	for j in `seq 0 9` ; do
		echo ${t}_${j}

		./equal.exe $t > $dir/$t"_"$j".in"
		sleep 1		

	done 
	echo " "
	((t=$t*2))
done
