#!/bin/bash

if [ $# -ne 1 ]; then echo "wrong [tag]"; exit -1; fi
pm=$(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n | wc -l)
pm=$(( pm -1 ))
vm=$(( $(cat /proc/cpuinfo | grep 'processor' | wc -l) / 2 ))
tag=$1

for i in $(seq 0 $pm); do ssh node-$i "killall -9 dispatcher_hypo.sh; pkill dispatcher_hypo.sh; killall -9 dispatcher.sh; pkill dispatcher.sh; "; done;

for i in $(seq 0 $pm); do ssh node-$i "cd ~/parameter_test_controller; git pull; cd ~/reconf_test_gen; git pull" & ppids[$i]=$!; done; for p in ${ppids[@]}; do wait $p; echo git update finished; done

for i in $(seq 0 $pm); do ssh node-$i "~/parameter_test_controller/container_utility_sh/clean_update_all_container.sh $vm" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo container git update finished; done
