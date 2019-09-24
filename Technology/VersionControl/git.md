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