#!/bin/bash

if [ $# -ne 1 ]; then echo wrong; exit -1; fi
num=$1

for i in $(seq 0 $num); do docker container stop hadoop-$i; docker container rm hadoop-$i; done
