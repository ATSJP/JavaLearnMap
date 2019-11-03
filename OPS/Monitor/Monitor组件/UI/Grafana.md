# Grafana

## 一、前言



## 二、部署

A、直接安装

B、Docker安装

1、命令安装

```bash
docker run \
  -d \
  -p 3000:3000 \
  --name=grafana \
  grafana/grafana
```

2、docker-compose安装

```yml
version: "3"

services: 
  # 监控UI
  grafana:
    image: grafana/grafana:6.4.3
    container_name: grafana
    restart: always
    ports:
      - "3000:3000"
```

