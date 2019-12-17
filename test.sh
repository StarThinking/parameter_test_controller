#!/bin/bash

paras+=('dfs.data.transfer.client.tcpnodelay')
paras+=('dfs.reformat.disabled')
paras+=('fs.client.block.write.replace-datanode-on-failure.best-effort')
paras+=('dfs.permissions.enabled')
paras+=('dfs.balancer.keytab.enabled')
paras+=('dfs.namenode.lock.detailed-metrics.enabled')

for i in ${paras[@]}
do
    java Controller $i > "$i".txt
done
