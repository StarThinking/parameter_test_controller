#!/bin/bash

rm target/*.class
rm target/*.txt

javac src/*.java -d target/
