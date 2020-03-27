#!/bin/bash

file=~/parameter_test_controller//task.txt

for t in $(cat $file) 
do
    echo $t
    cd ~/hadoop-3.1.2-src/hadoop-hdfs-project
    mvn test -Dtest=$t > /dev/null
    sleep 1
    class_name=$(echo $t | awk -F '#' '{print $1}')
    test_log=$(find . -name "$class_name"-output.txt | grep target | grep surefire-reports)
    if [ "$test_log" == "" ]; then 
        echo "Error: no output.txt for $t !!"
    else
        echo "test output log is $test_log"
        mv $test_log ~/parameter_test_controller/"$t"-output.txt
    fi
done
