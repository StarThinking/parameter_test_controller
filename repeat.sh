#!/bin/bash

if [ $# -ne 3 ]; then
    echo 'wrong arguments'
    exit 1
fi

repeat_times=$1
para=$2
component=$3

for t in $(seq 1 $repeat_times)
do
    echo "$t 'time test for $para $component" 
    java Controller $para $component > /dev/null
    sleep 10
done
