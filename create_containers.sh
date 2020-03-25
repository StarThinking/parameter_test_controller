#!/bin/bash

num=19

apt-get update
apt-get upgrade

sudo mkfs.ext4 /dev/sda4 
mkdir /root/vm_images
sudo mount /dev/sda4  /root/vm_images

apt install -y docker.io docker-compose

docker rm -f $(docker ps -aq); docker rmi -f $(docker images -q)
systemctl stop docker
rm -rf /var/lib/docker
mkdir /var/lib/docker
mount --rbind /root/vm_images /var/lib/docker
systemctl start docker
for i in $(seq 0 $num); do docker run -d -it --name hadoop-$i sixiangma/hdfs-reconf-template:v0.7_2020.03.24; done
