#!/bin/bash

if [ $# -ne 5 ]; then echo 'wrong'; exit -1; fi

unit_test=$1
vv_mode=$2
parameter=$3
v1=$4
v2=$5

log_name="$unit_test"%"$vv_mode"%"$parameter"%"$v1"%"$v2"
echo log_name=$log_name

echo "$vv_mode" > ~/parameter_test_controller/shared/reconf_vvmode
echo "$parameter" > ~/parameter_test_controller/shared/reconf_parameter
echo "$v1" > ~/parameter_test_controller/shared/v1
echo "$v2" > ~/parameter_test_controller/shared/v2

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
