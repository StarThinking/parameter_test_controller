#!/bin/bash

num=$1

docker rm -f $(docker ps -aq); docker rmi -f $(docker images -q)
systemctl stop docker
rm -rf /var/lib/docker
mkdir /var/lib/docker
mount --rbind /root/vm_images /var/lib/docker
systemctl start docker
for i in $(seq 0 $num); do docker run -d -it --name hadoop-$i sixiangma/hdfs-reconf-template:v0.1_2020.03.22; done
