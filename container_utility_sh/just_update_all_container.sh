#!/bin/bash

if [ $# -ne 1 ]; then echo wrong: num; exit -1; fi
num=$1

for i in $(seq 0 $num)
do 
    echo $i
    docker exec hadoop-$i bash -c "cd /root/parameter_test_controller; git pull; cd /root/reconf_test_gen; git pull; git checkout -- compress.sh" &
    pids[$i]=$!
done

for i in $(seq 0 $num)
do
    wait ${pids[$i]}
done
echo 'all containers are updated'
