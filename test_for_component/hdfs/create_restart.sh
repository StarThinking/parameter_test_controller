#!/bin/bash

root_dir=~/parameter_test_controller/test_for_component/hdfs

for c in namenode datanode journalnode
do
    for i in $(cat start_$c.txt)
    do 
	if [ "$(grep $i $root_dir/component_test_in_method_filter_success/restart_$c.txt)" != "" ]; then 
	    echo $i >> restart_$c.txt 
	 fi
    done 
done
