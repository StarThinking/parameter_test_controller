#!/bin/bash
# for i in $(ls | awk -F '_issue_' '{print $1}'); do mkdir $i; mv "$i"_issue_*.txt $i;  done
# mkdir result
# for i in dfs*; do ~/parameter_test_controller/repeat_analysis.sh $i 1 > result/$i.txt; done

cat * | grep % | awk -F ' ' '{print $2}' | sort -u > issues.txt

for t in $(cat issues.txt); do echo -n "$t"" "; grep -rnF $t dfs.* | wc -l;  done > distribution.txt.bak
sort -n -k 2 distribution.txt.bak > distribution.txt
rm distribution.txt.bak
