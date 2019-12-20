#!/bin/bash

function check_restart() {
    methodfile=$1
    component=$2
    stopped=0
    restarted=0
    while IFS= read -r line
    do
        if [[ "$line" == *"$component stop"* ]]; then
            stopped=1
        fi
        if [[ "$line" == *"$component start"* ]]; then
            if [ $stopped -eq 1 ]; then
                restarted=1
            fi
        fi
    done < "$methodfile"
    
    return $restarted
}

NameNode_restart=()
DataNode_restart=()
JournalNode_restart=()
for method in *
do
    check_restart $method NameNode
    ret=$?
    if [ $ret -eq 1 ]; then
        echo "$method restart NameNode"
        NameNode_restart+=("$method")
    fi
done

for method in *
do
    check_restart $method DataNode
    ret=$?
    if [ $ret -eq 1 ]; then
        echo "$method restart DataNode"
        DataNode_restart+=("$method")
    fi
done

for method in *
do
    check_restart $method JournalNode
    ret=$?
    if [ $ret -eq 1 ]; then
        echo "$method restart JournalNode"
        JournalNode_restart+=("$method")
    fi
done

# analysis for solely NameNode restart
for method in ${NameNode_restart[@]}
do
    datanode_restart=0
    journalnode_restart=0
#    for d in ${DataNode_restart[@]}
#    do
#        # method is found in DataNode_restart
#        if [ "$method" == "$d" ]; then
#            datanode_restart=1
#        fi
#    done
#
#    for j in ${JournalNode_restart[@]}
#    do
#        # method is found in JournalNode_restart
#        if [ "$method" == "$j" ]; then
#            journalnode_restart=1
#        fi
#    done

    if [ $datanode_restart -eq 0 ] && [ $journalnode_restart -eq 0 ]; then
        echo "$method" >> restart_namenode.txt
    fi
done

# analysis for solely DataNode restart
for method in ${DataNode_restart[@]}
do
    namenode_restart=0
    journalnode_restart=0
#    for n in ${NameNode_restart[@]}
#    do
#        # method is found in DataNode_restart
#        if [ "$method" == "$n" ]; then
#            namenode_restart=1
#        fi
#    done
#
#    for j in ${JournalNode_restart[@]}
#    do
#        # method is found in JournalNode_restart
#        if [ "$method" == "$j" ]; then
#            journalnode_restart=1
#        fi
#    done

    if [ $namenode_restart -eq 0 ] && [ $journalnode_restart -eq 0 ]; then
        echo "$method" >> restart_datanode.txt
    fi
done

# analysis for solely JournalNode restart
for method in ${JournalNode_restart[@]}
do
    namenode_restart=0
    datanode_restart=0
#    for n in ${NameNode_restart[@]}
#    do
#        # method is found in DataNode_restart
#        if [ "$method" == "$n" ]; then
#            namenode_restart=1
#        fi
#    done
#
#    for d in ${DataNode_restart[@]}
#    do
#        # method is found in JournalNode_restart
#        if [ "$method" == "$d" ]; then
#            datanode_restart=1
#        fi
#    done

    if [ $namenode_restart -eq 0 ] && [ $datanode_restart -eq 0 ]; then
        echo "$method" >> restart_journalnode.txt
    fi
done
