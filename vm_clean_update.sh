#!/bin/bash

for i in $(seq 0 19)
do 
    echo $i
    docker exec hadoop-$i bash -c "killall -9 bash"
    docker exec hadoop-$i bash -c "pkill -9 java; cd /root/parameter_test_controller; rm *.txt; git clean -df; git checkout -- *; git pull; javac *.java"
done
