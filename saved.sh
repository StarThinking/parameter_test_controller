# start dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; ps aux | grep dispatcher | awk '{print $2}' | xargs kill -9; nohup ~/parameter_test_controller/dispatcher.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# start hypo dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; ps aux | grep dispatcher | awk '{print $2}' | xargs kill -9; nohup ~/parameter_test_controller/dispatcher_hypo.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

###############
# collect hypothesis .txt
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/parameter_test_controller/target/*.txt; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh _hypothesis_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh _run_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/;"; mkdir ~/parameter_test_controller/target/$i; scp node-$i:~/parameter_test_controller/target/*.txt ~/parameter_test_controller/target/$i; ssh node-$i "rm ~/parameter_test_controller/target/*.txt"; done; for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do for j in $i/*; do mv $j ./"$(echo $j | awk -F '/' '{print $2}')"_$i; done; rm -rf $i; done

# result
find target/ -name '*_hypothesis_*' | awk -F '_hypothesis_' '{print $1}' | sort -u | while read line; do echo $line; ./hypo_analysis_parallel.sh $line 0.95; echo ""; done > tmp.txt
################

# check finish ratio
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do finished=$(ssh node-$i "cat ~/nohup.txt | grep assign | wc -l"); all=$(ssh node-$i "cat nohup.txt | head -n 2 | tail -n 1" | awk -F ' = ' '{print $2}'); ratio=$(echo "scale=2; $finished / $all" | bc); echo "$finished out of $all are finished $ratio"; done

########
# check hypo that contains might
for i in *_hypothesis_*; do if [ "$(grep might $i)" != "" ]; then echo $i; cat $i | tail -n 5; echo "";  fi ; done

cf=0.95; clean_sheet=1; for i in *_hypothesis_*; do ~/parameter_test_controller/hypo_analysis.sh $i $cf $clean_sheet; done | while read line; do echo "$line"; cat $line | tail -n 5; cat $line | head -n 2; echo ''; done

# check if hypo unfinished
for i in *_hypothesis_*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done

for i in $(for i in *hypothesis*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done); do echo $i; grep 'v1v2 test failed' $i | wc -l; grep 'Test vvMode=v1v2' $i | wc -l; grep 'v1v1 or v2v2 test failed' $i | wc -l; grep 'Test vvMode=v2v2\|Test vvMode=v1v1' $i | wc -l; done

# do not forget to collect hypo first
# save suspicious hypo log and fetch run log
rm -rf suspicious; mkdir suspicious; cf=0.95; clean_sheet=1; mv $(for i in *_hypothesis_*; do ~/parameter_test_controller/hypo_analysis.sh $i $cf $clean_sheet; done) suspicious/
cd suspicious

# fetch corresponding run logs
for c in *_hypothesis_*; do i=$(echo $c | awk -F '_' '{print $NF}'); ssh node-$i "rm ~/parameter_test_controller/target/*txt*; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh "$(echo "$c" | awk -F '_hypothesis_' '{print $1}')"_run_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/"; scp node-$i:~/parameter_test_controller/target/*txt* .; done
#########

rm /ttttmp.txt; ls ~/parameter_test_controller/target/ | grep .txt | awk -F '_hypothesis_' '{print $1}' | sort -u > /ttttmp.txt; for i in $(cat /ttttmp.txt); do echo $i; ~/parameter_test_controller//hypo_analysis_parallel.sh $i; echo ''; done


