# Vault Patcher

## 语言：**[简体中文](README.md)丨[English](README_en_us.md)丨[中文教程PLUS](README_PLUS.md)**

## 相关链接

[![modrinth-VP](https://img.shields.io/badge/modrinth-Vault%20Patcher-green)](https://modrinth.com/mod/vault-patcher/versions)
[![Hardcode Patcher](https://img.shields.io/badge/github-Hardcode%20Patcher-F16436)](https://github.com/LocalizedMC/HardcodePatcher)
[![mcmod-VP](https://img.shields.io/badge/mcmod-Vault%20Patcher-blue)](https://www.mcmod.cn/class/8765.html)
[![mcmod-HP](https://img.shields.io/badge/mcmod-Hardcode%20Patcher-blue)](https://www.mcmod.cn/class/9315.html)
[![VPtool](https://img.shields.io/badge/github-VPtool-blue)](https://github.com/KlparetlR/Vault-Patcher-Grocery-Store/tree/main/VPtool%E7%BC%96%E5%86%99%E5%B7%A5%E5%85%B7)

[![114514](https://img.shields.io/badge/github-%E6%A8%A1%E7%BB%84%E5%88%B6%E4%BD%9C%E4%B8%BB%E9%A1%B5-gold)](https://github.com/3093FengMing)
[![a](https://img.shields.io/badge/github-%E6%9C%AC%E6%95%99%E7%A8%8B%E4%BD%9C%E8%80%85%E4%B8%BB%E9%A1%B5-gold)](https://github.com/KlparetlR)
[![b](https://img.shields.io/badge/afdian-%E6%A8%A1%E7%BB%84%E4%BD%9C%E8%80%85%E4%B8%BB%E9%A1%B5-purple)](https://afdian.net/a/fengming3093)
[![c](https://img.shields.io/badge/QQ-%E6%A8%A1%E7%BB%84QQ%E4%BA%A4%E6%B5%81%E7%BE%A4-aqua)](https://jq.qq.com/?_wv=1027&k=3Slm2Zso)

[Vault-Patcher-Grocery-Store](https://github.com/KlparetlR/Vault-Patcher-Grocery-Store)本教程作者创建的一个收集库，接收中文简体，欢迎提pr，QAQ

## ⚠警告：此教程对标 1.2.10 版本

客户端和服务端都可以安装，看配置内容针对什么

## 模组可用指令

以`/vaultpatcher`或`/vp`为基础，共有3个可用的命令或参数，分别为：`export`、`list`、`reload`。

* `/vp export`会向`.Minecraft`根目录输出一个叫`langpacther.json`的文件，里面存放的是你已经加载的文本内容（并不代表加载的模组和游戏的全部文本，将鼠标放在模组物品上，这个物品的名字和描述将全部记录）
该指令与`optimize_params`功能有关
* `/vp list`在聊天栏列举加载的`模块.json`，鼠标放在绿色字符串上会显示相关信息
* `/vp reload`重新加载资源包内容

## 提取`key`

## 提取所需工具及环境

* 反编译工具：IDEA的java-decompiler 或 [jadx](https://github.com/skylot/jadx)

* 文本编辑器（推荐VSC，能批量查文件夹的所以内容）

* 配置编写工具（可选的）：[VPtool](https://github.com/KlparetlR/Vault-Patcher-Grocery-Store/tree/main/VPtool%E7%BC%96%E5%86%99%E5%B7%A5%E5%85%B7)

* 环境：JDK17或JDK8

## 选择模组和提取硬编码内容

### 第一步：如何选择、确认、挑选模组

一般的，主要瞄准模组物品的物品描述（即tooltip的那块），如果GUI内容很多的话也可以作为提取范围

然后，你要保证这个模组的lang内容是全部汉化的，可以防止你在提取不到这个内容，结果发现在lang文件中

### 第二步：反编译并提取模组的硬编码

### 首先准备工具的相关配置

#### java-decompiler

IDEA的`java-decompiler.jar`文件可以在这里的[网盘链接](https://www.123pan.com/s/MMQ9-1tnzv.html)下载,提取码:vpvp

如果链接挂了，就要你**安装jdk17**后，再**安装IDEA**可以得到（IDEA其实在这里就起一个下载的作用，也就是说你要下载一个2.65 GB大小的软件，就为了一个 340 kb的文件）

它是作为IDEA的捆绑包存放在目录`IntelliJ IDEA Community Edition 202X.X.X\plugins\java-decompiler\lib\`中

想要调用它来为你反编译`.jar`文件，需要你写一个`.bat`文件（文件**编码必须是**`ANSI`），**并提前把`输出文件夹`创建好**，模板及示范如下：

```batch
java -cp "<java-decompiler.jar的地址>" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler -dgs=true "<模组.jar的地址>" "<输出文件夹地址>"
java -cp "D:\java-decompiler\lib\java-decompiler.jar" org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler -dgs=true "D:\.gz\Minecraft\DawnCraft-Tweaks-1.18.2-1.2.1i.jar" "D:\.gz\Minecraft\jd-output"
```

#### [jadx](https://github.com/skylot/jadx)

这里直接放一个[教程链接](https://blog.csdn.net/weixin_39142112/article/details/80356244)，自己看着搞

PS：IDEA本身也能反编译，但它限制你只能一个一个弄，还不能全局查找内容，所以这个办法被抛弃

#### 其他的方式（各有优点）

* **拍照识字**，可以快速提取内容，但要确认大小写以及其他的问题

* **[/vp report](https://github.com/3093FengMing/VaultPatcher/blob/master/README_PLUS.md#模组可用指令)**,可以批量获取你要提取的内容，只要你用鼠标加载了它

* **手打**，没什么好说的...

### 反编译

在`mods`文件夹中找到你选定要汉化的模组 ~~（别告诉我你找不到，就这点技术力最好别学这个）~~

接着复制它，贴贴到一个你**精心挑选的**地址，作为`模组.jar的地址`。

java-decompiler的用户，创建`.bat`文件，然后将你找到的`java-decompiler.jar的地址`、`模组.jar的地址`、`输出文件夹地址`这三个参数填入模板，最后运行文件，按`Enter`键来开始和暂停反编译。

jadx的用户，**直接把.jar文件丢**进去反编译

### 加载及提取

#### 加载

jadx的用户**则要找**`.class`后缀文件，就可以反编译查看内容了（不用vsc）

java-decompiler的用户，打开`模组.jar`文件，找到其中放有大量`.java`文件的文件夹（最好能看见与模组名相关的.java文件，比如模组名：XPCoins，找到`XPCoins.java`文件所在的目录）

接着**返回上级目录**到**第一层目录**（就是你刚打开这个.jar文件的样子，方便提取内容根），把放着那些文件的文件夹解压出来，然后用VSC`打开文件夹`，你就可以用VSC功能栏的第二个`搜索功能`，进行全局内容搜索

比如`XXX\XXX.jar\com\coldspell\xpcoins`，这里要把`com`文件夹解压出来

现在，这两种工具已成功加载内容，就要开始提取了

#### 提取

可能需要汉化的文本类型都是字符串，它们是用`" "`包起来的（不一定用它包起来的都是要汉化的），并且文本颜色在文本编辑器中也辨别度很高

知识点1：TextFormatting.颜色 + "内容"。比如`TextFormatting.GRAY`这类颜色代码要转码为`§7`。

但是，有些不能转，即使从意思上是颜色，如：`.m_130940_(ChatFormatting.YELLOW)`等

这里有转码相关的[链接](https://wiki.biligame.com/mc/%E6%A0%BC%E5%BC%8F%E5%8C%96%E4%BB%A3%E7%A0%81)

知识点2：内容大量带`__`和`.`的都不用提取

**setRegistryName()** 注册名 该模组在游戏里的名字 不是你看的

**StringTextComponent()** 字符串文本组件 为该物品创建展示文字 这就是我们需要汉化的

```java
#需要汉化
msg1 = TextFormatting.GRAY + "You need to wager an XP Coin in your Off Hand to play!";
return new TextComponent("Loot Statue Options");
player.m_6352_((new TextComponent("Bronze is no longer used to reroll bounties. Removed all existing bronze from your bounty table and placed in your inventory or dropped if your inventory is full.")).m_130940_(ChatFormatting.YELLOW), player.m_142081_());
list.add(new StringTextComponent("§7Sonic The Hedgehog"));
#不需要汉化
this.setRegistryName("cartridge_sonic_1");
public static BasicItem VAULT_ROCK = new BasicItem(VaultMod.id("vault_rock"));
CRYSTAL_SHARD_BENEVOLENT = new CrystalShardItem(VaultMod.id("shard_benevolent"), VAULT_MOD_GROUP, new MutableComponent[]{new TranslatableComponent("tooltip.the_vault.shard_benevolent")});
```

将`" "`包起来的内容**作为一个**`key`:

```txt
§7You need to wager an XP Coin in your Off Hand to play!
Loot Statue Options
Bronze is no longer used to reroll bounties. Removed all existing bronze from your bounty table and placed in your inventory or dropped if your inventory is full.
§7Sonic The Hedgehog
```

这就完成了提取工作（这块有很多要摸索，欢迎进群讨论( • ̀ω•́ )✧）

## 编写配置文件

### 这里有一个用Python写的工具，可以使用，已经写了教程，可以与这篇教程对照，配置编写工具[VPtool](https://github.com/KlparetlR/Vault-Patcher-Grocery-Store/tree/main/VPtool%E7%BC%96%E5%86%99%E5%B7%A5%E5%85%B7)

## 配置（`config.json`）

在1.2.5以后，配置文件均采用模块加载的形式

在`config/vaultpatcher/`下的格式为`config.json`和若干个`模块.json`。

文件示范如下：

```json
{
  "mods": [
    "模块1",
    "模块2"
  ],
  "debug_mode": {
    "is_enable": false,
    "test_mode": false,
    "output_mode": 0,
    "output_format": "<source> -> <target>"
  },
  "optimize_params": {
    "disable_export": true,
    "disable_stacks": true,
    "stack_min": -1,
    "stack_max": -1
  }
}
```

### 模块（mods）

`config.json`要定义了`模块.json`，

`模块.json`才会被正常读取读取并使用。反之亦然。

模块可以用模组名命名，反正没限制

### 调试模式（debug_mode）

一般在**调试和查找**文本时使用，开启**将占用性能**，尽量单独挑出一点内容来测试，内容太多替换**会出bug**。

`is_enable`决定调试功能**是否开启**。开启时会在替换字符串时向日志中输出一行调试信息，调试信息的格式由`output_format`决定，调试信息的内容由`output_mode`决定。
`is_enable`默认为`false`。

`output_format`决定了调试信息的格式，默认为`<source> -> <target>`。
共有4种占位符，支持的占位符如下：

* `<source>`：源内容，即未做替换前的字符串。
* `<target>`：替换后的字符串。
* `<stack>`：堆栈跟踪数组，是此字符串所在类的`StackTrace`(包括本mod)。
* `<method>`：该文本渲染所调用的方法。

`output_mode`决定了调试信息的内容。
若为 0, 则仅输出替换的字符串；
若为 1, 则仅输出不被替换的字符串。

### 编辑模式（test_mode，虽然没有编辑功能）

开启后，模组会把字符串匹配度高达50%的做一个标记，替换的地方会做一个标记

### 优化参数（optimize_params）

更改优化替换算法的参数。

`disable_export`决定是否禁用`export`功能，该选项对于优化有很大的作用。但同时指令`/vaultpatcher export`也将被禁用。
`disable_export`默认为 false。

`disable_stacks`决定是否禁用堆栈匹配，该选项对于优化有很大的作用。但同时类匹配也将禁用。
默认为 false。

`stack_min`和`stack_max`，决定了堆栈跟踪数组中的上限与下限，适当调整参数可以达到优化效果。
默认均为 -1（即不更改上限下限）。

## 模块

### 模块的第一个键值对

模块的第一个键值对**不起替换效果**，它**有固定格式**，用于`/vp list`中**显示相关信息**，如下：

```json
    {
        "authors": "(authors)",
        "name": "(name)",
        "desc": "(describe)",
        "mods": "(mods)"
    },
```

### 模块格式

模块的完整格式：

```json
[
  {
      "authors": "(authors)",
      "name": "(name)",
      "desc": "(describe)",
      "mods": "(mods)"
  },
  {
    "target_class": {
      "name": "@iskallia.vault",
      "method": "",
      "stack_depth": 0
    },
    "key": "被汉化文本",
    "value": "汉化文本"
  },
  {
    "target_class": {
      "name": "#iskallia.vault.client.gui.screen.player.StatisticsElementContainerScreen",
      "method": "",
      "stack_depth": 0
    },
    "key": "被汉化文本",
    "value": "汉化文本"
  }
]
```

如果觉得该文本不会重复，可以去掉`target_class`：

```json
[
  {
    "key": "I'm key",
    "value": "@I'm value"
  },
  {
    "key": "Dragon Relic",
    "value": "namespace.modify.modid.item.relics.dragonrelic"
  },
  {
    "key": "Talents",
    "value": "namespace.modify.the_vault.gui.talnets"
  }
]
```

一个基础格式：

```json
{
  "key": "I'm key",
  "value": "Im value"
}
```

就是一个翻译键值对，主要涉及到`key`、`value`和`target_class`。

### 键值对（key-value pair）

#### 键（Key）

`key`，顾名思义，指定的是要翻译的字符串。

如果我想翻译标题界面的``Copyright Mojang AB. Do not distribute!``，
那么要指定`"key":"Copyright Mojang AB. Do not distribute!"`。

#### 值（Value）

有了键，还得有值。

那么我想将``Copyright Mojang AB. Do not distribute!``改为``Mojang AB.``，
那就要指定`"value":"Mojang AB."`。

#### 半匹配（功能模块1）

以上的方式均为全匹配（即完全替换），只替换与`key`完全相同的文本。

如果你想半匹配，或者原字符串中有格式化文本（例如`§a`、`%d`、`%s`等）。
那么可以把句子拆开，分别为一个`key`，然后在`value`的前面加上`@`字符，实现半匹配。

例子1：

```json
{
  "key": "Grass",
  "value": "@CAO"
}
```

这样就会把所有的`Grass`都替换为`CAO`（包括`Grass Block`、`Grass`、`Tall Grass`）

例子2：

```text
Complete a%s %s altar for a chance to gain favour with %s. A favour will grant a buff in the subsequent vault. Completing the objective in said vault will grant a reputation point which slowly increases the power of the buffs.
```

```json
  {
    "key": "Complete a",
    "value": "@I'm value"
  },
  {
    "key": "altar for a chance to gain favour with ",
    "value": "@I'm value"
  },
  {
    "key": "A favour will grant a buff in the subsequent vault. Completing the objective in said vault will grant a reputation point which slowly increases the power of the buffs.",
    "value": "@I'm value"
  }
```

这样，一个基础的键值对就完成了。

## 更进一步

一个完整格式：

```json
{
  "target_class": {
    "name": "",
    "method": "",
    "stack_depth": -1
  },
  "key": "I'm key",
  "value": "Im value"
}
```

### 注入目标（target_class）

`target_class`这个对象主要用于指定两个相同`key`的不同`value`。
简单解释下：

有一个GUI里面有`Close`（指关闭GUI）这个文本，另一个GUI也有`Close`（指关闭管道），

此时它们的含义不同，但是若不加上`target_class`，那么他们的翻译内容却是一样的。
所以要用到`target_class`。

`target_class`中有三个键：`name`、`mapping`和`stack_depth`。

### 类名（name）

类名的匹配的可用规则共3种：

#### 包匹配（功能模块2）

包名如何获取：

* jadx的用户，打开`模组.jar`文件，找到其中放有大量`.class`文件的文件夹（最好能看见与模组名相关的.class文件，比如模组名：XPCoins，找到`XPCoins.class`文件所在的目录），将这个文件夹的地址复制，大致是`XXX\XXX.jar\com\coldspell\xpcoins`，然后把`XXX\XXX.jar\`删掉，得到`com\coldspell\xpcoins`

* java-decompiler的用户，因为上文已经解压了放有大量`.java`文件的文件夹，即直接取`com\coldspell\xpcoins`

不同模组存`.class`文件的文件夹名可能不同（没有com文件夹），要自己辨别
接着把`\`全部改成`.`，包名就为`@文件夹地址`,比如`@com.coldspell.xpcoins`或者`@iskallia.vault`

* 以`@`开头的字符串会视为包匹配（示例：`#net.minecraft.client`会匹配`net.minecraft.client.gui.screens.TitleScreen`
  和`net.minecraft.client.gui.screens.BeaconScreen`等等
  也匹配`net.minecraft.client.gui.titlescreen.screens`）

#### 类匹配（功能模块3）

效果：在匹配替换范围限定在类地址的范围，可以有效避免一些替换效果不好，且不是你要替换的内容

内容根地址如何获取：

对于没有java语言概念的：与包名获取相同，找到那个文件夹（这里称它为`内容根初始地址`），里面的文件夹和.class文件是可以作为内容根地址，越深入，匹配范围越小。一般来说，你要用类匹配，就要知道这个key来源于哪个文件和文件夹，从`内容根初始地址`到某个文件夹或文件的地址，再把`\`全部改成`.`，并删除文件后缀，就是`内容根地址`，然后在前面加上`#`即可。如：`#iskallia.vault.client.gui.screen.player.StatisticsElementContainerScreen`

有概念的：就是java中的`类全限定名`，详见[教程](https://blog.csdn.net/weixin_36873225/article/details/117060872),(比如：import `类全限定名`;)

* `method`如何获取：指的是java中的`方法名`，详见[教程](https://www.runoob.com/java/java-methods.html)，要配合类匹配使用

* 以`#`开头的字符串会视为类匹配（示例：`#TitleScreen`会匹配`net.minecraft.client.gui.screens.TitleScreen`
  和`net.minecraft.client.gui.screens.titlescreen`
  但不匹配`net.minecraft.client.gui.titlescreen.screens`）

#### 完全匹配

* 不以`#`或`@`开头的字符串会视为全匹配（示例：`net.minecraft.client.gui.screens.TitleScreen`会匹配`net.minecraft.client.gui.screens.TitleScreen`
  和`net.minecraft.client.gui.screens.titlescreen`
  但不匹配`net.minecraft.client.gui.titlescreen.screens`）

### 映射（mapping）

保留字段

### 堆栈深度（stack depth）

保留字段

#### **(Tips: 过于复杂，不建议新手用)**

#### **(模组作者其实也不会)**

堆栈深度在堆栈中用于更精准的匹配类，
例如在如下堆栈中

```txt
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
  "target_class": {
    "name": "net.minecraft.client.gui.screens.TitleScreen",
    "method": "",
    "stack_depth": 2
  },
  "key": "Copyright Mojang AB. Do not distribute!",
  "value": "Mojang AB."
}
```

此时便能精准的定位到`net.minecraft.client.gui.screens.TitleScreen`这个类。

### 参考配置1

**_（用于Vault Hunter 3rd Edition）_**

```json
[
  {
    "target_class": {
      "name": "",
      "method": "",
      "stack_depth": 0
    },
    "key": "Attack Damage",
    "value": "namespace.modify.the_vault.gui.attackdamage"
  },
  {
    "target_class": {
      "name": "",
      "method": "",
      "stack_depth": 0
    },
    "key": "Dragon Relic",
    "value": "namespace.modify.the_vault.item.relics.dragonrelic"
  },
  {
    "target_class": {
      "name": "",
      "method": "",
      "stack_depth": 0
    },
    "key": "Talents",
    "value": "namespace.modify.the_vault.gui.talnets"
  }
]
```

### 参考配置2

**_（用于Vault Hunter 3rd Edition）_**

[链接](https://github.com/KlparetlR/Vault-Patcher-Grocery-Store/blob/main/ModConfigs/Forge/vault-hunters-official-mod/the_vault/mod-the_vault-1.18.2.json)

### 其他

#### 主作者：FengMing([github](https://github.com/3093FengMing))

#### 本教程编写：KlparetlR([github](https://github.com/KlparetlR))

#### 配置部分：teddyxlandlee([github](https://github.com/teddyxlandlee))

#### 想法：yiqv([github](https://github.com/yiqv))

#### Mod地址：[github](https://github.com/3093FengMing/VaultPatcher)，[mcmod](https://www.mcmod.cn/class/8765.html)，[bilibili](等)
