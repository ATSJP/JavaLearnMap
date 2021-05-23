[TOC]



# 踏上Mac之路

> 时间：2021.10.30
>
> 系统：Monterey
>
> 版本：12
>
> 说明：
>
> -   说在最前面，所有配置均涉及到路径，各自可以直接选择和博主放在一个地方，也可以自己改地方，全文采用`${your_path}`代替大家选择的根路径，涉及到根路径的地方，博主会上方写上博主的地址。

## 小白篇
### 基本操作

#### 快捷键

[Apple官方](https://support.apple.com/zh-cn/HT201236)，官方快捷键介绍挺详细的了，一开始可以先看一遍，留个印象，不然都不知道Apple到底有多少快捷方式，反正我是惊呆了，再看看Windows，哎，算了算了。

#### 终端

##### 打开App

大家知道，Mac的Finder用起来对于Windows重度使用者，太难受了，找个系统文件，门都没有（当然了，还有有其他方式直达目标文件的，比如桌面下使用command+shift+g，不过这个又不是Finder的功能）。Finder的无奈，导致大部分时间宁可使用终端，但是呢终端找到了目标，又想用某些软件打开，怎么办呢？

解决方案：

第一种：使用命令`open ${path}`，这样一个Path路径的Finder窗口就打开了，最后再去用软件打开，显然此方法不够优雅。

第二种：使用命令`${app_path} ${file_path}`，哦豁，举个例子，`/Applications/Sublime\ Text.app/Contents/SharedSupport/bin/subl settings.xml`，这样就可以使用Sublime
Text打开settings.xml文件啦，但是每次这么用，这路径也太长了吧。咋办呢？详情见Unix#Alias使用。

### 软件

#### 截图软件

##### IShot

免费，不仅有日常截图功能外，还支持轻度图片处理，值得一试。

![IShot](踏上Mac之路.assets/IShot.png)

#### 下载软件

##### Downie4

可以下载99%以上视频网站的视频，只需要复制网址，然后粘贴到软件里即可。可惜是收费的，不过价格足够诱惑，50元左右（去数码荔枝买），具体看优惠活动。

![Downie4](踏上Mac之路.assets/Downie4.png)

## 技术篇

### Jdk

不用说啦，支持M1的Jdk版本也就[zulujdk](https://www.azul.com/downloads/?package=jdk)了吧

![zulujdk](踏上Mac之路.assets/zulujdk.png)

#### 环境变量

环境变量文件配置在 \~/.zprofile 即可，最后别忘了`source ~/.zprofile`。

> 有一件事大家得知道，环境变量文件不是所有Mac系统都是一样的，环境变量文件名称取决于Shell，何为Shell，这里就不赘述了，大家自行百度。常见的Shell有：bash、zsh、oh-my-zsh。通常他们的环境变量文件如下：
>
>   Shell       环境变量文件
>   ----------- ---------------
>   bash        .bash_profile
>   Zsh         .zprofile
>   Oh-my-zsh   .zshrc

``` properties
# Java
# ${your_path} = /Library/Java/JavaVirtualMachines
export JAVA_HOME=${your_path}/zulu_jdk8/zulu-8.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.
```

懒人脚本：

``` shell
echo "\n# Java" >> ~/.zprofile
# ${your_path} = /Library/Java/JavaVirtualMachines
echo "export JAVA_HOME=${your_path}/zulu_jdk8/zulu-8.jdk/Contents/Home" >> ~/.zprofile
echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.zprofile
echo "export CLASSPATH=." >> ~/.zprofile

source ~/.zprofile
```

#### 验证

以下命令全部输出OK，即可

``` shell
java -version
javac
java
```

### Jprofiler

Jprogiler对于Java开发者，作用无须多言，建议各位从官方下载：[官方](https://www.ej-technologies.com/products/jprofiler/overview.html)



![Jprofiler](踏上Mac之路.assets/Jprofiler_Download.png)

目前Jprofiler12的Mac版本，已经同时支持了Intel和M1的架构，所以我们下载直接安装即可，如果只想玩一玩的话，10天的试用时间足够你玩的了，后面这一段话，就与你无关了。



![Jprofiler](踏上Mac之路.assets/Jprofiler_12.png)



除了试用，软件还支持买断，标准版$499。😮‍💨博主比较穷，又得知大家都爱学习，好像发现了一个可以无限重制试用时间的方法。大家都知道10天试用期都是根据你的安装时间算的，只要你重新安装，就永远都还有10天，不是吗，除非Jprofiler联网记住你的mac地址，对于后者，我不太信Jprofiler会干这么个事，所以我相信前者。但是博主啊，你不会是每次到期，重新安装Jprofiler吧，这谁都可以想出来呀，当然不是啦，博主发现每次删除`~/.jprofiler12`文件，重新打开Jprofiler之后，都会触发试用/激活码这个步骤，这说明什么？Jprofiler认为你是第一次重新安装。哦豁，是不是很神奇，似乎可以这么永久试用下去。如果这个猜想成立，博主会写个重制脚本，最好能写出PDRunner那样的启动软件（只存在期待中，因为博主不会IOS编程，哈哈），那么就等个10天，博主来验证这个猜想吧。



**最后的最后**，不管猜想是否成立，请各位大力支持正版，以上任何方案仅供学习。

### Maven

去哪里下载？官方入口直接下载：https://maven.apache.org/download.cgi

![maven](踏上Mac之路.assets/maven.png)

#### 环境变量

目前环境变量文件配置在 \~/.zprofile
即可，最后别忘了`source ~/.zprofile`。

``` properties
# Maven
# ${your_path} = /Library
export MAVEN_HOME=${your_path}/Maven
export PATH=$MAVEN_HOME/bin:$PATH
```

懒人脚本：

``` shell
echo "\n# Maven" >> ~/.zprofile
# ${your_path} = /Library
echo "export MAVEN_HOME=${your_path}/Maven" >> ~/.zprofile
echo "export PATH=\$MAVEN_HOME/bin:\$PATH" >> ~/.zprofile

source ~/.zprofile
```

#### 验证

以下命令全部输出OK，即可

``` shell
mvn -v
```

#### 附件

##### Settings.xml

下面是阿里云镜像+Maven中央仓库的配置方式

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

  	<!-- 改成你想放的位置 -->
    <localRepository>/repo</localRepository>

    <pluginGroups>
    </pluginGroups>

    <proxies>
    </proxies>

    <servers>
    </servers>

    <mirrors>

        <mirror>
          <id>aliyunmaven</id>
          <mirrorOf>*</mirrorOf>
          <name>阿里云公共仓库</name>
          <url>https://maven.aliyun.com/repository/public</url>
        </mirror>

        <mirror>
          <id>central</id>
          <mirrorOf>central</mirrorOf>
          <name>Maven Repository</name>
          <url>https://repo1.maven.org/maven2/</url>
        </mirror>

    </mirrors>

    <profiles>
    </profiles>

</settings>
```

### Git

Mac M1 最新系统自带Git，版本还不低，我就不重复装了。

### Docker

Mac上安装Docker方式各式各样，这边仅仅介绍一下Docker官网介绍的方式：https://docs.docker.com/desktop/mac/install/。

![docker](踏上Mac之路.assets/docker.png)

下载下来是个dmg文件，直接无脑安装即可。

#### 如何启动

正常打开应用即可，应用会启动DockerEngie，然后就可以直接在终端或应用里进行操作了。但是显然没有Linux下的`service或systemctl`来的快，所以Mac上还有一个工具可以等效他们------`launchctl`，可以配置Docker的注册表，然后直接使用`launchctl`打开Docker，网上大多有教程，我这里不在赘述，至少目前对于我打开个应用的时间还是有的，实在不行，设置开机就启动Docker。

### IDE

#### Idea

选择了Idea社区版本，为啥呢，因为大部分情况够用，但是如果你选择安装旗舰版，Windows上的Idea激活方式，仍然适合Mac。比如无限重制试用时间的插件。

#### Sublime Text

用过的都说好用，至少我觉得超级好看。[Macwk](https://www.macwk.com/soft/sublime-text)上有现成的破解版，据说挺干净的（不会偷偷摸摸干坏事），但是这傻货的安装包，Mac不认，非得给安装包开启一堆权限才可以。最终我选择临时安装了[Sublime Text](https://www.sublimetext.com/)官方版（好像可以一直试用，期间也不会提示你购买等等），先用着吧，后面在想法子破解。哦，最后的最后，Windows上之前都是利用修改16进制代码改的，我试过了，Mac的16进制代码不同于Windows，网上暂时也没有大神指出来怎么搞，我就不瞎折腾了。

放个官方的图，让你感受一下

![sublimetext](踏上Mac之路.assets/sublimetext.png)

### Unix

再来看看一些硬核的吧

#### Brew

通常基于Linux的Ubuntu、Centos都有自带的软件安装神器，分别是apt-get、yum，Mac也不例外，只是需要自己安装一下，她叫Brew。怎么安装？[官方](https://brew.sh/)

#### Vim

##### Theme

###### 下载安装

``` bash
git clone https://github.com/tomasr/molokai.git
mkdir -p ~/.vim/colors
mv molokai/colors/molokai.vim ~/.vim/colors/
cp /usr/share/vim/vimrc ~/.vimrc
vim ~/.vimrc
```

###### 配置

将下面几行添加到\~/.vimrc配置文件中即可

``` bash
set nu
syntax enable
set background=dark
colorscheme molokai
```

###### 效果

![vim](踏上Mac之路.assets/vim.png)

#### ITerm2

还没折腾，看看别人的效果吧（大致就是Oh-My-Zsh主题+字体+各种配色方案，本人讲究大道至简，搞点配色方案护护眼得了，排查BUG的时候，你还有心情看这玩意？）：

![img](踏上Mac之路.assets/item2.png)

#### Osx Terminal

###### Theme

打开偏好设置，即可在描述文件中配置不同的配色方案。



<img src="踏上Mac之路.assets/terminal.png" alt="img"  />



我们可以选择自己配置，也可以选择别人的方案，下面安利一个项目：[iTerm2-Color-Schemes](https://github.com/mbadolato/iTerm2-Color-Schemes)，下载好项目后，在项目路径下/terminal/存在大量的配置方法，其中文件为\*.terminal的文件，我们可以直接在Mac下打开，就可以预览（另外，在打开的同时，偏好设置中的描述文件已经有了你打开过的配色方案，惊不惊喜），然后挑选一款适合自己的，在偏好设置-描述文件中设置为默认即可。

#### Alias

别名，顾名思义，就是给一个Name重新起个名字，那这玩意就是起个名字吗？当然不是，这玩意配合其他功能一起玩会非常Happy

##### 终端

1.  如何在终端快速使用XXApp打开XX文件

    举个例子：

     App：Sublime Text

     文件： settings.xml

    正常使用：

    ``` shell
    /Applications/Sublime\ Text.app/Contents/SharedSupport/bin/subl settings.xml 
    ```

    别名使用：

    在\~/.zprofile文件中加入下面的别名定义，然后`source ~/.zprofile`。

    ``` properties
    alias sublime="/Applications/Sublime\ Text.app/Contents/SharedSupport/bin/subl"
    ```

    配置完成之后，我们使用以下命令直接打开：

    ``` shell
    sublime settings.xml
    ```

### NodeJs

Tips：说在最前面，由于M1架构的特殊性，所以常规去NodeJs官网找包，手动安装的方式，比较麻烦，因为你必须要找到正确的Arm架构的包，不然就得转义，然而所有转义都是有代价的，会有一定的性能损耗，各位看官看看怎么选择？A、直装Arm架构的包 B、官网x86转义 C、使用第三方装软件的助手帮忙。博主选择C。



另外由于NodeJs版本的更新换代十分的快速，有些老项目必须使用低版本的Node，这样本地需要不停的切换不同版本的NodeJs，麻烦且容易错误，故Nvm出现了，所以我们首选Nvm来管理NodeJs，而不是直接安装NodeJs。

#### Nvm

##### 安装

Mac上Brew是个好东西，所以直接用Brew来上手安装，避免遇到包不对的情况，还得转义，下面动手开干。

打开终端，执行`	brew install nvm`: 

![nvm_install](踏上Mac之路.assets/nvm_install.png)



##### 环境变量

如果得到以上图中的执行结果，基本上是大功告成了，下面按照Brew的提示，继续完成环境变量配置，这边注意一下，虽然提示是修改`.zshrc`文件，但是咱么也不用着急直接照搬，看下下面的引言：

> 有一件事大家得知道，环境变量文件不是所有Mac系统都是一样的，环境变量文件名称取决于Shell，何为Shell，这里就不赘述了，大家自行百度。常见的Shell有：bash、zsh、oh-my-zsh。通常他们的环境变量文件如下：
>
> Shell       环境变量文件
>
> ----------- ---------------
>
> bash        .bash_profile
> Zsh         .zprofile
> Oh-my-zsh   .zshrc

所以根据Brew的提示，大概是让我们去配置环境变量，那环境变量文件是哪一个完全取决于，我们使用的是什么Shell，因为博主是新Mac，默认的终端是用的zsh，所以咱么直接配置`.zprofile`文件即可，开干：

第一步：`mkdir ~/.nvm`

第二步（如果本地有则无需创建啦）：`mkdir ~/.zprofile`

第三步：`vim ~/.zprofile`

然后输入下面的配置

```shell
  export NVM_DIR="$HOME/.nvm"
  # This loads nvm
  [ -s "/opt/homebrew/opt/nvm/nvm.sh" ] && . "/opt/homebrew/opt/nvm/nvm.sh"  
  # This loads nvm bash_completion
  [ -s "/opt/homebrew/opt/nvm/etc/bash_completion.d/nvm" ] && . "/opt/homebrew/opt/nvm/etc/bash_completion.d/nvm"  
```

第四步：`source ~/.zprofile`

##### 验证

![nvm_v](踏上Mac之路.assets/nvm_v.png)

#### NodeJs

##### 安装

执行命令：`nvm install node`，默认安装最新的版本，更改`Node`为版本号既可以指定版本：`nvm use 16.13.0`，`nvm install 16.13.0`



![install node](踏上Mac之路.assets/node_install.png)

##### 验证

![node_v](踏上Mac之路.assets/node_v.png)





