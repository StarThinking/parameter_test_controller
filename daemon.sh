#!/bin/bash

for addr in 128.105.144.234 128.105.144.251 128.105.144.254 128.105.144.106 128.105.144.182
do
    ssh $addr '~/parameter_test_controller/pm_fetch_analyze_hypo.sh' 
done

sleep 2

for addr in 128.105.144.234 128.105.144.251 128.105.144.254 128.105.144.106 128.105.144.182
do
    ssh $addr 'cat ~/my_white_list.txt' > ~/my_white_list.txt
done

THRESHOLD=3
cat ~/my_white_list.txt | sort | uniq -c | awk '{if($1 >= $THRESHOLD) print $0}' | awk '{print $2}' > ~/parameter_test_controller/white_list.txt

cd ~/parameter_test_controller
git add white_list.txt
git commit -m 'update wl'
git push origin master


for addr in 128.105.144.234 128.105.144.251 128.105.144.254 128.105.144.106 128.105.144.182
do
    ssh $addr '~/parameter_test_controller/container_utility_sh/whole_cluster_update_white_list.sh'
done
