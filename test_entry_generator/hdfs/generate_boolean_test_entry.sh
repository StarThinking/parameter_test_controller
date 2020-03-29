#!/bin/bash

if [ $# -lt 2 ]; then
    echo 'wrong arguments'
    exit 1
fi

parameter_type=boolean
parameter=$1
component=$2
component_dir=/root/parameter_test_controller/tests/hdfs/accumulate/$component
mapping_dir=/root/parameter_test_controller/test_entry_generator/hdfs/mapping/$parameter_type
testset_for_parameter=$mapping_dir/$parameter
start_path=$component_dir/start.txt
reconf_files=( $(ls $component_dir | grep -v start.txt) )

for value_pair in 'true false' 'false true'
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
