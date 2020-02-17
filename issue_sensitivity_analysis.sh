#!/bin/bash

issue_file=$1

issue_entrys=( $(grep -rn parameter $issue_file | awk -F ':' '{print $1}') )

for entry in ${issue_entrys[@]}
do
    parameter=$(awk -v line=$(( entry + 0 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    component=$(awk -v line=$(( entry + 1 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    componentHasStopped=$(awk -v line=$(( entry + 2 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    v1v2=$(awk -v line=$(( entry + 3 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    testName=$(awk -v line=$(( entry + 4 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    echo "$parameter""%""$component""%""$testName""%""$componentHasStopped""%""$v1v2"
done
