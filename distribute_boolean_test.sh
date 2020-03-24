#!/bin/bash

if [ $# -lt 2 ]; then
    echo 'wrong arguments'
    exit 1
fi

parameter=$1
component=$2
component_dir=/root/parameter_test_controller/reconfTester_static_data/hdfs/accumulate/$component
start_path=$component_dir/start.txt
reconf_files=( $(ls $component_dir | grep -v start.txt) )

for value_pair in 'true false' 'false true'
do
    for t in $(cat $start_path)
    do
        echo $parameter $component $value_pair -1 $t
    done

    for restart_file in ${reconf_files[@]}
    do
        restart_path=$component_dir/$restart_file
        rp=$(echo $restart_file | awk -F '.txt' '{print $1}')
        for t in $(cat $restart_path)
        do
            echo $parameter $component $value_pair $rp $t
        done
    done
done
