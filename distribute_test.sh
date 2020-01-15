#!/bin/bash

if [ $# -ne 2 ]; then
    echo 'wrong arguments'
    exit 1
fi
paraType=$1
component=$2
echo "paraType = $paraType component = $component"
paras=( $(cat input.txt) )
para_num=${#paras[@]}
echo "para_num = $para_num"
for i in $(seq 0 $(( para_num - 1 )))
do
    echo "${paras[$i]} on hadoop-$i"
    ssh hadoop-$i "cd parameter_test_controller; java Controller $paraType ${paras[$i]} $component > /dev/null &" & 
done

sleep 10
echo 'jobs distributed'
