#!/bin/bash

if [ $# -lt 2 ]; then
    echo 'wrong arguments'
    exit 1
fi

component=$1
v1=$2
v2=$3
if [ $# -eq 3 ]; then
    echo wrong
    exit -1
fi

echo "component = $component, v1 = $v1, v2 = $v2"
paras=( $(cat input.txt) )
para_num=${#paras[@]}
echo "para_num = $para_num"
for i in $(seq 0 $(( para_num - 1 )))
do
    echo "${paras[$i]} on hadoop-$i"
    ssh hadoop-$i "~/parameter_test_controller/run_vm_task.sh ${paras[$i]} $component $v1 $v2 > /dev/null &" & 
done

sleep 10
echo 'jobs distributed'
