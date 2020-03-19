#!/bin/bash

java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 1048576 8 -2
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 1048576 8 -1
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 1048576 8 1
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 1048576 8 2

java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 8 1048576 -2
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 8 1048576 -1
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 8 1048576 1
java ReconfTester dfs.namenode.fs-limits.max-directory-items NameNode 8 1048576 2
#parameters+=('dfs.block.access.token.enable')
#parameters+=('dfs.encrypt.data.transfer')
#parameters+=('dfs.image.compress dfs.image.string-tables.expanded')
#
#reconf_points+=(-2)
#reconf_points+=(-1)
#reconf_points+=(1)
#reconf_points+=(2)
#for parameter in ${parameters[@]}
#do
#    for reconf_point in ${reconf_points[@]}
#    do
#        java ReconfTester $parameter NameNode true false $reconf_point
#        java ReconfTester $parameter NameNode false true $reconf_point
#    done
#done

#java ReconfTester dfs.bytes-per-checksum NameNode 512 8 -2
#java ReconfTester dfs.bytes-per-checksum NameNode 512 8 -1
#java ReconfTester dfs.bytes-per-checksum NameNode 512 8 1
#java ReconfTester dfs.bytes-per-checksum NameNode 512 8 2
#java ReconfTester dfs.bytes-per-checksum DataNode 512 8 -2
#java ReconfTester dfs.bytes-per-checksum DataNode 512 8 -1
#java ReconfTester dfs.bytes-per-checksum DataNode 512 8 1
#java ReconfTester dfs.bytes-per-checksum DataNode 512 8 2
#java ReconfTester dfs.ha.tail-edits.max-txns-per-lock NameNode 9223372036854775807 10 -2
#java ReconfTester dfs.ha.tail-edits.max-txns-per-lock NameNode 9223372036854775807 10 -1
#java ReconfTester dfs.ha.tail-edits.max-txns-per-lock NameNode 9223372036854775807 10 1
#java ReconfTester dfs.ha.tail-edits.max-txns-per-lock NameNode 9223372036854775807 10 2
#java ReconfTester dfs.blockreport.incremental.intervalMsec DataNode 0 1000 -2
#java ReconfTester dfs.blockreport.incremental.intervalMsec DataNode 0 1000 -1
#java ReconfTester dfs.blockreport.incremental.intervalMsec DataNode 0 1000 1
#java ReconfTester dfs.blockreport.incremental.intervalMsec DataNode 0 1000 2
#java ReconfTester dfs.blockreport.intervalMsec DataNode 21600000 2160000000 -2
#java ReconfTester dfs.blockreport.intervalMsec DataNode 21600000 2160000000 -1
#java ReconfTester dfs.blockreport.intervalMsec DataNode 21600000 2160000000 1
#java ReconfTester dfs.blockreport.intervalMsec DataNode 21600000 2160000000 2
#java ReconfTester dfs.datanode.cached-dfsused.check.interval.ms DataNode 600000 60 -2
#java ReconfTester dfs.datanode.cached-dfsused.check.interval.ms DataNode 600000 60 -1
#java ReconfTester dfs.datanode.cached-dfsused.check.interval.ms DataNode 600000 60 1
#java ReconfTester dfs.datanode.cached-dfsused.check.interval.ms DataNode 600000 60 2
