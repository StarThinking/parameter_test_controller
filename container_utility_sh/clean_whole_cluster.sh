#!/bin/bash

if [ $# -ne 3 ]; then echo wrong pm vm tag; exit -1; fi
pm=$1
vm=$2
tag=$3

for i in $(seq 0 $pm); do ssh node-$i "cd ~/parameter_test_controller ; git pull" & done; sleep 20;

for i in $(seq 0 $pm); do ssh node-$i "~/parameter_test_controller/container_utility_sh/rm_all_containers.sh $vm"; done;

for i in $(seq 0 $pm); do ssh node-$i "~/parameter_test_controller/container_utility_sh/create_containers.sh $vm $tag"; done;

for i in $(seq 0 $pm); do ssh node-$i "~/parameter_test_controller/container_utility_sh/clean_update_all_container.sh $vm" & pids[$i]=$!; done; 
for p in ${pids[@]}; do wait $p; echo finished; done
