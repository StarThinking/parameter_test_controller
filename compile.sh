#!/bin/bash

rm target/*.class
rm target/*.txt

javac -cp src/lib/commons-math3-3.6.1.jar src/*.java -d target/
