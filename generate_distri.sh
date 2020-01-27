#!/bin/bash
# for i in $(ls | awk -F '_issue_' '{print $1}' | sort -u); do mkdir $i; mv "$i"_issue*.txt $i;  done
# mkdir result
# for i in dfs*; do ~/parameter_test_controller/repeat_analysis.sh $i 1 > result/$i.txt; done
# cd result

# cat * | grep % | awk -F ' ' '{print $2}' | sort -u > issues.txt


# for i in $(cat issues.txt.all); do if [ "$(grep -Fw $i ~/reconfig_test_result_new_boolean/vanilla/2/issue/result/issues.txt)" == "" ]; then echo $i; fi ; done >> issues.txt
# for i in dfs*; do ~/parameter_test_controller/repeat_analysis_ref.sh $i 1 ; done | tee report_1.txt

for t in $(cat issues.txt)
do 
    echo -n "$t"" " 
    grep -rF $t dfs.* | wc -l
done > distribution.txt.bak
sort -n -k 2 distribution.txt.bak > distribution.txt
rm distribution.txt.bak
