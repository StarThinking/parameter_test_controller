#!/bin/bash

if [ $# -ne 1 ]; then echo 'wrong: unit_test_class'; exit -1; fi

unit_test_class=$1

log_name="$unit_test_class"
echo log_name=$log_name

echo "none" > ~/parameter_test_controller/shared/reconf_vvmode

cd ~/hadoop-3.1.2-src/hadoop-hdfs-project
mvn test -Dtest=$unit_test_class > /dev/null
sleep 1

test_log=$(find . -name "$unit_test_class"-output.txt | grep target | grep surefire-reports)
if [ "$test_log" == "" ]; then 
    echo "Error: no output.txt for $unit_test_class !!"
else
    echo "test output log is $test_log"
    mv $test_log ~/parameter_test_controller/$log_name.txt
fi
