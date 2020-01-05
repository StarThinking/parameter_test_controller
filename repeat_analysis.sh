#!/bin/bash

if [ $# -lt 1 ]; then
    echo 'wrong arguments'
    exit 1
fi

dir=$1
option=$2
threshold=0
cd $dir
repeat_times=$(ls | wc -l)

num_of_reconf_issue=0
num_of_restart_reconf_issue=0
num_of_start_reconf_issue=0

tests=( $(cat * | grep testName | awk -F ':| ' '{print $3}' | sort -u) )
index=0
# tests_stat, indexes
for t in ${tests[@]}
do
    tests_stat[$index]=0
    indexes[$index]=$t
    index=$(( index + 1 ))
done

function indexOf() {
    test=$1
    found=-1
    for i in $(seq 0 $(( ${#indexes[@]} - 1 )))
    do
        if [ "$test" == "${indexes[$i]}" ];then
	    found=$i
	fi
    done
    echo "$found"
}

for repeat in *
do
    failed=0
    if [ "$(cat $repeat | grep 'componentHasStopped: 0')" != "" ]; then
        num_of_restart_reconf_issue=$(( num_of_restart_reconf_issue + 1 ))
	failed=1
    fi
    if [ "$(cat $repeat | grep 'componentHasStopped: 1')" != "" ]; then
        num_of_start_reconf_issue=$(( num_of_start_reconf_issue + 1 ))
	failed=1
    fi
    if [ $failed -eq 1 ]; then
	num_of_reconf_issue=$(( num_of_reconf_issue + 1 ))
    fi

    for t in ${tests[@]}
    do
	# test name includes specific characters like '[]'
	if [ "$(cat $repeat | grep -F $t)" != "" ]; then
            index=$(indexOf $t)
	    v=${tests_stat[$index]}
            tests_stat[$(indexOf $t)]=$(( v + 1 ))
        fi
    done
done

echo "repeat task: $dir"
echo "repeat_times: $repeat_times"
echo "num_of_tests: ${#tests[@]}"
#for t in ${tests[@]}; do echo $t; done
for i in $(seq 0 $(( ${#indexes[@]} - 1 )))
do
    if [ $option -eq 1 ]; then
	ratio=$(echo "${tests_stat[$i]} * 100 / $repeat_times" | bc)
	if [ $ratio -ge $threshold ]; then 
            echo "${tests_stat[$i]} ${indexes[$i]} "$ratio"%"
	fi
    else
        echo "${tests_stat[$i]} ${indexes[$i]}"
    fi
done
echo "num_of_reconf_issue: $num_of_reconf_issue"
echo "num_of_restart_reconf_issue: $num_of_restart_reconf_issue"
echo "num_of_start_reconf_issue: $num_of_start_reconf_issue"
echo ""
