# Vault Patcher
语言：**[简体中文](README.md)丨[English](README_en_us.md)丨[中文教程PLUS](README_PLUS.md)**

⚠警告：1.2.10及以下的版本不再受支持！

Fabric版请见：[这里](https://github.com/3093FengMing/VaultPatcher/tree/fabric)

# 工作原理

![VP](https://img1.imgtp.com/2023/07/13/6tV4Ntia.jpg)


# 配置文件

## 配置

在1.2.5以后，配置文件均采用模块的形式
在`config/vaultpatcher/`下的格式为`config.json`和若干个`模块.json`。

`config.json`是必须的。 
如下：
```json
{
  "mods": [
    "模块"
  ],
  "classes":[
    "some.thing.class",
    "another.class"
  ],
  "debug_mode": {
    "is_enable": false,
    "output_mode": 0,
    "output_format": "<source> -> <target>"
  }
}
```
### 模块（mods）
`config.json`定义了`模块.json`，
`模块.json`才会被正常读取读取并使用。反之亦然。

### 调试模式（Debug Mode）
可以在调试和查找文本时使用。

`is_enable`决定调试功能是否开启。开启时会在替换字符串时向日志中输出一行调试信息，调试信息的格式由`output_format`决定，调试信息的内容由`output_mode`决定。
默认为 false。

`output_format`决定了调试信息的格式，默认为`<source> -> <target>`。
允许占位符，支持的占位符如下：
* `<source>`：源内容，即未做替换前的字符串。
* `<target>`：替换后的字符串。
* `<class>`：位于哪个类中替换的。
* `<method>`：替换时所调用的方法。

`output_mode`决定了调试信息的内容。
若为 0, 则仅输出替换的字符串；
若为 1, 则仅输出不被替换的字符串。

### 全局类（classes）
`classes`定义了全局类，这些类在一般情况下都会加入到替换队列中。

除非在模块中定义了`target_class`。写法请见[目标类](#目标类target-class)章节

## 模块

模块的格式大概这样：

```json
[
  {
    "target_class": {
      "name": "me.modid.item.relics",
      "method": ""
    },
    "key": "Dragon Relic",
    "value": "namespace.modify.modid.item.relics.dragonrelic"
  }
]
```
。其中，

```json
{
  "target_class": {
    "name": "me.modid.item.relics",
    "method": ""
  },
  "key": "Dragon Relic",
  "value": "namespace.modify.modid.item.relics.dragonrelic"
}
``` 

就是一个翻译键值对，主要涉及到`key`、`value`和`target_class`。

### 键值对（key-value pair）

#### 键（Key）
`key`，顾名思义，指定的是要翻译的字符串。

如果我想翻译标题界面的``Copyright Mojang AB. Do not distribute!``，
那么可以指定`"key":"你的翻译内容"`。

#### 值（Value）

有了键，还得有值。

那么我想将``Copyright Mojang AB.``改为``Copyright Mojang ABCD.``。
那就可以指定`"value":"Copyright Mojang ABCD."`。

这样一个基础的键值对就完成了。
应该是这样子的：

```json
{
  "target_class": {
    "name": "",
    "method": ""
  },
  "key": "Copyright Mojang AB. Do not distribute!",
  "value": "Mojang AB."
}
```

例如

```json
{
  "target_class": {
    "name": "",
    "method": "",
    "stack_depth": -1
  },
  "key": "Copyright Mojang AB. Do not distribute!",
  "value": "Mojang AB."
}
```


### 目标类（target class）

当然，有了键值对还不够。你还需要`target_class`。

`target_class`这个对象主要用于指定两个相同`key`的不同`value`。
简单解释下：

有一个GUI里面有`Close`（指关闭GUI）这个文本，另一个GUI也有`Close`（指关闭管道），

此时它们的含义不同，但是若不加上`target_class`，那么他们的翻译内容却是一样的。
所以要用到`target_class`。

`target_class`中有三个键：`name`和`method`。

### 类名（name）

即你需要替换的类。注意，该类名必须完整（不含.java或.class）。

### 方法（method）

方法用于更精准的定位类内的文本，可以留空，这样会遍历所有的方法。

### 模板配置

**_（适合初学者）_**

```json
[
  {
    "authors": "作者名字",
    "name": "随便一个名字吧",
    "desc": "随便一个描述吧",
    "mods": "就是这个模组了"
  },
  {
    "target_class": {
      "name": "a.b.c.some.thing.class",
      "method": "justThisMethod"
    },
    "key": "Debug",
    "value": "调试"
  },
  {
    "target_class": {
      "name": "net.minecraft.client.gui.components.DebugScreenOverlay",
      "method": "getGameInformation"
    },
    "key": "Block: %d %d %d [%d %d %d]",
    "value": "方块: %d %d %d [%d %d %d]"
  }
]
```

## 其他

#### 主作者：FengMing([github](https://github.com/3093FengMing)，[爱发电](https://afdian.net/a/fengming3093))

#### 配置部分：teddyxlandlee([github](https://github.com/teddyxlandlee))

#### 想法：yiqv([github](https://github.com/yiqv))

#### Mod地址：[Modrith](https://modrinth.com/mod/vault-patcher)，[Github](https://github.com/3093FengMing/VaultPatcher)，[mcmod](https://www.mcmod.cn/class/8765.html)，[bilibili](https://img.shields.io/badge/bilibili-%E7%AD%89-blue)
