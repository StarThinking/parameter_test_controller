#!/bin/bash

cat * | grep % | awk -F ' ' '{print $2}' | sort -u > issues.txt

for t in $(cat issues.txt); do echo -n "$t"" "; grep -rnF $t dfs.* | wc -l;  done > distribution.txt.bak
sort -n -k 2 distribution.txt.bak > distribution.txt
rm distribution.txt.bak
