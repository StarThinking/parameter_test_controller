#!/bin/bash

if [ $# -ne 7 ]; then
    echo "./procedure.sh [parameter] [testProject] [unitTest] [component] [reconfPoint] [v1] [v2]"
    exit -1
fi

# disable conf tracking
echo 'false' > ~/reconf_test_gen/lib/enable

parameter=$1
component=$4
v1=$6
v2=$7
testProject=$2
unitTest=$3
reconfPoint=$5

java -cp /root/parameter_test_controller/target/ ReconfTester "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
tester_rc=$?

echo "tester_rc is $tester_rc"

if [ $tester_rc -eq 0 ]; then
    exit 0;
fi

repeat_times=50
java -cp /root/parameter_test_controller/target/ Hypothesis "$repeat_times" "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
