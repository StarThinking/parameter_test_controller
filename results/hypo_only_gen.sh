#!/bin/bash

#if [ $# -ne 1 ]; then echo "[repeat_times]"; exit -1; fi
repeat_times=10
parallel=10
IFS=$'\n'

while read -r line
do
    for i in $(seq 1 $parallel)
    do
	echo "$line $repeat_times"  
    done
done
