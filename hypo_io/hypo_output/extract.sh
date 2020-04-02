#!/bin/bash

libs=/root/parameter_test_controller/hypo_io/hypo_output:/root/parameter_test_controller/hypo_io/hypo_output/commons-math3-3.6.1/commons-math3-3.6.1.jar
if [ $# -ne 1 ]; then echo "wrong"; exit -1; fi

f=$1
tuple=$(echo $f | awk -F '_hypothesis_' '{print $1}')

v1v2_num=$(cat $f | tail -n 4 | head -n 1 | awk -F ' ' '{print $8}')
v1v2_failure=$(cat $f | tail -n 4 | head -n 1 | awk -F ' ' '{print $5}')
v1v1v2v2_num=$(cat $f | tail -n 3 | head -n 1 | awk -F ' ' '{print $8}')
v1v1v2v2_failure=$(cat $f | tail -n 3 | head -n 1 | awk -F ' ' '{print $5}')

if [ $v1v1v2v2_failure -gt $v1v2_failure ]; then exit 0; fi

hypo_ret=$(java -cp $libs ReconfTtest $v1v2_num $v1v2_failure $v1v1v2v2_num $v1v1v2v2_failure)
if [ "$hypo_ret" == 'null_hypothesis is false' ]; then
    echo "$tuple $v1v2_num $v1v2_failure $v1v1v2v2_num $v1v1v2v2_failure"
fi
