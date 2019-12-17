#!/bin/bash

while IFS= read -r parameter
do
    java Controller $parameter > "$parameter".txt
done < "./input.txt"
