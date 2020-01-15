#!/bin/bash

echo 'none' > reconfig_mode
echo 'NameNode' > reconfig_component
echo 'dfs.data.transfer.client.tcpnodelay' > parameter
echo '0' > componentHasStopped
echo 'none' > currentBeforeClassTestName
echo '0' > v1
echo '0' > v2
