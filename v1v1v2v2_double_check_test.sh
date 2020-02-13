#!/bin/bash

java Controller Boolean dfs.block.access.token.enable NameNode org.apache.hadoop.fs.viewfs.TestViewFileSystemHdfs#testNflyClosestRepair org.apache.hadoop.hdfs.security.TestDelegationToken#testDTManagerInSafeMode org.apache.hadoop.hdfs.server.balancer.TestBalancerWithHANameNodes#testBalancerWithHANameNodes org.apache.hadoop.hdfs.server.namenode.ha.TestHASafeMode#testAppendWhileInSafeMode org.apache.hadoop.hdfs.server.namenode.ha.TestHAMetrics#testHAInodeCount
sleep 10
java Controller Boolean dfs.encrypt.data.transfer NameNode org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testAuthentication org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testIntegrity org.apache.hadoop.hdfs.server.balancer.TestBalancerWithEncryptedTransfer#testEncryptedBalancer2 org.apache.hadoop.hdfs.server.balancer.TestBalancerWithSaslDataTransfer#testBalancer0Authentication org.apache.hadoop.hdfs.server.balancer.TestBalancerWithSaslDataTransfer#testBalancer0Integrity
sleep 10
java Controller Boolean dfs.encrypt.data.transfer DataNode org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testAuthentication org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testIntegrity org.apache.hadoop.hdfs.server.balancer.TestBalancerWithEncryptedTransfer#testEncryptedBalancer2 org.apache.hadoop.hdfs.server.balancer.TestBalancerWithSaslDataTransfer#testBalancer0Authentication org.apache.hadoop.hdfs.server.balancer.TestBalancerWithSaslDataTransfer#testBalancer0Integrity
sleep 10
java Controller Boolean dfs.image.compress NameNode org.apache.hadoop.hdfs.server.namenode.ha.TestStandbyCheckpoints#testBothNodesInStandbyState
sleep 10
java Controller Boolean dfs.image.string-tables.expanded NameNode org.apache.hadoop.hdfs.TestDFSUpgradeFromImage#testUpgradeFromRel023ReservedImage org.apache.hadoop.hdfs.TestDFSUpgradeFromImage#testUpgradeFromRel1ReservedImage org.apache.hadoop.hdfs.TestDFSUpgradeFromImage#testUpgradeFromRel2ReservedImage
sleep 10
java Controller Int dfs.bytes-per-checksum NameNode org.apache.hadoop.hdfs.TestEncryptionZones#testReadWriteUsingWebHdfs org.apache.hadoop.hdfs.TestEncryptionZonesWithKMS#testReadWriteUsingWebHdfs
sleep 10
java Controller Int dfs.bytes-per-checksum DataNode org.apache.hadoop.hdfs.TestEncryptionZones#testReadWriteUsingWebHdfs org.apache.hadoop.hdfs.TestEncryptionZonesWithKMS#testReadWriteUsingWebHdfs
sleep 10
java Controller Long dfs.ha.tail-edits.max-txns-per-lock NameNode org.apache.hadoop.hdfs.TestRollingUpgrade#testQueryAfterRestart org.apache.hadoop.hdfs.TestRollingUpgrade#testRollingUpgradeWithQJM
sleep 10
java Controller Long dfs.blockreport.incremental.intervalMsec DataNode org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testAuthentication org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testIntegrity org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testNoSaslAndSecurePortsIgnored org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testPrivacy org.apache.hadoop.hdfs.server.namenode.TestFsck#testStoragePoliciesCK
sleep 10
java Controller Long dfs.blockreport.intervalMsec DataNode org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testAuthentication org.apache.hadoop.hdfs.protocol.datatransfer.sasl.TestSaslDataTransfer#testIntegrity org.apache.hadoop.hdfs.server.mover.TestMover#testMoverWithKeytabs org.apache.hadoop.hdfs.server.mover.TestMover#testScheduleBlockWithinSameNode org.apache.hadoop.hdfs.server.namenode.TestFsck#testStoragePoliciesCK
sleep 10
java Controller Long dfs.datanode.cached-dfsused.check.interval.ms DataNode org.apache.hadoop.hdfs.server.diskbalancer.command.TestDiskBalancerCommand#testDiskBalancerExecuteOptionPlanValidity org.apache.hadoop.hdfs.server.diskbalancer.command.TestDiskBalancerCommand#testPrintFullPathOfPlan org.apache.hadoop.hdfs.server.diskbalancer.command.TestDiskBalancerCommand#testRunMultipleCommandsUnderOneSetup org.apache.hadoop.hdfs.server.diskbalancer.TestDiskBalancer#testDiskBalancerEndToEnd org.apache.hadoop.hdfs.server.diskbalancer.TestDiskBalancer#testDiskBalancerWhenRemovingVolumes
sleep 10
java Controller Long  dfs.datanode.cache.revocation.timeout.ms DataNode org.apache.hadoop.hdfs.server.datanode.TestBlockRecovery#testRaceBetweenReplicaRecoveryAndFinalizeBlock org.apache.hadoop.hdfs.server.datanode.TestBlockRecovery#testRecoverySlowerThanHeartbeat org.apache.hadoop.hdfs.server.datanode.TestBlockRecovery#testRecoveryTimeout org.apache.hadoop.hdfs.server.datanode.TestBlockRecovery#testRecoveryWillIgnoreMinReplication
