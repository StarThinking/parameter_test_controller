#!/bin/bash

if [ $# -ne 1 ]; then echo 'wrong'; exit -1; fi

IFS=$'\n' 
REPEAT=$1
entry_list=( $(cat task.txt) )
entry_list_length=${#entry_list[@]}
entry_cursor=0
echo entry_list_length = $entry_list_length

while [ $entry_cursor -lt $entry_list_length ]
do
    for i in $(seq 0 19)
    do
        jps_num=$(docker exec hadoop-$i bash -c "jps" | wc -l)
        if [ $jps_num -gt 1 ]; then
	     echo hadoop-$i is busy
	else
	    docker exec -d hadoop-$i bash -c "cd ~/parameter_test_controller; java Hypothesis $REPEAT ${entry_list[$entry_cursor]}"
	    echo hadoop-$i is idle, assign entry $entry_cursor to it
	    entry_cursor=$(( entry_cursor + 1 ))
	    if [ $entry_cursor -ge $entry_list_length ]; then echo finish all tasks; break; fi
	fi
    done
    sleep 60
done
