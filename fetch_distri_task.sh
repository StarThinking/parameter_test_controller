#!/bin/bash

if [ $# -ne 3 ]; then echo 'wrong [src_ip_path] [file] [task_dir]'; exit -1; fi

pm=( $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n) )
num=${#pm[@]}
src_ip_path=$1
all_task_file=$2
task_dir=$3

scp $src_ip_path $all_task_file

rm x*
split -d -n l/$num $all_task_file
for i in $(seq 0 9); do mv x0$i x$i; done

index=0
for p in ${pm[@]}
do
    echo "scp x $index to node $p"
    scp x$index node-$p:"$task_dir"/task.txt
    index=$(( index + 1 ))
done
rm x*
