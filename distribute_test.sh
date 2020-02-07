#!/bin/bash

if [ $# -lt 2 ]; then
    echo 'wrong arguments'
    exit 1
fi

paraType=$1
component=$2
option=""
if [ $# -eq 3 ]; then
    option=$3
fi

echo "paraType = $paraType component = $component"
paras=( $(cat input.txt) )
para_num=${#paras[@]}
echo "para_num = $para_num"
for i in $(seq 0 $(( para_num - 1 )))
do
    echo "${paras[$i]} on hadoop-$i"
    ssh hadoop-$i "cd parameter_test_controller; java Controller $paraType ${paras[$i]} $component $option > /dev/null &" & 
done

sleep 10
echo 'jobs distributed'
