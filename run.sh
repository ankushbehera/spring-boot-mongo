#!/bin/bash
cd /home/ec2-user/spring-boot-mongo
docker-compose build --no-cache
docker-compose up -d
