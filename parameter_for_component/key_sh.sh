#!/bin/bash

if [ $# -ne 1 ]; then
    echo 'wrong arguments'
    exit 1
fi
component=$1

# it is important that we grep by "$i " as parameter has suffix
for i in $(cat "$component"_getInt.txt); do grep -rnF "$i " parameter_values_unit_tests.txt ; done | awk -F ' ' '{print $3" " $5}' | sort -u > "$component"_getInt_values_unit_tests_notmerged_with_null.txt

grep -v null "$component"_getInt_values_unit_tests_notmerged_with_null.txt > "$component"_getInt_values_unit_tests_notmerged_without_null.txt

for i in $(cat "$component"_getInt_values_unit_tests_notmerged_without_null.txt | awk -F ' ' '{print $1}' | sort -u); do values=( $(grep "$i " "$component"_getInt_values_unit_tests_notmerged_without_null.txt | awk -F ' ' '{print $2}') ); if [ ${#values[@]} -gt 0 ]; then echo -n "$i " ; echo "${values[@]}" | sort -n ; fi ; done  > "$component"_getInt_values_unit_tests_merged_all.txt 

#for i in $(cat journalnode_getInt.txt); do values=( $(grep "$i " journalnode_getInt_values_unit_tests_notmerged_without_null.txt | awk -F ' ' '{print $2}') ); if [ ${#values[@]} -gt 1 ]; then echo "$i"" ""${values[@]}"; fi ; done  > journalnode_getInt_values_unit_tests_merged_multiple.txt
