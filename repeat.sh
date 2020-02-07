#!/bin/bash

if [ $# -lt 4 ]; then
    echo 'wrong arguments: repeat_times, paraType, para, component, option'
    exit 1
fi

repeat_times=$1
paraType=$2
para=$3
component=$4
option=""

if [ $# -lt 5 ]; then
    option=$5
fi

for t in $(seq 1 $repeat_times)
do
    echo "$t 'time test for $para $component $option" 
    java Controller $paraType $para $component $option> /dev/null
    sleep 10
done
