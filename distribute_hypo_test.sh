#!/bin/bash

IFS=$'\n'
lines=( $(cat input.txt) )
length=${#lines[@]}
i=0
hadoop_index=0
while [ $i -lt $(( length - 1 )) ]
do
    line0=${lines[$i]}
    i=$(( i + 1 ))
    line1=${lines[$i]}
    i=$(( i + 1 ))
    line2=${lines[$i]}
    i=$(( i + 1 ))
    line3=${lines[$i]}
    i=$(( i + 1 ))
    
    echo "perform hypothesis tests on hadoop-$hadoop_index as follows:"
    echo "$line0"
    echo "$line1"
    echo "$line2"
    echo "$line3"
    #ssh hadoop-$hadoop_index "cd parameter_test_controller; java Hypothesis $line > /dev/null &" & 
    ssh hadoop-$hadoop_index "cd parameter_test_controller; java Hypothesis $line0 > /dev/null; sleep 60; java Hypothesis $line1 > /dev/null; sleep 60; java Hypothesis $line2 > /dev/null; sleep 60; java Hypothesis $line3 > /dev/null &" &
    hadoop_index=$(( hadoop_index + 1 ))
done

sleep 10
echo 'jobs distributed'
