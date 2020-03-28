#!/bin/bash

if [ $# -ne 1 ]; then echo wrong: num; exit -1; fi
num=$1

killall -9 dispatcher_hypo.sh
pkill dispatcher_hypo.sh
killall -9 dispatcher.sh
pkill dispatcher.sh

for i in $(seq 0 $num)
do 
    echo $i
    docker exec hadoop-$i bash -c "pkill -9 java; cd /root/parameter_test_controller; rm *.txt; git clean -df; git checkout -- *; git pull; javac *.java" &
    pids[$i]=$!
done

for i in $(seq 0 $num)
do
    wait ${pids[$i]}
done
echo 'all containers are cleaned and updated'
