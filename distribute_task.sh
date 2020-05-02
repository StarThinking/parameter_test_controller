#!/bin/bash

if [ $# -ne 2 ]; then echo 'wrong [num] [file]'; exit -1; fi

num=$1
all_task_file=$2

rm x*
split -d -n l/$num $all_task_file
for i in $(seq 0 9); do mv x0$i x$i; done

for i in $(seq 0 $(( num - 1 )))
do
    scp x$i node-$i:~/parameter_test_controller/task.txt
done
rm x*
