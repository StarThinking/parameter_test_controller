#!/bin/bash

if [ $# -ne 4 ]; then echo wrong; exit -1; fi 

parameter=$1
component=$2
v1=$3
v2=$4

java ReconfTester $parameter $component $v1 $v2 -1; sleep 5

for i in $(seq 1 30)
do
    java ReconfTester $parameter $component $v1 $v2 $i; sleep 5
done
