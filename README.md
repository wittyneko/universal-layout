
## UniversalLayout 的导入

1. 在项目根目录build.gradle添加repositories，注意是项目根目录的，不是项目的build.gradle
	```gradle
	repositories {
		//其他maven库...
		maven { url "https://jitpack.io" }
	}
	
	```
2. 在项目的build.gradle添加dependencies
	```gradle
	dependencies {
		compile 'com.github.brady9308:universal-layout:1.1.0'
	}
	
	```

## 版本历史
- 1.1.0版本
	```
	重构代码结构，拆分类文件
    添加百分比以自身为比例计算
    添加百分比以父控件比例是否加入padding值计算设置
    添加布局文件参数提示
    修改简化参数名称
    修改基础值默认为自适应、屏幕、宽度
    修改min、max的计算方式支持任意空间设置
    不兼容1.0.x版本，但两个版本可以共存
    ```
- 1.0.x版本
	```
    初始版本
    ```

## UniversalLayout 的使用之自动适配

1. 定义一个Style样式存放设计的尺寸，多个尺寸就定义多个

	```xml
	<style name="Design320x568">
		<item name="layout_widthDesign">320px</item>
		<item name="layout_heightDesign">568px</item>
	</style>
	```
	为了方便使用我们多定义几个其他的
	```xml
	<style name="Design640x1136">
		<item name="layout_widthDesign">640px</item>
		<item name="layout_heightDesign">1136px</item>
	</style>
	<style name="LayoutWrapWrap">
		<item name="android:layout_width">wrap_content</item>
		<item name="android:layout_height">wrap_content</item>
	</style>
	```

