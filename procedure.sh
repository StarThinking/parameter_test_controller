#!/bin/bash

if [ $# -lt 3 ]; then
    echo "./procedure.sh [proj] [u_test] [[para,component,point,v1,v2] [...]]"
    exit -1
fi

# disable conf tracking
echo 'true' > ~/reconf_test_gen/lib/enable

# check if test is in white list
#if [ "$(grep ^"$parameter $component"$ ~/parameter_test_controller/white_list.txt)" != "" ]; then
#    echo "found in white list as $parameter $component, skip this test"
#    exit 0
#fi

proj=$1
u_test=$2
shift 2
hypo_max_repeats=30
LOG_TIME="$(($(date +%s%N)/1000000))"

# argument $@ is h_list: "para,component,point,v1,v2 para,component,point,v1,v2"
function test_procedure {   
    echo "args: $@"
    conbime_type=""
    OLDIFS=$IFS
    task_array=()
    IFS='%%%' read -ra task_array <<< "$@"
    IFS=$OLDIFS
    h_list_size=$(echo ${task_array[@]} | tr ' ' '\n' | wc -l)
    echo "h_list_size = $h_list_size"

    if [ $h_list_size -eq 1 ]; then
        conbime_type="single"
    elif [ $h_list_size -ge 2 ]; then
        conbime_type="combine"
    else
        echo "ERROR: h_list_size $h_list_size is wrong!"
    fi
    
    echo "";
    echo ">>>>>>>> running $conbime_type run_test for $(echo $@ | awk -F '@@@|%%%' '{for (i=1;i<=NF;i+=5) print $i}' | tr "\n" ",")"
    java -cp /root/parameter_test_controller/target/ HConfRunner 'run' $proj $u_test $@ > /root/parameter_test_controller/target/"$proj.$u_test.$LOG_TIME."$conbime_type"_run_$RANDOM$RANDOM.txt"
    run_rc=$?
    echo "run_rc is $run_rc"
    if [ $run_rc -eq 0 ]; then
        echo "no issue, bye bye."
        return 0;
    else
        if [ $conbime_type == "single" ] ; then
            echo "";
            echo ">>>>>>>> running $conbime_type hypo_test for $(echo $@ | awk -F '@@@|%%%' '{for (i=1;i<=NF;i+=5) print $i}' | tr "\n" ",")"
            java -cp /root/parameter_test_controller/target/ HConfRunner 'hypothesis' $proj $u_test $@ > /root/parameter_test_controller/target/"$proj.$u_test.$LOG_TIME."$conbime_type"_hypothesis_$RANDOM$RANDOM.txt"
            return 0
        else
            echo "";
            echo ${task_array[@]} | tr ' ' '\n' | while read per_task; do
                test_procedure $per_task    
            done
        fi
    fi
}

test_procedure $@
echo "test_procedure returns $?"
