[TOC]

# ssh

**生成RSA密钥对**

```shell
ssh-keygen -t rsa -C "shijianpeng@test.cn" -b 4096
```


# Openssl

```shell
openssl genrsa -out rsa_private_key.pem 2048

openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key_2048.pub

openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt > rsa_private_key_pkcs8.pem
```

