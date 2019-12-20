#!/bin/bash

component=$1
cd /root/parameter_test_controller
javac Controller.java
while IFS= read -r parameter
do
    java Controller $parameter $component > /root/parameter_test_controller/"$parameter".txt
done < "/root/parameter_test_controller/input.txt"
