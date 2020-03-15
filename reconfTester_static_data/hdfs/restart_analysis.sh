#!/bin/bash

if [ $# -ne 2 ]; then
    echo 'wrong argument: methodfile component'
    exit -1
fi

methodfile=$1
compnent=$2

function check_restart() {
    component=$1
    stopped=0
    restarted=0
    while IFS= read -r line
    do
	if [[ "$line" == *"[msx-restart] $component"*"stop"* ]]; then
            stopped=1
        fi
	if [[ "$line" == *"[msx-restart] $component"*"init"* ]]; then
            if [ $stopped -eq 1 ]; then
                restarted=1
            fi
        fi
    done < "$methodfile"
    
    return $restarted
}

check_restart $compnent
echo $?
