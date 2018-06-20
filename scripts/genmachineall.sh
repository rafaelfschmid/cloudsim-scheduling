#!/bin/bash
dir=$1

vms=2
totalmips=1500
while [ $vms -le 64 ]
do
	((pa=($vms+1)*$vms/2))
	((mips=$totalmips/$pa))	

	echo $vms
	j=1
	while [ $j -le $vms ]
		echo ${t}_${j}

		echo $t > $dir/$t"_"$j".in"
		sleep 1		

	done 
	echo " "
	((vms=$vms*2))
done
