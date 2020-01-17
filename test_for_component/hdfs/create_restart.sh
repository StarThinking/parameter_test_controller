#!/bin/bash

for c in namenode datanode journalnode
do
    for i in $(cat start_$c.txt)
    do 
	if [ "$(grep $i backup_all_tests/restart_$c.txt)" != "" ]; then 
	    echo $i >> restart_$c.txt 
	 fi
    done 
done
