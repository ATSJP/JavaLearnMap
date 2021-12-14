[TOC]

# Upsource

## Docker-Compose

```yml
version: "3.1"

networks:
  upsource-network:
    driver: bridge
    ipam:
      driver: default
      config:
        - subnet: 172.100.0.0/16

volumes:
  upsource-data:
  upsource-conf:
  upsource-logs:
  upsource-backups:

services:
  upsource:
    image: jetbrains/upsource:2020.1.1970
    container_name: upsource-server-instance
    networks:
      upsource-network:
        ipv4_address: 172.100.0.10
    ports:
      - 8080:8080
    # user: root
    volumes:
      - upsource-data:/opt/upsource/data
      - upsource-conf:/opt/upsource/conf 
      - upsource-logs:/opt/upsource/logs
      - upsource-backups:/opt/upsource/backups

  web:
    image: nginx:1.21.5
    container_name: nginx
    networks:
      - upsource-network
    ports:
      - 80:80
    extra_hosts:
      - upsource.server.ip:192.168.56.80
    volumes:
      - ./conf.d:/etc/nginx/conf.d:ro
```

## Nginx

```nginx
server {
    listen 80;
    server_name upsource.xyz.cn;

    location / {
        proxy_set_header X-Forwarded-Host $http_host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;

        # to proxy WebSockets in nginx
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass http://upsource.server.ip:8080;
        proxy_pass_header Sec-Websocket-Extensions;
    }

}

# server {
#     listen 443 ssl;

#     ssl_certificate <path_to_certificate>;
#     ssl_certificate_key <path_to_key>;

#     server_name localhost;

#     location / {
#         proxy_set_header X-Forwarded-Host $http_host;
#         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
#         proxy_set_header X-Forwarded-Proto $scheme;
#         proxy_http_version 1.1;

#         # to proxy WebSockets in nginx
#         proxy_set_header Upgrade $http_upgrade;
#         proxy_set_header Connection "upgrade";
#         proxy_pass http://upsourcemachine.domain.local:1111/;
#         proxy_pass_header Sec-Websocket-Extensions;
#     }
# }
```
