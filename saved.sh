# start dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; ps aux | grep dispatcher | awk '{print $2}' | xargs kill -9; nohup ~/parameter_test_controller/dispatcher.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# start hypo dispatcher
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "rm ~/nohup.txt; ps aux | grep dispatcher | awk '{print $2}' | xargs kill -9; nohup ~/parameter_test_controller/dispatcher_hypo.sh > nohup.txt &" & pids[$i]=$!; done; for p in ${pids[@]}; do wait $p; echo "$p is done"; done

# collect hypothesis .txt
ls | xargs rm -rf; pids=(); for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "find ~/parameter_test_controller/target/ -type f -maxdepth 1 -name '*' | xargs rm; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh _hypothesis_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/; cd /root/parameter_test_controller/; rm -rf target.$i; cp -r target target.$i; tar zcvf $i.tar.gz target.$i" & pids+=($!); done; for id in ${pids[@]}; do wait $id; echo "pid $id finished"; done; for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do scp node-$i:~/parameter_test_controller/$i.tar.gz ~/parameter_test_controller/target/; done; find -maxdepth 1 -name '*txt*' | xargs rm ; find -maxdepth 1 -name '*.class' | xargs rm; ls;

#
for i in *.tar.gz; do tar zxvf $i; rm $i; done; mkdir single_hypothesis; find -maxdepth 2 -name '*single_hypothesis_*' | xargs mv -t single_hypothesis; cd single_hypothesis; ~/parameter_test_controller/just_compile.sh; mkdir no_need_hypo/; cf=0.9999; for hlog in *_hypothesis_*; do ~/parameter_test_controller/hypo_analysis.sh $hlog $cf 1; done | while read line; do mv $line no_need_hypo/; done

#
for i in *.tar.gz; do tar zxvf $i; rm $i; done; mkdir single_run; find -maxdepth 2 -name '*single_run_*' | xargs mv -t single_run; cd single_run; ~/parameter_test_controller/just_compile.sh; mkdir invalid; for log in *single_run_*; do if [ "$(grep 'invalid value' $log)" != "" ]; then mv $log invalid/; fi; done

# check finish ratio
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do finished=$(ssh node-$i "cat ~/nohup.txt | grep assign | wc -l"); all=$(ssh node-$i "cat nohup.txt | head -n 2 | tail -n 1" | awk -F ' = ' '{print $2}'); ratio=$(echo "scale=2; $finished / $all" | bc); echo "$finished out of $all are finished $ratio"; done

# check hypo that contains might

# list white list candidates
for i in *_hypothesis_*; do cat $i | head -n 3 | tail -n 1; done | sort | awk -F ' |@@@' '{print $2" "$3}'

cat tmp.txt | sort | uniq -c | awk '{if($1 >= 5) print $0}'

# check if hypo unfinished
for i in *_hypothesis_*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done

for i in $(for i in *hypothesis*; do if [ "$(grep 'Total execution time' $i)" == "" ]; then echo $i; fi; done); do echo $i; grep 'v1v2 test failed' $i | wc -l; grep 'Test vvMode=v1v2' $i | wc -l; grep 'v1v1 or v2v2 test failed' $i | wc -l; grep 'Test vvMode=v2v2\|Test vvMode=v1v1' $i | wc -l; done

# collect unfininshed tasks
for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do finished=$(ssh node-$i "cat ~/nohup.txt | grep assign | wc -l"); all=$(ssh node-$i "cat nohup.txt | head -n 2 | tail -n 1" | awk -F ' = ' '{print $2}'); ssh node-$i "cat ~/parameter_test_controller/task.txt | tail -n $(( all - finished ))"; done > left.1.txt
