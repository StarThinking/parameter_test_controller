#!/bin/bash

if [ $# -ne 2 ]; then echo 'wrong [file] [task_dir]'; exit -1; fi

num=$(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n | wc -l)
all_task_file=$1
task_dir=$2

rm x*
split -d -n l/$num $all_task_file
for i in $(seq 0 9); do mv x0$i x$i; done

for i in $(seq 0 $(( num - 1 )))
do
    scp x$i node-$i:"$task_dir"/task.txt
done
rm x*
