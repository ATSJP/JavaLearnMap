# GitFlow

## 工作流

> 参考文章：A successful Git branching model：https://nvie.com/posts/a-successful-git-branching-model/

![GitFlow](GitFlow.assets/git-model@2x.png)



## 分支

- Master分支

​	这个分支是最近发布到生产环境的代码、最近发布的Release，这个分支只能从其他分支合并，不能在这个分支直接修改。

- Develop分支

​	这个分支是我们是我们的主开发分支，包含所有要发布到下一个Release的代码，这个主要合并与其他分支，比如Feature分支。

- Feature分支

​	这个分支主要是用来开发一个新的功能，一旦开发完成，我们合并回Develop分支进入下一个Release。

- Release分支

​	当你需要一个发布一个新Release的时候，我们基于Develop分支创建一个Release分支，完成Release后，我们合并到Master和Develop分支。

- Hotfix分支

​	当我们在Production发现新的Bug时候，我们需要创建一个Hotfix, 完成Hotfix后，我们合并回Master和Develop分支，所以Hotfix的改动会进入下一个Release。