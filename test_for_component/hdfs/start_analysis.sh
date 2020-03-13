#!/bin/bash

if [ $# -ne 2 ]; then
    echo 'wrong argument: methodfile component'
    exit -1
fi

methodfile=$1
compnent=$2

function check_start() {
    #methodfile="org.apache.hadoop.tracing.TestTracingShortCircuitLocalRead#testShortCircuitTraceHooks"
    component=$1
    started=0
    while IFS= read -r line
    do
        if [[ "$line" == *"[msx-restart] $component"*"start"* ]]; then
            started=$(( started + 1 ))
        fi
    done < "$methodfile"
    
    return $started
}

check_start $compnent
echo $?
