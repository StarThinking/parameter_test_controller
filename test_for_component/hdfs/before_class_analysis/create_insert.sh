#!/bin/bash
#grep -rn @BeforeClass * | grep '.java' | awk -F ':' '{print $2" " $1}'
files=( $(cat files.txt) )
lines=( $(cat lines.txt) )
numoffiles=${#files[@]}
numoflines=${#lines[@]}

echo $numoffiles
echo $numoflines
num=$(( numoffiles - 1 ))
for i in $(seq 0 $num)
do
    beforeclass_nextline=$(( ${lines[$i]} + 1 ))
    content_beforeclass_nextline=$(awk -v line=$beforeclass_nextline 'NR==line' ${files[$i]})
    while [ "$(echo $content_beforeclass_nextline | grep '{')" == "" ]
    do
        beforeclass_nextline=$(( beforeclass_nextline + 1 ))
        content_beforeclass_nextline=$(awk -v line=$beforeclass_nextline 'NR==line' ${files[$i]})
    done

    if [ "$(echo $content_beforeclass_nextline | grep '{')" == "" ]; then
        echo 'wrong'
    fi
    echo "sed -i '"$beforeclass_nextline"a System.out.println(\"[msx] before_class\");' "${files[$i]}""
done
