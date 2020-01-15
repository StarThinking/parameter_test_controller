#!/bin/bash

if [ $# -ne 4 ]; then
    echo 'wrong arguments: repeat_times, paraType, para, component'
    exit 1
fi

repeat_times=$1
paraType=$2
para=$3
component=$4

for t in $(seq 1 $repeat_times)
do
    echo "$t 'time test for $para $component" 
    java Controller $paraType $para $component > /dev/null
    sleep 10
done
