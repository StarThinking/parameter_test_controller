#!/bin/bash

if [ $# -ne 1 ]; then echo 'wrong [file]'; exit -1; fi

num=$(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n | wc -l)
all_task_file=$1

rm x*
split -d -n l/$num $all_task_file
for i in $(seq 0 9); do mv x0$i x$i; done

for i in $(seq 0 $(( num - 1 )))
do
    scp x$i node-$i:~/parameter_test_controller/task.txt
done
rm x*
