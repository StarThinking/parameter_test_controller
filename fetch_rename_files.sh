#!/bin/bash

for i in $(seq 0 9)
do 
    issue_file=$(ssh hadoop-$i "ls ~/parameter_test_controller | grep -E '_issue_|_run_' | grep txt")
    #echo $issue_file
    if [ "$issue_file" != "" ]; then
	for f in $issue_file
	do 
	    #echo $f on hadoop-$i
            scp hadoop-$i:~/parameter_test_controller/$f ./"$f".$i
	done
    else
	echo "no issue file on hadoop-$i"
    fi
done
