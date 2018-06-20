#!/bin/bash
vms=$1
totalmips=1500

((pa=($vms+1)*$vms/2))
mips=$totalmips/$pa

echo $pa
echo $vms
j=1
while [ $j -le $vms ]
do
	((cur=$j*$mips))
	echo ${cur}
	((j=$j+1))
done
