#!/bin/bash

if [ $# -ne 1 ]; then
    echo "./hypo_analysis.sh [hypo.txt]"
    exit -1
fi

hypo_file=$1

v1v2_failed=$(grep 'v1v2 failed with probability' $hypo_file | awk -F ' ' '{print $5}')
v1v2_num=$(grep 'v1v2 failed with probability' $hypo_file | awk -F ' ' '{print $8}')
v1v1v2v2_failed=$(grep 'v1v1v2v2 failed with probability' $hypo_file | awk -F ' ' '{print $5}')
v1v1v2v2_num=$(grep 'v1v1v2v2 failed with probability' $hypo_file | awk -F ' ' '{print $8}')

function print_data {
    echo "v1v2_num: $v1v2_num"
    echo "v1v2_failed: $v1v2_failed"
    echo "v1v1v2v2_num: $v1v1v2v2_num"
    echo "v1v1v2v2_failed: $v1v1v2v2_failed"
}

if [ $v1v1v2v2_failed -gt $v1v2_failed ]; then
    #echo 'v1v1v2v2 failed more';
    exit 0; 
fi

classpath=/root/parameter_test_controller/target/:/root/parameter_test_controller/src/lib/commons-math3-3.6.1.jar
output=$(java -cp $classpath HypoAnalysis $v1v2_num $v1v2_failed $v1v1v2v2_num $v1v1v2v2_failed)
if [ "$output" == "null_hypothesis is false" ]; then
    echo "$hypo_file"    
#    print_data
fi

