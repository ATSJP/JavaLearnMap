# Docker之Nginx



## 一、搭建代理服务器

### 方案一

Nginx正向代理

```shell
 docker run -d -p 8080:8080 -p 9443:9443 -v /root/nginx_home/:/root/nginx_home --name nginx nginx
```

增加nginx server配置

```
server {  
  resolver 114.114.114.114;      
  listen 8080;  
  location / {  
    proxy_pass http://$http_host$request_uri;     
    proxy_set_header HOST $http_host;
    proxy_buffers 256 4k;
    proxy_max_temp_file_size 0k; 
    proxy_connect_timeout 30;
    proxy_send_timeout 60;
    proxy_read_timeout 60;
    proxy_next_upstream error timeout invalid_header http_502;
  }  
} 

server {  
  resolver 114.114.114.114;      
  listen 9443;  
  location / {  
    proxy_pass https://$host$request_uri;   
    proxy_buffers 256 4k;
    proxy_max_temp_file_size 0k; 
    proxy_connect_timeout 30;
    proxy_send_timeout 60;
    proxy_read_timeout 60;
    proxy_next_upstream error timeout invalid_header http_502;
  }  
}

```

nginx默认不支持Https正向代理，增加以下模块即可支持

```
ngx_http_proxy_connect_module 
```

### 方案二

openresty : nginx + lua

















































