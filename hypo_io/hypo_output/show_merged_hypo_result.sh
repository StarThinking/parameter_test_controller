#!/bin/bash

file=$1
libs=/root/parameter_test_controller/hypo_io/hypo_output:/root/parameter_test_controller/hypo_io/hypo_output/commons-math3-3.6.1/commons-math3-3.6.1.jar

entries=( $(grep '_hypothesis_' $file | awk -F '_hypothesis_' '{print $1}' | sort -u) )

for entry in ${entries[@]}
do
    echo "entry: $entry"
    entry_lines=( $(grep -rn $entry $file | awk -F ':' '{print $1}') )
    v1v2_num_sum=0
    v1v2_failure_sum=0
    v1v1v2v2_num_sum=0
    v1v1v2v2_failure_sum=0
    for el in ${entry_lines[@]}
    do
        v1v2=$(awk -v line=$(( el+ 1 )) 'NR==line' $file)
        v1v2_num=$(echo $v1v2 | awk -F ' ' '{print $8}')
        v1v2_failure=$(echo $v1v2 | awk -F ' ' '{print $5}')
        
	v1v1v2v2=$(awk -v line=$(( el + 2 )) 'NR==line' $file)
        v1v1v2v2_num=$(echo $v1v1v2v2 | awk -F ' ' '{print $8}')
        v1v1v2v2_failure=$(echo $v1v1v2v2 | awk -F ' ' '{print $5}')

	null_hypo=$(java -cp $libs ReconfTtest $v1v2_num $v1v2_failure $v1v1v2v2_num $v1v1v2v2_failure)
        echo "$v1v2_num $v1v2_failure $v1v1v2v2_num $v1v1v2v2_failure $null_hypo"

	v1v2_num_sum=$(( v1v2_num_sum + v1v2_num ))
	v1v2_failure_sum=$(( v1v2_failure_sum + v1v2_failure ))
	v1v1v2v2_num_sum=$(( v1v1v2v2_num_sum + v1v1v2v2_num ))
	v1v1v2v2_failure_sum=$(( v1v1v2v2_failure_sum + v1v1v2v2_failure ))	
    done
    null_hypo=$(java -cp $libs ReconfTtest $v1v2_num_sum $v1v2_failure_sum $v1v1v2v2_num_sum $v1v1v2v2_failure_sum)
    echo "$v1v2_num_sum $v1v2_failure_sum $v1v1v2v2_num_sum $v1v1v2v2_failure_sum $null_hypo"
    echo ""
done
