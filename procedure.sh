#!/bin/bash

if [ $# -lt 3 ]; then
    echo "./procedure.sh [proj] [u_test] [[para,component,point,v1,v2] [...]]"
    exit -1
fi

# disable conf tracking
echo 'false' > ~/reconf_test_gen/lib/enable

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
    echo "";
    echo "args: $@"
    local conbime_type=""
    local OLDIFS=$IFS
    local task_array=()
    IFS='%%%' read -ra task_array <<< "$@"
    IFS=$OLDIFS
    local h_list_size=$(echo ${task_array[@]} | tr ' ' '\n' | wc -l)
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
    local run_rc=$?
    echo "run_rc is $run_rc"
    if [ $run_rc -eq 0 ]; then
        echo "no issue, bye bye."
        return 0
    else
        if [ $conbime_type == "single" ] ; then
            echo "";
            echo ">>>>>>>> running $conbime_type hypo_test for $(echo $@ | awk -F '@@@|%%%' '{for (i=1;i<=NF;i+=5) print $i}' | tr "\n" ",")"
            java -cp /root/parameter_test_controller/target/ HConfRunner 'hypothesis' $proj $u_test $@ > /root/parameter_test_controller/target/"$proj.$u_test.$LOG_TIME."$conbime_type"_hypothesis_$RANDOM$RANDOM.txt"
            return 0
        else
            local half_index=$(( h_list_size / 2))
            echo "half_index = $half_index"
            local first_tasks=$(echo ${task_array[@]} | awk -v first_end="$half_index" -F ' ' '{for (i=1;i<=first_end;i++) if (i != first_end) {printf "%s", $i"%%%"} else printf "%s", $i}')
            local second_tasks=$(echo ${task_array[@]} | awk -v first_end="$half_index" -v second_end="$h_list_size" -F ' ' '{for (i=first_end+1;i<=second_end;i++) if (i != second_end) {printf "%s", $i"%%%"} else printf "%s", $i}')
            #echo "first_tasks = $first_tasks"
            #echo "second_tasks = $second_tasks"
            #| awk '{for(i=0;i<2;i++) print $i}'
            
            test_procedure $first_tasks
            test_procedure $second_tasks
        fi
    fi
}

test_procedure $@
echo "test_procedure returns $?"
