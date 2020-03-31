#!/bin/bash

if [ $# -ne 1 ]; then exit -1; fi
num=$1

for i in $(seq 0 $num); do ssh node-$i "cd ~/parameter_test_controller ; git pull" & done; sleep 20;
for i in $(seq 0 $num); do ssh node-$i "~/parameter_test_controller/container_utility_sh/rm_all_containers.sh 19"; done;
for i in $(seq 0 $num); do ssh node-$i '~/parameter_test_controller/container_utility_sh/create_containers.sh 19 v0.9_2020.03.25'; done;
for i in $(seq 0 $num); do ssh node-$i '~/parameter_test_controller/container_utility_sh/clean_update_all_container.sh 19' & pids[$i]=$!;  done; for p in ${pids[@]}; do wait $p; echo finished; done
