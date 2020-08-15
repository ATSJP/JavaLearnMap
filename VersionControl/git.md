# git

## 一、基本使用


```shell
git commit –amend #修改本地commit的注释

git stash 将当前修改储藏起来

git stash list 显示当前所有储藏记录

git stash pop 恢复当前暂储藏区的内容，并删除储藏区的内容

git stash appy <stash@{0}> 恢复当前储藏区的内容，但是不删除储藏区的内容

git stash drop 删除当前储藏区的内容

git stash list 显示当前储藏区的内容

git reflog 显示历史命令

git reset --hard <head>  回退到指定的head

git checkout -- <fileName>  撤销指定的文件，到最近一次add或者commit的状态

git remote add orgin <remote resp link>  关联仓库到远程仓库中

git push -u orgin master 推送本地内容到远程仓库中，注意-u 针对第一次关联仓库

git checkout -b <分支名>  创建分支  -b表示创建并切换 （相当于下面两条命令）

git branch <分支名>  创建分支
 
git checkout <分支名>  切换分支

git branch -d <分支名>  删除分支

## 回退本地分支版本，并且推送到远程仓库

git reset --hard <head>

git push -f 强制推送本地分支要 远程分支

# 全局配置
git config --global user.name "John Doe"

git config --global http.proxy 'http://127.0.0.1:1080'
git config --global https.proxy 'http://127.0.0.1:1080'

```
## 二、常见问题

**问题一、当本地初始化的库，第一次推送到远程仓库的时候，需要进行设置**

原因：本地库和远程库初始化信息不一致

```shell
git pull origin master --allow-unrelated-histories
```

**问题二：如何恢复已经commit到本地的内容**

```shell
git reset HEAD~
```
**问题三：如何恢复已经commit到本地的内容,并删除commit内容不保留**

```shell
git reset --hard HEAD~
```

**问题四：如何如何合并两个仓库**

假设现在有两个repo：repo1，repo2，现在想把repo2中的更新合并到repo1中，设repo2的URL为 https://github.com/username/repo2
命令如下：

```shell
cd repo1
git checkout master		#假设是往repo1的master分支合并
git remote add repo2 https://github.com/username/repo2
git fetch repo2 master:repo2 
git merge repo2 master
git push 
```
解释：

1. 进入repo1文件夹
2. 切换到master分支（需要合并到哪个分支自选）
3. 添加repo2URL作为repo1的新远程仓库，并命名为repo2
4. 将repo2的master分支获取到repo1并创建分支名为repo2（此处不会切换到repo2分支）
5. 将repo2分支合并到repo1的master分支上
6. 提交更新到repo1的master分支上 



# 配置git push不用每次输入用户名和密码

#### 1.使用ssh协议

- step 1: 生成公钥

```bash
ssh-keygen -t rsa -C "xxxxx@xxxxx.com"  
# Generating public/private rsa key pair...
# 三次回车即可生成 ssh key
```

- step 2: 查看已生成的公钥

```ruby
cat ~/.ssh/id_rsa.pub
# ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC6eNtGpNGwstc....
```

- step3: 复制已生成的公钥添加到git服务器
- step4:
   使用ssh协议clone远程仓库
   or
   如果已经用https协议clone到本地了，那么就重新设置远程仓库

```dart
git remote set-url origin git@xxx.com:xxx/xxx.git
```

#### 2.设置git配置

##### 对于 HTTP 协议 git 拥有一个凭证系统来处理这个事情

- 默认所有都不缓存。 每一次连接都会询问你的用户名和密码。
- "cache" 模式会将凭证存放在内存中一段时间。 密码永远不会被存储在磁盘中，并且在15分钟后从内存中清除。
- "store" 模式会将凭证用明文的形式存放在磁盘中，并且永不过期。 这意味着除非你修改了你在 Git 服务器上的密码，否则你永远不需要再次输入你的凭证信息。 这种方式的缺点是你的密码是用明文的方式存放在你的 home 目录下。
- 如果你使用的是 Mac，Git 还有一种 “osxkeychain” 模式，它会将凭证缓存到你系统用户的钥匙串中。 这种方式将凭证存放在磁盘中，并且永不过期，但是是被加密的，这种加密方式与存放 HTTPS 凭证以及 Safari 的自动填写是相同的。
- 如果你使用的是 Windows，你可以安装一个叫做 “winstore” 的辅助工具。 这和上面说的 “osxkeychain” 十分类似，但是是使用 Windows Credential Store 来控制敏感信息。 可以在 [https://gitcredentialstore.codeplex.com](https://link.jianshu.com?t=https://gitcredentialstore.codeplex.com/)下载。
   你可以设置 Git 的配置来选择上述的一种方式

```csharp
git config --global credential.helper cache
```

部分辅助工具有一些选项。 “store” 模式可以接受一个 --file <path> 参数，可以自定义存放密码的文件路径（默认是`~/.git-credentials`）。 “cache” 模式有 --timeout <seconds> 参数，可以设置后台进程的存活时间（默认是 “900”，也就是 15 分钟）。 下面是一个配置 “store” 模式自定义路径的例子：

```csharp
git config --global credential.helper store --file ~/.my-credentials
```

Git 甚至允许你配置多个辅助工具。 当查找特定服务器的凭证时，Git 会按顺序查询，并且在找到第一个回答时停止查询。 当保存凭证时，Git 会将用户名和密码发送给 所有 配置列表中的辅助工具，它们会按自己的方式处理用户名和密码。 如果你在闪存上有一个凭证文件，但又希望在该闪存被拔出的情况下使用内存缓存来保存用户名密码，.gitconfig 配置文件如下：

```csharp
[credential]
    helper = store --file /mnt/thumbdrive/.git-credentials
    helper = cache --timeout 30000
```

#### 3.修改git配置文件

在用户文件夹下找到 `.gitconfig`文件,用编辑器或者vim打开，如果之前有配置过用户名和密码就会在里面看到

```csharp
[user]
    name = xxx
    email = xxx@xxxxx.com
```

在后面追加如下配置并保存

```csharp
[credential]
     helper=store
```

下次执行`git push`再次输入用户名之后，git就会记住用户名密码并在上述目录下创建.git-credentials文件，记录的就是输入的用户名密码。





