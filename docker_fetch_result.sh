#!/bin/bash

function fetch {
    d=$1
    files=$(docker exec hadoop-$d bash -c 'ls ~/parameter_test_controller/ | grep dfs')
    for f in ${files[@]}; do docker cp hadoop-$d:/root/parameter_test_controller/$f .  ; done
}

for i in $(seq 0 19)
do
    fetch $i &
    pids[$i]=$!
done

for i in $(seq 0 19)
do
    wait ${pids[$i]}
done
