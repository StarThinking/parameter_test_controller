#!/bin/bash

IFS=$'\n'
lines=( $(cat input.txt) )
length=${#lines[@]}
i=0
hadoop_index=0
while [ $i -lt $length ]
do
    line0=${lines[$i]}
    i=$(( i + 1 ))
    
    echo "perform hypothesis tests on hadoop-$hadoop_index as follows:"
    echo "$line0"
    #ssh hadoop-$hadoop_index "cd parameter_test_controller; java Hypothesis $line > /dev/null &" & 
    ssh hadoop-$hadoop_index "cd parameter_test_controller; java Hypothesis $line0 > /dev/null &" &
    hadoop_index=$(( hadoop_index + 1 ))
done

sleep 10
echo 'jobs distributed'
