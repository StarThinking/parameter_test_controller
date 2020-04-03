#!/bin/bash

if [ $# -lt 3 ]; then
    echo 'wrong arguments: parameter_type parameter component'
    exit 1
fi

parameter_type=$1 # int, long
parameter=$2
component=$3
component_dir=/root/parameter_test_controller/tests/hdfs/accumulate/$component
start_path=$component_dir/start.txt
reconf_files=( $(ls $component_dir | grep -v start.txt) )

mapping_dir=/root/parameter_test_controller/run_entry_generator/hdfs/mapping/$parameter_type
testset_for_parameter=$mapping_dir/$parameter

value_store=/root/parameter_test_controller/parameters/hdfs/makeup_value/"$parameter_type"_values.txt 
function get_values {
    p=$1
    if [ $(grep ^"$p"' ' $value_store | wc -l) -ne 1 ]; then echo 'wrong!'; grep ^"$p"' ' $value_store; exit -1; fi
    ret_values=( $(grep ^"$p"' ' $value_store | awk -F ' ' '{print $2" "$3" "$4}') )
}

ret_values=()
get_values $parameter
#echo "values=${ret_values[@]}"
v0=${ret_values[0]}
v1=${ret_values[1]}
v2=${ret_values[2]}
IFS=$'\n' 
value_pairs[0]="$v1 $v0"
value_pairs[1]="$v1 $v2"
value_pairs[2]="$v0 $v1"
value_pairs[3]="$v2 $v1"

for value_pair in ${value_pairs[@]}
do
    for t in $(cat $start_path)
    do
        if [ "$(grep $t $testset_for_parameter)" != "" ]; then
            echo $parameter $component $value_pair -1 $t
        fi
    done

    for restart_file in ${reconf_files[@]}
    do
        restart_path=$component_dir/$restart_file
        rp=$(echo $restart_file | awk -F '.txt' '{print $1}')
        for t in $(cat $restart_path)
        do
            if [ "$(grep $t $testset_for_parameter)" != "" ]; then
                echo $parameter $component $value_pair $rp $t
            fi
        done
    done
done
