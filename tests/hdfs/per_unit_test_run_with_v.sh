#!/bin/bash

if [ $# -ne 4 ]; then echo 'wrong: unit_test vv_mode parameter v1'; exit -1; fi

unit_test=$1
vv_mode=$2
parameter=$3
v1=$4

log_name="$unit_test"%"$vv_mode"%"$parameter"%"$v1"
echo log_name=$log_name

echo "$vv_mode" > ~/parameter_test_controller/shared/reconf_vvmode
echo "$parameter" > ~/parameter_test_controller/shared/reconf_parameter
echo "$v1" > ~/parameter_test_controller/shared/v1
echo "0:" > ~/parameter_test_controller/shared/v2 # dummy, never use as we just use v1v1 vv_mode
echo "NameNode" > ~/parameter_test_controller/shared/reconf_component # dummy, ireelavent
echo "1" > ~/parameter_test_controller/shared/reconf_point # dummy, ireelavent

cd ~/hadoop-3.1.2-src/hadoop-hdfs-project
mvn test -Dtest=$unit_test > /dev/null
sleep 1

class_name=$(echo $unit_test | awk -F '#' '{print $1}')
test_log=$(find . -name "$class_name"-output.txt | grep target | grep surefire-reports)
if [ "$test_log" == "" ]; then 
    echo "Error: no output.txt for $unit_test !!"
else
    echo "test output log is $test_log"
    mv $test_log ~/parameter_test_controller/$log_name.txt
fi
