# Vanilla Hardcoded Breaker（Vault Patcher）

[简体中文](README.md) [English](README_en_us.md)

### 硬编码->本地化字符串
### Let the hard coded change into localization string in some mods.

## 基础配置
Vanilla Hardcoded Breaker会在`.minecraft\config\vaultpatcher`下生成config.json（下文统称***配置文件***）

配置文件的格式大概是长这样：
```json
[
    {
        "target_class":{
            "name":"",
            "mapping":"SRG",
            "stack_depth":-1
        },
        "key":"I'm key",
        "value":"Im value"
    },
    {
        "target_class":{
            "name":"me.modid.item.relics",
            "mapping":"SRG",
            "stack_depth":3
        },
        "key":"Dragon Relic",
        "value":"namespace.modify.modid.item.relics.dragonrelic"
    },
    {
        "target_class":{
            "name":"",
            "mapping":"SRG",
            "stack_depth":0
        },
        "key":"Talents",
        "value":"namespace.modify.the_vault.gui.talnets"
    }
]
```
其中，
```json
{
    "target_class":{
        "name":"",
        "mapping":"SRG",
        "stack_depth":-1
    },
    "key":"I'm key",
    "value":"Im value"
}
``` 
就是一个翻译键值对，主要涉及到`key`、`value`和`target_class`。

### 键值（key & value）
`key`，顾名思义，指定的是要翻译的字符串。

如果我想翻译标题界面的``Copyright Mojang AB. Do not distribute!``，
那么可以指定`"key":"Copyright Mojang AB. Do not distribute!"`。

有了键，还得有值。

那么我想将``Copyright Mojang AB. Do not distribute!``改为``Mojang AB.``。
那就可以指定`"value":"Mojang AB."`。

这样一个基础的键值对就完成了。
应该是这样子的：
```json
{
    "key":"Copyright Mojang AB. Do not distribute!",
    "value":"Mojang AB."
}
```

然而，光有这点东西是无法使用的。
你还得加上~~虽然基本用不到但是还是要加上的~~``target_class``

例如
```json
{
    "target_class":{
        "name":"",
        "mapping":"SRG",
        "stack_depth":-1
    },
    "key":"Copyright Mojang AB. Do not distribute!",
    "value":"Mojang AB."
}
```

## 高级配置

### 注入目标（target class）

`target_class`这个对象主要用于指定两个相同`key`的不同`value`。
简单解释下：

有一个GUI里面有`Close`（指关闭GUI）这个文本，另一个GUI也有`Close`（指关闭管道），

此时它们的含义不同，但是若不加上`target_class`，那么他们的翻译内容却是一样的。
所以要用到`target_class`。

`target_class`中有三个键：`name`、`mapping`和`stack_depth`。

### 类名（name）
类名的匹配规则大概是这样的： 
* 以`#`开头的字符串会视为模糊匹配（示例：`#TitleScreen`会匹配`net.minecraft.client.gui.screens.TitleScreen`和`net.minecraft.client.gui.screens.titlescreen`
  但不匹配`net.minecraft.client.gui.titlescreen.screens`）
* 不以`#`开头的字符串会视为全匹配（示例：`net.minecraft.client.gui.screens.TitleScreen`会匹配`net.minecraft.client.gui.screens.TitleScreen`和`net.minecraft.client.gui.screens.titlescreen`
  但不匹配`net.minecraft.client.gui.titlescreen.screens`）

### 映射（mapping）
保留字段

### 堆栈深度（stack depth）
堆栈深度在堆栈中用于更精准的匹配类，
例如在如下堆栈中
```
java.base/java.lang.Thread.getStackTrace(Thread.java:1610), 
TRANSFORMER/minecraft@1.18.2/net.minecraft.network.chat.TextComponent.handler$zza000$proxy_init(TextComponent.java:531),
TRANSFORMER/minecraft@1.18.2/net.minecraft.client.gui.screens.TitleScreen(TitleScreen.java:3),
...
```
`net.minecraft.client.gui.screens.TitleScreen`的`stack_depth`就是2。
`stack_depth`的大小取决于要定位的堆栈所处的位置，
使用`stack_depth`时，`name`不能为模糊匹配。

例如：
```json
{
    "target_class":{
        "name":"net.minecraft.client.gui.screens.TitleScreen",
        "mapping":"SRG",
        "stack_depth":2
    },
    "key":"Copyright Mojang AB. Do not distribute!",
    "value":"Mojang AB."
}
```
此时便能精准的定位到`net.minecraft.client.gui.screens.TitleScreen`这个类。

### 参考配置
**_（用于Vault Hunter 3rd Edition）_**
```json
[
    {
        "target_class":{
            "name":"",
            "mapping":"SRG",
            "stack_depth":0
        },
        "key":"Attack Damage",
        "value":"namespace.modify.the_vault.gui.attackdamage"
    },
    {
        "target_class":{
            "name":"",
            "mapping":"SRG",
            "stack_depth":0
        },
        "key":"Dragon Relic",
        "value":"namespace.modify.the_vault.item.relics.dragonrelic"
    },
    {
        "target_class":{
            "name":"",
            "mapping":"SRG",
            "stack_depth":0
        },
        "key":"Talents",
        "value":"namespace.modify.the_vault.gui.talnets"
    }
]
```
如果你仔细看的话，那么你会发现，`target_class`这个键其实很少被使用。

## 其他
#### 主作者：FengMing([github](https://github.com/3093FengMing))
#### 配置部分：teddyxlandlee([github](https://github.com/teddyxlandlee))
#### 想法：yiqv([github](https://github.com/yiqv))
#### Mod地址：[github](https://github.com/3093FengMing/VaultPatcher)，[mcmod](等)，[bilibili](等)