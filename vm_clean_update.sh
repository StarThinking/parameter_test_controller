#!/bin/bash

for i in $(seq 0 9)
do 
    echo $i
    ssh hadoop-$i "killall -9 bash"
    ssh hadoop-$i "pkill -9 java; cd ~/parameter_test_controller; rm *.txt; git clean -df; git checkout -- *; git pull; javac Controller.java; javac Hypothesis.java"
done
