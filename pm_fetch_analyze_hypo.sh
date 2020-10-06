#!/bin/bash

cd ~/parameter_test_controller/target

ls | xargs rm -rf; pids=(); for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do ssh node-$i "find ~/parameter_test_controller/target/ -type f -maxdepth 1 -name '*' | xargs rm; ~/parameter_test_controller/container_utility_sh/docker_fetch_result.sh _hypothesis_ /root/parameter_test_controller/target/ /root/parameter_test_controller/target/; cd /root/parameter_test_controller/; rm -rf target.$i; cp -r target target.$i; tar zcvf $i.tar.gz target.$i" & pids+=($!); done; for id in ${pids[@]}; do wait $id; echo "pid $id finished"; done; for i in $(grep -oP "node-[0-9]{1,2}$" /etc/hosts | sed 's/node-//g' | sort -n); do scp node-$i:~/parameter_test_controller/$i.tar.gz ~/parameter_test_controller/target/; done; find -maxdepth 1 -name '*txt*' | xargs rm ; find -maxdepth 1 -name '*.class' | xargs rm; ls;

cd ~/parameter_test_controller/target

for i in *.tar.gz; do tar zxvf $i; rm $i; done; mkdir single_hypothesis; find -maxdepth 2 -name '*single_hypothesis_*' | xargs mv -t single_hypothesis; cd single_hypothesis; ~/parameter_test_controller/just_compile.sh; mkdir no_need_hypo/; cf=0.9999; for hlog in *_hypothesis_*; do ~/parameter_test_controller/hypo_analysis.sh $hlog $cf 1; done | while read line; do mv $line no_need_hypo/; done

cd no_need_hypo/
for i in *_hypothesis_*; do cat $i | head -n 3 | tail -n 1; done | sort | awk -F ' |@@@' '{print $2"@@@"$3}'
