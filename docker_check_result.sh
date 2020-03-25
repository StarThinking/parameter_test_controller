#!/bin/bash

function check {
    d=$1
    docker exec hadoop-$d bash -c 'cd ~/parameter_test_controller/ ; grep "full report"'
}

for i in $(seq 0 19)
do
    check $i 
    #pids[$i]=$!
done

#for i in $(seq 0 19)
#do
#    wait ${pids[$i]}
#done
