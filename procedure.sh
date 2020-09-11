#!/bin/bash

if [ $# -lt 3 ]; then
    echo "./procedure.sh [proj] [u_test] [[para,component,point,v1,v2] [...]]"
    exit -1
fi

# disable conf tracking
echo 'true' > ~/reconf_test_gen/lib/enable

# check if test is in white list
#if [ "$(grep ^"$parameter $component"$ ~/parameter_test_controller/white_list.txt)" != "" ]; then
#    echo "found in white list as $parameter $component, skip this test"
#    exit 0
#fi
proj=$1
u_test=$2
shift 2

#echo $@ | tr " " "\n" | while read line; do
#echo $line;
#done
#exit 0

java -cp /root/parameter_test_controller/target/ HConfRunner $proj $u_test $@ > /root/parameter_test_controller/target/"$proj.$u_test.$(($(date +%s%N)/1000000)).$RANDOM.combine_run.txt"
tester_rc=$?

echo "tester_rc is $tester_rc"

if [ $tester_rc -eq 0 ]; then
    exit 0;
fi

#repeat_times=50
#java -cp /root/parameter_test_controller/target/ Hypothesis "$repeat_times" "$parameter" "$component" "$v1" "$v2" "$testProject" "$unitTest" "$reconfPoint"