2. 在xml布局中加入我们的样式就行了，顺带介绍下工作原理了。很简单的就是把设计的尺寸加入到LayoutParams里，每个UniversalLayout的子控件都有一个自己的设计尺寸。那不是得每个都写尺寸信息之不累死人也会烦死人，所以才用到style，当然不需要每个写，也不需要每个都添加style，只要在继承UniversalLayout的父控件加个childStyle，子控件就会默认使用指定的style，如果子控件设置了Design会覆盖掉默认style，这样一来就算设计换了尺寸有多个尺寸，我们只要改下style就OK了，原理自行阅读源码。
	```xml
    <silicar.tutu.universal.UniversalLinearLayout
        android:orientation="vertical"
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:childStyle="@style/Design320x568">
        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:text="Design Width 320  Height 568 TextSize 16aw Width 320a"
            app:layout_textSizeUniversal="16a"
            app:layout_widthUniversal="320a"/>

        <TextView
            style="@style/Design640x1136"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:text="Design Width 640  Height 1136 TextSize 16aw Width 320a"
            app:layout_textSizeUniversal="16a"
            app:layout_widthUniversal="320a"/>

        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:text="Width 320aw  Height 25aw TextSize 16aw"
            app:layout_textSizeUniversal="16a"
            app:layout_widthUniversal="320a"
            app:layout_heightUniversal="25aw"/>

        <TextView
            style="@style/LayoutWrapWrap"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:textColor="@android:color/white"
            android:text="Width 320ah  Height 25ah TextSize 16ah"
            app:layout_textSizeUniversal="16ah"
            app:layout_widthUniversal="320ah"
            app:layout_heightUniversal="25a"/>

        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:text="Width 160aw  Height 60aw TextSize 16aw"
            app:layout_textSizeUniversal="16a"
            app:layout_widthUniversal="160a"
            app:layout_heightUniversal="60aw"/>

        <TextView
            style="@style/LayoutWrapWrap"
            app:layout_marginTopUniversal="5a"
            android:background="#666"
            android:textColor="@android:color/white"
            android:text="Width 160ah  Height 60ah TextSize 16ah"
            app:layout_textSizeUniversal="16ah"
            app:layout_widthUniversal="160ah"
            app:layout_heightUniversal="60a"/>
    </silicar.tutu.universal.UniversalLinearLayout>
	```

	![AutoLayout](http://brady9308.github.io/images/universal_layout/auto_nuxus_4.png)

	Nexus 4预览效果(768x1280)，同样建议以宽度为基准

3. 在代码中使用UniversalLayout这有两种情况，一种父控件是UniversalLayout的，另一种是普通布局的。
	- 父控件是UniversalLayout
		```java
        UniversalLinearLayout.LayoutParams params = (UniversalLinearLayout.LayoutParams) codeView.getLayoutParams();
        UniversalLayoutInfo info = params.getUniversalLayoutInfo();
        info.width.value = 0.8f;
        //SampleModel model = (SampleModel) info.width.model;
        //model.setMode(BaseModel.modePercent).setObj(BaseModel.objScreen);
        info.width.model = new SampleModel(BaseModel.modePercent, BaseModel.objScreen, true);
        info.height.value = 50;
        codeView.requestLayout();
		```

	- 普通布局
		```java
        UniversalValue universalValue = new UniversalValue(300, new SampleModel().getDefaultDesign());
        codeView2.getLayoutParams().width = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
        codeView2.getLayoutParams().height = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
        codeView2.requestLayout();
		```

## UniversalLayout 的使用之百分比

```xml
<?xml version="1.0" encoding="utf-8"?>
<silicar.tutu.universal.UniversalLinearLayout
    android:orientation="vertical"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        style="@style/LayoutWrapWrap"
        android:textColor="@android:color/white"
        app:layout_marginTopUniversal="1%"
        android:background="#666"
        android:text="Width 100%sw  Height 8%sw TextSize 5%ws"
        app:layout_textSizeUniversal="5%s"
        app:layout_widthUniversal="100%s"
        app:layout_heightUniversal="8%sw"/>

    <TextView
        style="@style/LayoutWrapWrap"
        app:layout_marginTopUniversal="1%"
        android:background="#666"
        android:textColor="@android:color/white"
        android:text="Width 100%sh  Height 8%sh TextSize 5%sh"
        app:layout_textSizeUniversal="5%sh"
        app:layout_widthUniversal="100%sh"
        app:layout_heightUniversal="8%s"/>

    <silicar.tutu.universal.UniversalLinearLayout
        android:orientation="vertical"
        android:background="#aaa"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_marginUniversal="5%s"
        app:childStyle="@style/Design320x568">

        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="1%"
            android:background="#666"
            android:text="Parent Margin 5%s TextSize 5%s"
            app:layout_textSizeUniversal="5%s"/>

        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="1%"
            android:background="#666"
            android:text="Width 50%w  Height 15%w TextSize 5%w"
            app:layout_textSizeUniversal="5%"
            app:layout_widthUniversal="50%"
            app:layout_heightUniversal="15%w"/>

        <TextView
            style="@style/LayoutWrapWrap"
            android:textColor="@android:color/white"
            app:layout_marginTopUniversal="1%"
            android:background="#666"
            android:text="Width 50%sw  Height 15%sw TextSize 5%sw"
            app:layout_textSizeUniversal="5%s"
            app:layout_widthUniversal="50%s"
            app:layout_heightUniversal="15%sw"/>

        <TextView
            style="@style/LayoutWrapWrap"
            app:layout_marginTopUniversal="1%"
            android:background="#666"
            android:textColor="@android:color/white"
            android:text="Width 50%h  Height 15%h TextSize 5%h"
            app:layout_textSizeUniversal="5%h"
            app:layout_widthUniversal="50%h"
            app:layout_heightUniversal="15%"/>

        <TextView
            style="@style/LayoutWrapWrap"
            app:layout_marginTopUniversal="1%"
            android:background="#666"
            android:textColor="@android:color/white"
            android:text="Width 50%sh  Height 15%sh TextSize 5%sh"
            app:layout_textSizeUniversal="5%sh"
            app:layout_widthUniversal="50%sh"
            app:layout_heightUniversal="15%s"/>

    </silicar.tutu.universal.UniversalLinearLayout>

</silicar.tutu.universal.UniversalLinearLayout>
```

适配的时候建议宽度作为基础，Android的屏幕比不一定都是9：16，不同的手机会有不同的显示尤其是魅族这类奇葩机型

![Nexus 4](http://brady9308.github.io/images/universal_layout/percent_nexus_4.png)

Nexus 4预览效果(768x1280)

![Galaxy Nexus](http://brady9308.github.io/images/universal_layout/percent_galaxy_nexus.png)

Galaxy Nexus 预览效果(720x1280)

可见不同比例的手机显示的效果不同