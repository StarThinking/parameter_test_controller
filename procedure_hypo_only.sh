#!/bin/bash

if [ $# -ne 8 ]; then
    echo "./procedure.sh [repeat_times] [parameter] [component] [v1] [v2] [testProject] [unitTest] [reconfPoint]"
    exit -1
fi

parameter=$1
component=$2
v1=$3
v2=$4
testProject=$5
unitTest=$6
reconfPoint=$7
repeat_times=$8

java -cp /root/parameter_test_controller/target/ Hypothesis "$repeat_times" "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
