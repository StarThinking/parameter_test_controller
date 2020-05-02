#!/bin/bash

if [ $# -ne 7 ]; then
    echo "./procedure.sh [parameter] [component] [v1] [v2] [testProject] [unitTest] [reconfPoint]"
    exit -1
fi

parameter=$1
component=$2
v1=$3
v2=$4
testProject=$5
unitTest=$6
reconfPoint=$7

java -cp /root/parameter_test_controller/target/ ReconfTester "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
tester_rc=$?

echo "tester_rc is $tester_rc"

if [ $tester_rc -eq 0 ]; then
    exit 0;
fi

repeat_times=10
java -cp /root/parameter_test_controller/target/ Hypothesis "$repeat_times" "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
