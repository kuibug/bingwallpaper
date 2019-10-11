>以前一直很喜欢必应搜索首页的背景图片，懒癌晚期的我使用API写一个简单的无界面小程序，配合系统任务计划使他每天下载更换。  

>后来有同学需要，但是这个操作实在是超出他们的想象……GitHub上大佬们写了不少，但想把API全部榨干，借练手目的做了这个有界面的。  

>程序全部使用Java完成，界面使用swing（swing太臃肿，但是我也不会其他的界面啊🙃）  
自动功能的平台适配的化主要针对我经常使用的`Windows 10` 和 `Ubuntu 18.04`

>我不会设计界面，也知道界面极度丑陋，除非有人愿意帮我设计界面，否则绝不修改！！！  
>开机自启和后台运行这两件极其消耗系统资源的事情绝对不会加！！！

>另外安利一下 [https://bing.ioliu.cn/](https://bing.ioliu.cn/) 这个网站收录了大量的必应壁纸，不过仅包含中国版。极简强迫症患者可以试试


# 声明

1. 该程序为利用微软接口做的下载程序，下载内容(图片)的版权及所有权仍然归其原作者所有， 仅能用于个人桌面壁纸用途！
2. 程序使用Java语言开发，如不能运行可以尝试切换为Jre 1.8下运行
3. 目前在准备考试，可能提交会巨慢，还可能会断片儿……有问题 `issues` 见

# 使用说明

## 功能

1. 下载壁纸（废话）
2. Swing GUI支持
3. 支持自定义下载路径
4. 支持自定义下载天数
5. 支持国际版
6. 批量获取（文档待更新）
7. 部分参数支持持久化
支持的持久化参数`-path` `-mkt` `-pixel` `-name` `coookie` 

## 无UI启动
默认的启动类是GUI所以请从`top.zewenchen.bingWallpaper.Bing.java` 类启动  
或者直接下载无UI的版本

参数

参数|取值|说明|备注
-|-|-|-
-day|-1 ~ 7|图片发布日期|-1(明天)0(今天)1(昨天)2(前天)最多回去到前7天的内容（**目前-1好像失效了**）
-path|{dir}|图片保存路径 |默认值 `./Bingwallpaper/`
-pixle|`720` / `1080`|图片分辨率|默认值 `1080` (1920x1080)看后期需求添加其他分辨率
-name|`date` / `link`| 文件命名方式|`date` 以图片发布日期命名</br>`link` 从下载链接中解析文件名
-mkt|`cn` / `us`|区域|`cn` 中国版 </br>`us` 国际版
-n|1 ~ 8|批量获取数量|以day往前批量获取

>国际版目前需要一个本地cookie，cookie获取方式已经更新再文档中，大家可以自己获取，不再负责更新cookie  
>API 仅支持最近七天的获取，但是我们可以通过批量获取的方式突破这个限制😜

``` txt
目前对UI配置持久化做出了妥协，性能上比上一版略微弱了一些  
用桌面版操作系统应该不在意这点性能消耗，有强迫症需要我把UI界面删除单独做了一个包
```

## 从UI启动

部分版本Windows会因为编码导致版权显示乱码，点击根目录下start.bat启动就不会乱码了

## FQA

### 打开图像预览也无法下载

1. 检查你的网络
2. 检查目录下config.json文件是否存在

### 国际版cookie如何获取
1. 使用chrome firefox edge 等浏览器访问(IE暂时没找到方法) [API](https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US) `https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US`
2. F12 审查选择network ，再header或者cookie选项卡中找到cookie信息copy下来  

FireFox cookie结构如下

``` cookie
SRCHD=AF=MOZLBR; 
SRCHUID=V=2&GUID=5FCxxxxxFD33E1&dmnchg=1;
SRCHUSR=DOB=20181212&T=1569819713000; 
_EDGE_V=1;
MUID=03Dxxxxxxxxxxxx22; 
MUIDB=03Dxxxxxxxxxxxxx22; 
SRCHHPGUSR=CW=1920&CH=1096&DPR=1&UTC=480&WTS=63705416513&NSB=1; 
ENSEARCH=BENVER=0; 
ULC=P=A293|1: 1&H=A293|1: 1&T=A293|1: 1;
_SS=SID=365BxxxxxxxxxxxxE66FE;
_EDGE_S=SID=36xxxxxxxxxxxxxE66FE

```

3. 将 `ENSEARCH=BENVER=0;` 一项改为 `ENSEARCH=BENVER=0;` 不同浏览器的顺序可能不一样使用文本编辑器查找一下就好了


### 命令模式下重复下载覆盖相同文件
这个是为了容错，防止第一次输入参数就直接超出参数范围

config.json格式

``` json
{
"mkt": "cn",
"path": "./BingWallpaper",
"name": "link",
"pixel": "1080"
"cookie":"cookie value"
}
```

### 批量获取
目前只能通过命令行传参 `-n` 完成

### 系统任务计划注册失败
Windows 自动化均再用批处理的方式完成，若任务计划注册失败请到系统任务计划删除后重新注册即可

>有其他建议直接在项目 `issues` 写吧，我尽量做到

# 版本预告 TODO

1. 添加日志功能
2. 启动时进行必要的检查，例如现在FQA的两个问题缺失只能手动解决，而没有提示
3. 对启动速度进行优化
4. 批量获取是解析json为map，利用多线程下载
5. Ubuntu 自动化


版本|size|说明|适合人群
-|-|-|-
V0.2.3 | 3kB|最初的版本，没有界面，参数仅支持`-day`（因为只有一个参数，所以直接跟上参数值就好了）|有Java环境，熟悉操作系统任务计划
V1.0.2 Alpha||开发中……|有Java环境的Windows大众，Debian系列用户
V1.0.2||正式版，敬请期待|All

# LICENSE
中间使用了阿里的fastjson，代码就仅供交流和个人使用了

# change log
支持国际版
添加Windows自动任务
cookie获取更新
添加批量获取