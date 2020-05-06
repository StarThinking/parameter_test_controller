# start dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; nohup ~/parameter_test_controller/dispatcher.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# start hypo dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do sleep; ssh node-$i "rm ~/nohup.txt; nohup ~/parameter_test_controller/hypo_dispatcher.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# check finish ratio
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do finished=$(ssh node-$i "cat ~/nohup.txt | grep assign | wc -l"); all=$(ssh node-$i "cat nohup.txt | head -n 1" | awk -F ' = ' '{print $2}'); ratio=$(echo "scale=2; $finished / $all" | bc); echo "$finished out of $all are finished $ratio"; done

# collect hypothesis .txt
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh 19 hypothesis"; scp node-$i:~/parameter_test_controller/target/*.txt .; done

# check hypo that contains might
for i in *.txt; do if [ "$(grep might $i)" != "" ]; then echo $i; cat $i | tail -n 5; echo "";  fi ; done

# check if hypo unfinished
for i in *hypothesis*; do num=$(cat $i | grep vvMode | wc -l); if [ $num -ne 150 ]; then echo $i; echo $num; fi; done

# fetch run log
mkdir suspicious
mv $(for i in *.txt; do ~/parameter_test_controller/hypo_analysis.sh $i; done) suspicious/

for c in *.txt; do for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh 19 "$(echo "$c" | awk -F '_hypothesis_' '{print $1}')"_run_"; scp node-$i:~/parameter_test_controller/target/*.txt .; done; done
