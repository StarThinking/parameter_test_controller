#!/bin/bash
#set -x

if [ $# -ne 2 ]; then 
    echo 'wrong arguments: [per_vm_tasks] [repeats]'
    exit 1
fi

per_vm_tasks=$1
repeats=$2

echo "per_vm_tasks=$per_vm_tasks repeats=$repeats"

per_vm_tasks=$(( per_vm_tasks - 1 ))
IFS=$'\n'
lines=( $(cat input.txt) )
length=${#lines[@]}
cursor=0
hadoop_index=0
ending=false
while ! $ending
do
    for vm_i in $(seq 0 $per_vm_tasks)
    do
	per_vm_lines[$vm_i]=${lines[$cursor]}
	cursor=$(( cursor + 1 ))
	if [ $cursor -ge $length ]; then 
	    ending=true
	    break
	 fi
    done
    
    echo "perform hypothesis tests on hadoop-$hadoop_index as follows:"
    for vm_l in ${per_vm_lines[@]}
    do
	echo "$vm_l"
    done

    vm_cmd=$(for vm_i in $(seq 0 $per_vm_tasks); do echo -n "java Hypothesis $repeats ${per_vm_lines[$vm_i]} > /dev/null; sleep 60; "; done)
    vm_cmd_all=$(echo "cd parameter_test_controller; $vm_cmd echo 'finished' &")
    #echo "vm_cmd_all=$vm_cmd_all"
    ssh hadoop-$hadoop_index "eval $vm_cmd_all" &
    hadoop_index=$(( hadoop_index + 1 ))
done

sleep 10
echo 'jobs distributed'
