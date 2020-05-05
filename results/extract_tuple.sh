#!/bin/bash
IFS=$'\n'

while read -r line
do
    raw_tuple=$(echo $line | awk -F '_hypothesis_|_run_' '{print $1}')
    tuple=$(echo $raw_tuple | sed 's/%/ /g')
    echo $tuple
done
