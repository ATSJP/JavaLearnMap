## Jenkins

### 一、介绍

官方文档：[点此进入](<https://jenkins.io/zh/doc/>)


### 二、安装

#### 1、docker部署

**单节点部署:**

[点此进入](<https://github.com/ATSJP/note/blob/master/Docker/Docker-Jenkins.md>)

**集群部署：**

[点此进入](<https://github.com/ATSJP/note/blob/master/Docker/Docker-Jenkins-cluster.md>)

#### 2、war包部署

1. [下载 Jenkins](http://mirrors.jenkins.io/war-stable/latest/jenkins.war).
2. 打开终端进入到下载目录.
3. 运行命令 `java -jar jenkins.war --httpPort=8080`.
4. 打开浏览器进入链接 `http://localhost:8080`.
5. 按照说明完成安装.

### 三、使用

#### 1.常用插件

Locale：中英文切换

Role-based Authorization Strategy：可以按照角色、用户来管理jenkins。>> [官方文档](https://wiki.jenkins.io/display/JENKINS/Role+Strategy+Plugin)

#### 2.构建项目



### 四、常用示例

#### 1、使用Locale切换语言

**A、中文转English**

- 安装插件：
  系统管理 ／ 插件管理，打开“可选插件”，选择locale 插件，点击“下载待重启后安装“按钮安装。

  勾选“安装完成后重启Jenkins“

- 设置语言
  系统管理 ／ 系统设置，找到Locale

  输入Default Language为en_US

  勾选“Ignore browser preference and force this language to all users“

  保存

**B、English转简体中文**

- 安装插件
  Manage Jenkins / Manage Plugins, 打开Avaiable，选择locale 插件，点击“Download now and install after restart”

  勾选“Restart Jenkins when installation is complete and no jobs are running“

- 设置语言
  Manage Jenkins ／ Configure System找到Locale

  输入Default Language为zh_CN 

  勾选“Ignore browser preference and force this language to all users“

  Save 

### 五、坑记录 



### 六、扩展