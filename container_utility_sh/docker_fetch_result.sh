#!/bin/bash

if [ $# -ne 2 ]; then echo wrong: num key; exit -1; fi

num=$1
key=$2

host=$(cat /proc/sys/kernel/hostname | awk -F '.' '{print $1}')
function fetch {
    d=$1
    files=$(docker exec hadoop-$d bash -c "ls /root/parameter_test_controller/target | grep -F $key")
    for f in ${files[@]}; do prefix=$(echo $f | awk -F '.txt' '{print $1}'); docker cp hadoop-$d:/root/parameter_test_controller/target/$f /root/parameter_test_controller/target/"$prefix"."$host"."$d".txt; done
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
