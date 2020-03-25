#!/bin/bash

if [ $# -ne 2 ]; then echo wrong; exit -1; fi
num=$1
tag=$2
#apt-get update
#apt-get upgrade
#
#sudo mkfs.ext4 /dev/sdc 
#mkdir /root/vm_images
#sudo mount /dev/sdc  /root/vm_images
#
#apt install -y docker.io docker-compose
#
#docker rm -f $(docker ps -aq); docker rmi -f $(docker images -q)
#systemctl stop docker
#rm -rf /var/lib/docker
#mkdir /var/lib/docker
#mount --rbind /root/vm_images /var/lib/docker
#systemctl start docker
for i in $(seq 0 $num); do docker run -d -it --name hadoop-$i sixiangma/hdfs-reconf-template:$tag ; done
