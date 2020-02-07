#!/bin/bash

for i in $(seq 0 9)
do 
    issue_file=$(ssh hadoop-$i "ls ~/parameter_test_controller | grep _issue_")
    echo $issue_file
    if [ "$issue_file" != "" ]; then
        scp hadoop-$i:~/parameter_test_controller/$issue_file ./"$issue_file".$i
    else
	echo "no issue file on hadoop-$i"
    fi
done
