# ClassResGuard

## 前言
此项目是在[XmlClassGuard](https://github.com/liujingxing/XmlClassGuard)基础上修改的,
因原作者不满足自己的需求,所以自己拉下来改了一个Task的功能(在所有类名前加前缀,原项目是修改成ABCD之类的),
并且新增了两个Task(修改所有res中的文件,添加垃圾文件)
这是一个可以给所有类添加前缀,给所有res文件添加前缀,并且图片会自动在layout中换成最新的名称
可以添加垃圾类(普通java class,Activity class,layout,drawable,并且Activity会
自动在AndroidManifest.xml里注册,还会绑定生成的垃圾xml)

## 警告警告⚠️

由于是在本地操作，任务执行是不可逆的，故务必做好代码备份，否则代码将很难还原

## 使用

没上传到maven,需要clone此项目,依赖到本地使用
1.在setting.gradle里配置
```
pluginManagement {
    repositories {
        mavenLocal()
    }
}
```
2.在build.gradle(project)中配置
```
buildscript {
    dependencies {
        classpath "com.leos.superplugin:XmlClassGuard:1.0.0"
    }
}
```
3.在build.gradle(app)中配置
```
apply plugin: "xml-class-guard"

//以下均为非必须，根据自己需要配置即可
xmlClassGuard {
    /*
     * 是否查找约束布局的constraint_referenced_ids属性的值，并添加到AabResGuard的白名单中，
     * 是的话，要求你在XmlClassGuard前依赖AabResGuard插件，默认false
     */
    findConstraintReferencedIds = true
    //前缀名称
    prefixName = "Large"
    //需要修改的res文件目录
    changeXmlPkg = ["drawable","layout","mipmap-hdpi"]
    //生成java垃圾文件的目录 目前只能是自己根目录
    packageName = "com.leos.superplugin"
    //生成java Activity垃圾文件中的方法数
    activityClassMethodCount = 20
    //生成java Activity文件的数量
    activityClassCount = 50
    //生成java 普通垃圾文件中的方法数
    normalClassMethodCount = 20
    //生成java 普通文件的数量
    normalClassCount = 50
    //生成layout文件的数量
    layoutClassCount = 50
    //生成layout文件中view的数量
    layoutClassMethodCount = 15
    //生成drawable文件的数量
    drawableClassCount = 50
    //混淆映射文件 改名称需要在xml-class-mapping.txt中配置好所有的包名,详情可看[demo文件](https://github.com/coolxinxin/ClassResGuard/blob/master/app/xml-class-mapping.txt)
    mappingFile = file("xml-class-mapping.txt")
    //更改manifest里package属性  多个module的情况用,分割
    packageChange = ["com.leos.superplugin": "con.large.crede"]
    //移动目录，支持同时移动多个目录
    moveDir = ["com.leos.superplugin": "con.large.crede"]
}
```
如图看到一下任务
![image](img/1.jpg)

## 任务介绍 

分别是findConstraintReferencedIds、moveDir、packageChange、xmlClassGuard、xmlNameChangeGuard、garbageGuard，这6个任务之间没有任何关系，下面将一一介绍
其中前四个任务是[XmlClassGuard](https://github.com/liujingxing/XmlClassGuard)项目,
xmlClassGuard任务的功能被我修改了部分

### 1.findConstraintReferencedIds
该任务需要配合[AabResGuard](https://github.com/bytedance/AabResGuard)插件使用，如果你未使用AabResGuard插件，可忽略。

这里简单介绍下，由于约束布局constraint_referenced_ids属性的值，内部是通过getIdentifier方法获取具体的id，这就要求我们把constraint_referenced_ids属性的值添加进AabResGurad的白名单中，否则打包时，id会被混淆，打包后，constraint_referenced_ids属性会失效，UI将出现异常。

然而，项目中可能很多地方都用到constraint_referenced_ids属性，并且值非常多，要一个个找出来并手动添加到AabResGuard的白名单中，无疑是一项繁琐的工作，于是乎，findConstraintReferencedIds任务就派上用场了，它是在打包时，自动查找constraint_referenced_ids属性并添加进AabResGuard的白名单中，非常实用的功能，你仅需要在XmlClassGurad的配置findConstraintReferencedIds为true即可，如下：

```
//以下均为非必须
xmlClassGuard {
    /*
     * 是否查找约束布局的constraint_referenced_ids属性的值，并添加到AabResGuard的白名单中，
     * true的话，要求你在XmlClassGuard前依赖AabResGuard插件，默认false
     */
    findConstraintReferencedIds = true
}
```
findConstraintReferencedIds任务不需要手动执行，打包(aab)时会自动执行

### 2.moveDir

moveDir是一个移动目录的任务，它支持同时移动任意个目录，它会将原目录下的所有文件(包括子目录)移动到另外一个文件夹下，并将移动的结果，同步到其他文件中，配置如下：
```
xmlClassGuard {
    //移动目录，支持同时移动多个目录 多个module的情况用,分割
    moveDir = ["com.leos.superplugin": "con.large.crede"]
}
```
上面代码中moveDir是一个Map对象，其中key代表要移动的目录，value代表目标目录； 上面任务会把com.leos.superplugin目录下的所有文件，移动到con.large.crede 目录下，

### 3.packageChange

packageChange是一个更改manifest文件里package属性的任务，也就是更改app包名的任务(不会更改applicationId) ，改完后，会将更改结果，同步到其他文件中(不会更改项目结构)，配置如下：
```
xmlClassGuard {
    //更改manifest文件的package属性，即包名
    packageChange = ["com.leos.superplugin": "ab.cd"]
}
```
以上packageChange是一个Map对象，key为原始package属性，value为要更改的package属性，原始package属性不匹配，将更改失败

### 4.xmlClassGuard
```
 prefixName = "Large"
//需要指定个文件,并配置好所有的包名,可看demo,如觉得不好用,可使用XmlClassGuard的功能,上面有传送门
 mappingFile = file("xml-class-mapping.txt")
```

### 5.xmlNameChangeGuard

```
 //前缀名称
 prefixName = "Large"
 //将你需要修改类名的目录放到这个数组中
 changeXmlPkg = ["drawable","layout","mipmap-hdpi"]
```

### 6.garbageGuard
```
    //生成java垃圾文件的目录 目前只能是自己根目录
    packageName = "com.leos.superplugin"
    //生成java Activity垃圾文件中的方法数
    activityClassMethodCount = 20
    //生成java Activity文件的数量
    activityClassCount = 50
    //生成java 普通垃圾文件中的方法数
    normalClassMethodCount = 20
    //生成java 普通文件的数量
    normalClassCount = 50
    //生成layout文件的数量
    layoutClassCount = 50
    //生成layout文件中view的数量
    layoutClassMethodCount = 15
    //生成drawable文件的数量
    drawableClassCount = 50
```

