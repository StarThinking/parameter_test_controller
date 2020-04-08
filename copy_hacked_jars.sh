#!/bin/bash

# Hacked NameNode, DataNode, JournalNode
cp ~/hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs/target/hadoop-hdfs-3.1.2.jar  ~/.m2/repository/org/apache/hadoop/hadoop-hdfs/3.1.2/

# Hacked Configured and newly added ReconfAgent
cp ~/hadoop-3.1.2-src/hadoop-common-project/hadoop-common/target/hadoop-common-3.1.2.jar ~/.m2/repository/org/apache/hadoop/hadoop-common/3.1.2/

# Newly added MyRunListener
cp ~/hadoop-3.1.2-src/hadoop-common-project/hadoop-common/target/hadoop-common-3.1.2-tests.jar ~/.m2/repository/org/apache/hadoop/hadoop-common/3.1.2/
