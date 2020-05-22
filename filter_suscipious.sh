#!/bin/bash

~/parameter_test_controller/compile.sh
mkdir suspicious
mv $(for i in *.txt; do ~/parameter_test_controller/hypo_analysis.sh $i; done) suspicious/
cd suspicious
# fetch corresponding run logs
for c in *.txt; do for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh "$(echo "$c" | awk -F '_hypothesis_' '{print $1}')"_run_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/"; scp node-$i:~/parameter_test_controller/target/*.txt .; done; done
