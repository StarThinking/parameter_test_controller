#!/bin/bash

if [ $# -ne 1 ]; then
    echo 'wrong arguments'
    exit 1
fi
component=$1
paras=( $(cat input.txt) )
para_num=${#paras[@]}
echo "para_num = $para_num"
for i in $(seq 0 $(( para_num - 1 )))
do
    echo "${paras[$i]} on hadoop-$i"
    ssh hadoop-$i "cd parameter_test_controller; java Controller ${paras[$i]} $component > /dev/null &" & 
done

sleep 10
echo 'jobs distributed'
