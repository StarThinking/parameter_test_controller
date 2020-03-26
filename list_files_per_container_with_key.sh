#!/bin/bash

if [ $# -ne 1 ]; then echo 'wrong'; exit -1; fi

key=$1

cd ~/parameter_test_controller
for i in dfs*.txt
do
    if [ "$(grep "$key" $i)" != "" ]; then
        echo $i
    fi
done
