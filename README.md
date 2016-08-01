
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

这是推荐的方法先进行介绍，布局会自动根据设计稿的尺寸和屏幕尺寸自动适配。只需要你定义设计稿的尺寸，一切都由布局自动完成。

1. 在主题存放设计的尺寸

	```xml
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="layout_widthDesign">640px</item>
        <item name="layout_heightDesign">1136px</item>
    </style>
	```
    或者自定义一个Style样式存放设计的尺寸，多个尺寸就定义多个
	```xml
	<style name="Design320x568">
		<item name="layout_widthDesign">320px</item>
		<item name="layout_heightDesign">568px</item>
	</style>
	<style name="Design640x1136">
		<item name="layout_widthDesign">640px</item>
		<item name="layout_heightDesign">1136px</item>
	</style>
	<style name="LayoutWrapWrap">
		<item name="android:layout_width">wrap_content</item>
		<item name="android:layout_height">wrap_content</item>
	</style>
	```

2. 针对主题定义的设计尺寸，最简单了不需要特殊属性设置
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
    	android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent">

    	<ImageView
        	android:layout_width="wrap_content"
        	android:layout_height="wrap_content"
        	android:scaleType="fitXY"
        	android:src="@mipmap/temp"
        	app:layout_widthExt="300"
        	app:layout_heightExt="200"/>

	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    
	![640x1136预览效果](https://github.com/brady9308/universal-layout/raw/master/resource/auto_640x1136.png)
    
	640x1136预览效果
3. 设计换人了，新的设计稿和原来的尺寸不一样怎么办，不急我们也跟着换尺寸，在父控件添加`app:childStyle="@style/Design320x568"`指定新的设计稿尺寸
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
    	android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent"
        app:childStyle="@style/Design320x568">

    	<ImageView
        	android:layout_width="wrap_content"
        	android:layout_height="wrap_content"
        	android:scaleType="fitXY"
        	android:src="@mipmap/temp"
        	app:layout_widthExt="300"
        	app:layout_heightExt="200"/>

	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    需要说明的是，这里只改变子控件的设计稿尺寸，不传递的就是说子控件的子控件是不改变的，必须另外指定
    
	![320x568预览效果](https://github.com/brady9308/universal-layout/raw/master/resource/auto_320x568.png)
    
	320x568预览效果
4. 只修改某个控件的设计尺寸，这个适配方案就这么考虑的，每个子控件都有自己的设计稿尺寸，都能指定设计稿尺寸`app:layout_widthDesign="480px" app:layout_heightDesign="800px"`
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
    	android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
    	android:layout_width="match_parent"
    	android:layout_height="match_parent"
        app:childStyle="@style/Design320x568">

    	<ImageView
        	android:layout_width="wrap_content"
        	android:layout_height="wrap_content"
        	android:scaleType="fitXY"
        	android:src="@mipmap/temp"
        	app:layout_widthDesign="480px"
        	app:layout_heightDesign="800px"
        	app:layout_widthExt="300"
        	app:layout_heightExt="200"/>

	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    说明下属性的优先级，子控件尺寸>父控件尺寸>主题尺寸
    
	![480x800预览效果](https://github.com/brady9308/universal-layout/raw/master/resource/auto_480x800.png)
    
	480x800预览效果


## UniversalLayout 的使用之百分比

UniversalLayout的就是基于Google官方的PercentLayout扩展重构的。因此百分比的支持是必须的，在原来的基础上又进行了扩展，使用更加方便了。

1. 以屏幕宽度为参照的百分比，百分比的默认方法
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
	    android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
    	android:layout_height="match_parent">
	
    	<ImageView
	        android:layout_width="wrap_content"
    	    android:layout_height="wrap_content"
	        android:scaleType="fitXY"
        	android:src="@mipmap/temp"
        	app:layout_widthExt="50%"
    	    app:layout_heightExt="30%"/>
	
	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    ![屏幕宽度百分比](https://github.com/brady9308/universal-layout/raw/master/resource/percent_screen_width.png)
2. 以屏幕高度为参照的百分比
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
	    android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
    	android:layout_height="match_parent">
	
    	<ImageView
	        android:layout_width="wrap_content"
    	    android:layout_height="wrap_content"
	        android:scaleType="fitXY"
        	android:src="@mipmap/temp"
        	app:layout_widthExt="50%h"
    	    app:layout_heightExt="30%h"/>
	
	</silicar.tutu.universal.widget.UniversalLinearLayout>
    ```
    
    ![屏幕高度百分比](https://github.com/brady9308/universal-layout/raw/master/resource/percent_screen_height.png)
3. 以父控件宽度为参照的百分比(高度参考项目例子，不举例了)
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
	    android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
    	android:layout_height="match_parent">
	
    	<silicar.tutu.universal.widget.UniversalLinearLayout
	        android:orientation="vertical"
    	    android:gravity="center"
        	android:background="#ccc"
	        android:layout_width="match_parent"
    	    android:layout_height="match_parent"
        	app:layout_marginExt="5%">
        	<ImageView
	            android:layout_width="wrap_content"
    	        android:layout_height="wrap_content"
        	    android:scaleType="fitXY"
            	android:src="@mipmap/temp"
            	app:layout_widthExt="50%p"
            	app:layout_heightExt="30%p"/>
    	</silicar.tutu.universal.widget.UniversalLinearLayout>
	
	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    
    ![父控件宽度百分比](https://github.com/brady9308/universal-layout/raw/master/resource/percent_parent_width.png)
4. 以自身为参照的百分比
	```xml
    <?xml version="1.0" encoding="utf-8"?>
	<silicar.tutu.universal.widget.UniversalLinearLayout
    	android:orientation="vertical"
	    android:gravity="center"
    	xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
    	android:layout_height="match_parent">
	
    	<silicar.tutu.universal.widget.UniversalLinearLayout
	        android:orientation="vertical"
    	    android:gravity="center"
        	android:background="#ccc"
	        android:layout_width="match_parent"
    	    android:layout_height="match_parent"
        	app:layout_marginExt="5%">
        	<ImageView
	            android:layout_width="wrap_content"
    	        android:layout_height="wrap_content"
        	    android:scaleType="fitXY"
            	android:src="@mipmap/temp"
            	app:layout_widthExt="100%oh"
            	app:layout_heightExt="30%ph"/>
    	</silicar.tutu.universal.widget.UniversalLinearLayout>
	
	</silicar.tutu.universal.widget.UniversalLinearLayout>
	```
    该功能还未完成，存在点小问题50%o,50%ow,50%oh，多数情况还是能使用的
    
    ![自身高度百分比](https://github.com/brady9308/universal-layout/raw/master/resource/percent_own_height.png)

适配的时候建议宽度作为基础，默认也是以宽度为基础，Android的屏幕比不全都是9：16，不同的手机会有不同，Nexus 4(768x1280)和Galaxy Nexus(720x1280)都是4.7寸屏，但比例不一样。


## UniversalLayout 动态设置

难免需要用到，比如在ListView中设置item的高度，在代码中使用UniversalLayout有两种情况。

- 父控件是UniversalLayout并设置了相应属性
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

- 普通布局、父控件是UniversalLayout未设置属性
```java
        UniversalValue universalValue = new UniversalValue(300, new SampleModel().getDefaultDesign());
        codeView2.getLayoutParams().width = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
        codeView2.getLayoutParams().height = (int) UniversalDimens.getUniversalDimens(universalValue, UniversalLayoutHelper.getDisplay());
        codeView2.requestLayout();
```