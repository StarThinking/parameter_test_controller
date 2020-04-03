#!/bin/bash

file=$1

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

#        echo "$v1v2_num $v1v2_failure $v1v1v2v2_num $v1v1v2v2_failure"

	v1v2_num_sum=$(( v1v2_num_sum + v1v2_num ))
	v1v2_failure_sum=$(( v1v2_failure_sum + v1v2_failure ))
	v1v1v2v2_num_sum=$(( v1v1v2v2_num_sum + v1v1v2v2_num ))
	v1v1v2v2_failure_sum=$(( v1v1v2v2_failure_sum + v1v1v2v2_failure ))	
    done
    echo "$v1v2_num_sum $v1v2_failure_sum $v1v1v2v2_num_sum $v1v1v2v2_failure_sum"
    echo ""
done
