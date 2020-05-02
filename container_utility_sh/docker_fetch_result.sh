#!/bin/bash

if [ $# -ne 1 ]; then echo wrong: num; exit -1; fi

num=$1
key='_hypothesis_'

function fetch {
    d=$1
    files=$(docker exec hadoop-$d bash -c "ls /root/parameter_test_controller/target | grep $key")
    for f in ${files[@]}; do docker cp hadoop-$d:/root/parameter_test_controller/target/$f /root/parameter_test_controller/target/ ; done
}

for i in $(seq 0 $num)
do
    fetch $i &
    pids[$i]=$!
done

for i in $(seq 0 $num)
do
    wait ${pids[$i]}
done

echo "all files with key $key are fetched"
