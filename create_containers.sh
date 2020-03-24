#!/bin/bash

num=$1

apt install -y docker.io docker-compose

docker rm -f $(docker ps -aq); docker rmi -f $(docker images -q)
systemctl stop docker
rm -rf /var/lib/docker
mkdir /var/lib/docker
mount --rbind /root/vm_images /var/lib/docker
systemctl start docker
for i in $(seq 0 $num); do docker run -d -it --name hadoop-$i sixiangma/hdfs-reconf-template:v0.7_2020.03.24; done
