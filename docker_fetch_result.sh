#!/bin/bash

for d in $(seq 0 19)
do
    files=$(docker exec hadoop-$d bash -c 'ls ~/parameter_test_controller/ | grep dfs')
    for f in ${files[@]}; do docker cp hadoop-$d:/root/parameter_test_controller/$f .; done
done
