# start dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; ps aux | grep dispatcher | awk '{print $2}' | xargs kill -9;  nohup ~/parameter_test_controller/dispatcher.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# start hypo dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; nohup ~/parameter_test_controller/dispatcher_hypo.sh> nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# check finish ratio
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do finished=$(ssh node-$i "cat ~/nohup.txt | grep assign | wc -l"); all=$(ssh node-$i "cat nohup.txt | head -n 1" | awk -F ' = ' '{print $2}'); ratio=$(echo "scale=2; $finished / $all" | bc); echo "$finished out of $all are finished $ratio"; done

########
# collect hypothesis .txt
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh _hypothesis_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/"; scp node-$i:~/parameter_test_controller/target/*.txt .; done

# check hypo that contains might
for i in *.txt; do if [ "$(grep might $i)" != "" ]; then echo $i; cat $i | tail -n 5; echo "";  fi ; done

# check if hypo unfinished
for i in *hypothesis*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done

for i in $(for i in *hypothesis*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done); do echo $i; grep 'v1v2 test failed' $i | wc -l; grep 'Test vvMode=v1v2' $i | wc -l; grep 'v1v1 or v2v2 test failed' $i | wc -l; grep 'Test vvMode=v2v2\|Test vvMode=v1v1' $i | wc -l; done

# save suspicious hypo log and fetch run log
mkdir suspicious
conf=0.8; mv $(for i in *.txt; do ~/parameter_test_controller/hypo_analysis.sh $i $conf; done) suspicious/
cd suspicious
# fetch corresponding run logs
for c in *.txt; do for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh "$(echo "$c" | awk -F '_hypothesis_' '{print $1}')"_run_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/"; scp node-$i:~/parameter_test_controller/target/*.txt .; done; done
#########

rm /ttttmp.txt; ls ~/parameter_test_controller/target/ | grep .txt | awk -F '_hypothesis_' '{print $1}' | sort -u > /ttttmp.txt; for i in $(cat /ttttmp.txt); do echo $i; ~/parameter_test_controller//hypo_analysis_parallel.sh $i; echo ''; done


