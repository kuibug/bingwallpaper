> 以前一直很喜欢必应搜索首页的背景图片，懒癌晚期的我借助API使用Java写一个简单的无界面小程序，配合系统任务计划每天下载更换。

> 后来有同学需要，但是这个操作实在是超出他们的想象……GitHub上大佬们写了不少，借练手目的做了这个有界面的，争取把API全部榨干。

> 程序全部使用Java完成，界面使用swing（so 又不是不能用 ¯\_(ツ)_/¯）  
自动下载功能的平台适配的化主要针对我经常使用的`Windows 10`(系统任务计划) 和 `Ubuntu 18.04`(cron)


> ~~另外安利一下 [https://bing.ioliu.cn/](https://bing.ioliu.cn/) 这个网站收录了大量的必应壁纸，不过仅包含中国版。极简强迫症患者可以试试~~

> 推荐Windows应用商店的 [动态主体](https://www.microsoft.com/store/productId/9NBLGGH1ZBKW) 里面不仅包含了Bing每日壁纸还有Windows聚焦壁纸。

# 注意

1. 该程序是利用微软接口做的下载程序，下载内容(图片)的版权及所有权仍然归其原作者所有， 仅能用于个人桌面壁纸用途！
2. 程序使用纯Java语言开发，如不能运行可以尝试切换Java版本需大于等于1.8

# 使用说明

## 功能

1. 下载壁纸（废话）
2. Swing GUI支持
3. 支持自定义下载路径
4. 支持自定义下载天数
5. 支持国际版
6. 批量获取
7. 部分参数支持持久化 支持的持久化参数`-path` `-mkt` `-pixel` `-name` `coookie`

## 无UI启动

默认的启动类是GUI所以请从`main.java.top.kuibug.bingWallpaper.Core` 类启动  
或者直接下载无UI的版本

参数

| 参数   | 取值                    | 说明         | 备注                                                        |
|--------|-----------------------|--------------|-------------------------------------------------------------|
| -day   | 0 ~ 7                 | 图片发布日期 | 0(今天)1(昨天)2(前天)最多回去到前7天的内容                  |
| -path  | {dir}                 | 图片保存路径 | 默认值 `./Bingwallpaper/`                                   |
| -pixle | `720` / `1080` /`UHD` | 图片分辨率   | 默认值 `1080` (1920x1080)看后期需求添加其他分辨率           |
| -name  | `date` / `link`       | 文件命名方式 | `date` 以图片发布日期命名</br>`link` 从下载链接中解析文件名 |
| -mkt   | `cn` / `us`           | 区域         | `cn` 中国版 </br>`us` 国际版                                |
| -n     | 1 ~ 7                 | 批量获取数量 | 以day往前批量获取                                           |
| -s     |                       | 保存本次传参 |                                                             |

> 国际版目前需要一个本地cookie，cookie获取方式已经更新再文档中，大家可以自己获取，不再负责更新cookie  
> API 仅支持最近七天的获取，但是我们可以通过批量获取的方式突破这个限制😜

## 从UI启动

部分版本Windows会因为编码导致版权信息显示乱码，点击根目录下start.bat启动就不会乱码了

## FQA

### 打开图像预览也无法下载

1. 检查你的网络
2. 检查目录下config.json文件是否存在

### 国际版cookie如何获取

1. 使用chrome firefox edge 等浏览器访问
   [API](https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US) `https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US`
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

3. 将 `ENSEARCH=BENVER=0;` 一项改为 `ENSEARCH=BENVER=1;` 不同浏览器的顺序可能不一样使用文本编辑器查找一下就好了

### 命令模式下重复下载相同文件

这个是为了容错，防止第一次输入参数就直接超出参数范围

### 配置文件config.json

格式

``` json
{
"mkt": "cn",
"path": "./BingWallpaper",
"name": "link",
"pixel": "1080"
"cookie":"cookie value"
"up_key": "keycode value",
"down_key": "keycode value"
}
```

config.json从1.0.3开始变为可选选项，格式中列出的为所有支持的  
不限制顺序 up_key，down_key为新增参数,仅对UI生效

注意：1.0.2没有config.json无法工作

### 自定义按键

自定义按键需要再配置文件中填入keycode，UI中按下按键时，日志会显示对应的keycode

### 系统任务计划注册失败

Windows 自动化均再用批处理的方式完成，若任务计划注册失败请到系统任务计划删除后重新注册即可

### Windows壁纸自动更换

关于壁纸自动更换查阅资料后有几种方法  
第一种：改注册表。(我菜，认怂)  
第二种：将下载图片替换当前的壁纸文件(需要管理员权限)  
第三种：自己手动设置幻灯片切换。
> 我选择了第三种...

> 有其他建议直接在项目 `issues` 写吧，我尽量做到

# 版本预告 TODO

1.Ubuntu 自动化

| 版本   | size  | 说明                                                                                 | 备注                                 |
|--------|-------|--------------------------------------------------------------------------------------|--------------------------------------|
| V0.2.3 | 3kB   | 最初的版本，没有界面，参数仅支持`-day`（因为只有一个参数，所以直接跟上参数值就好了） | 需要有Java环境，熟悉操作系统任务计划 |
| V1.0.2 | 3.3MB | Windows下能自动下载                                                                  | 建议单独安装Java环境                 |
| V1.0.3 | 984KB | 优化程序逻辑，添加两个配置参数，支持批量下载                                         |                                      |
