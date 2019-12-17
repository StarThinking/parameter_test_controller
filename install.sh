#!/bin/bash

sudo apt-get update
sudo apt-get -y upgrade
sudo apt-get -y install vim

sudo apt-get -y install software-properties-common
sudo apt-get -y install openjdk-8-jdk
sudo apt-get update
sudo apt-get -y install maven
sudo apt-get -y install build-essential autoconf automake libtool cmake zlib1g-dev pkg-config libssl-dev
#
sudo apt-get -y install libprotobuf-java

cd ~
wget https://github.com/protocolbuffers/protobuf/releases/download/v2.5.0/protobuf-2.5.0.tar.gz
tar zxvf protobuf-2.5.0.tar.gz
cd protobuf-2.5.0/
./configure
make
make check
sudo make install
cd ~

# Optional packages
sudo apt-get -y install snappy libsnappy-dev
sudo apt-get -y install bzip2 libbz2-dev
sudo apt-get -y install libjansson-dev
sudo apt-get -y install fuse libfuse-dev
sudo apt-get -y install zstd

sudo ldconfig
#echo "export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64" >> ~/.profile
#echo "export HADOOP_HOME=/root/hadoop-3.1.2-src/hadoop-dist/target/hadoop-3.1.2" >> ~/.profile

#wget https://archive.apache.org/dist/hadoop/common/hadoop-3.1.2/hadoop-3.1.2-src.tar.gz
#tar zxvf hadoop-3.1.2-src.tar.gz

#mvn package -Pdist,native -DskipTests -Dtar
