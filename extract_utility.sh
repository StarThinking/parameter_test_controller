#!/bin/bash

if [ $# -ne 2 ]; then echo 'wrong arguments: [log] [field]'; exit -1; fi

log=$1
if [[ "$log" != *"_hypothesis_"* ]] || [ ! -f $log ]; then echo "log $log is not a hypothesis log"; exit -1; fi
field=$2

if [ "$field" == "para" ] || [ "$field" == "parameter" ]; then
    sed -n 1p $log | awk '{print $2}'
fi

if [ "$field" == "comp" ] || [ "$field" == "component" ]; then
    sed -n 2p $log | awk '{print $2}'
fi

if [ "$field" == "para_and_comp" ] || [ "$field" == "parameter_and_component" ]; then
    para=$(sed -n 1p $log | awk '{print $2}')
    comp=$(sed -n 2p $log | awk '{print $2}')
    echo "$para $comp"
fi

if [ "$field" == "run" ] || [ "$field" == "runlog" ]; then
    log_sig=$(echo $log | awk -F '_hypothesis_' '{print $1}' | awk -F '/' '{print $NF}')
    check_num=$(find . -name "$log_sig"_run_* | wc -l)
    if [ $check_num -ne 1 ]; then echo "ERROR: when finding run_log for log sig $log_sig"; exit -1; fi
    find . -name "$log_sig"_run_*
fi
