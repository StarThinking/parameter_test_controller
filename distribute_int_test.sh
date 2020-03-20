#!/bin/bash

if [ $# -lt 3 ]; then
    echo 'wrong arguments'
    exit 1
fi

component=$1
v1_p=$2
v2_p=$3

if [ $v1_p -ge 3 ] || [ $v2_p -ge 3 ]; then echo 'wrong!'; exit -1; fi

value_store=~/parameter_test_controller/int_values.txt 

ret_values=()
function get_values {
    p=$1
    if [ $(grep "^$p " $value_store | wc -l) -ne 1 ]; then echo 'wrong!'; grep -wF "$p" $value_store; exit -1; fi
    ret_values=( $(grep -wF "$p" $value_store | awk -F ' ' '{print $2" "$3" "$4}') )
}

paras=( $(cat input.txt) )
para_num=${#paras[@]}
echo "para_num = $para_num"
for i in $(seq 0 $(( para_num - 1 )))
do
    echo "${paras[$i]} on hadoop-$i"
    ret_values=()
    get_values ${paras[$i]}
    echo "values=${ret_values[@]}"
    v1=${ret_values[$v1_p]}
    v2=${ret_values[$v2_p]}
    echo "component=$component v1=$v1 v2=$v2"
    ssh hadoop-$i "cd ~/parameter_test_controller; ./run_vm_task.sh ${paras[$i]} $component $v1 $v2 > /dev/null &" &
done

sleep 10
echo 'jobs distributed'
