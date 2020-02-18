#!/bin/bash

# for test in $(cat ../vanilla.txt); do occ=$(grep -wF $test * | wc -l); perc=$(echo "scale=2; ( $occ * 100 / 1760 ) / 2" | bc); echo "$perc"" ""$test" ; done >> percent.txt
issue_file=$1

issue_entrys=( $(grep -rn parameter $issue_file | awk -F ':' '{print $1}') )
echo "# of issue_entrys: ${#issue_entrys[@]}"

for entry in ${issue_entrys[@]}
do
    parameter=$(awk -v line=$(( entry + 0 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    #component=$(awk -v line=$(( entry + 1 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    #componentHasStopped=$(awk -v line=$(( entry + 2 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    #v1v2=$(awk -v line=$(( entry + 3 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    testName=$(awk -v line=$(( entry + 4 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    #result=$(awk -v line=$(( entry + 5 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')

    failureMessageExist=$(awk -v line=$(( entry + 6 )) 'NR==line' $issue_file | awk -F ':' '{print $1}' | sed -e 's/^[ \t]*//')
    if [ "$failureMessageExist" != 'failureMessage' ]; then continue; fi
    failureMessage=$(awk -v line=$(( entry + 6 )) 'NR==line' $issue_file | awk -F ':' '{print $2}' | sed -e 's/^[ \t]*//')
    echo "$parameter""%""$testName""%""$componentHasStopped""%""$v1v2""%""$failureMessage"
done

#for i in dfs*; do ./issue_sensitivity_analysis.sh $i | sort -u >> message.$i; done
