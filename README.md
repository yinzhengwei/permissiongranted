# permissionGranted
Android动态权限检测和申请管理

# 配置方式

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

allprojects {

	repositories {
	
		...
		
		maven { url 'https://jitpack.io' }
		
	}
	
}

Step 2. Add the dependency

dependencies {

	implementation 'com.github.yinzhengwei:permissiongranted:13.0'
	
}

# 代码使用方式

关于Android6.0以上系统需要动态申请和检测权限，此demo做了一次封装， 
使用方式简洁明了，只需要一行代码(
参数一表示上下文、
参数二表示要开启 的权限集合、
第三个参数表示要申请的权限名称(如：相机)，当权限遭到用户拒绝时，弹窗 中的提示语里使用，默认不传表示不具体说明哪个权限、
参数四是操作结果的回调)：

方式一：
PermissionUtils.permissionCheck(this, permission_CAMERA, "xxx", null)

方式二：
PermissionUtils.permissionCheck(this, permission_CAMERA, "xxx", object : PermissionGrantedCallback {
    override fun granted() {
        Log.d("permissionCheck", "同意")
    }
    override fun denied() {
        Log.d("permissionCheck", "拒绝")
    }
})

方式三（这里的第四个参数表示是否强制用户打开权限）：
PermissionUtils.permissionCheck(this, permission_CAMERA, "xxx", false, object : PermissionGrantedCallback {
    override fun granted() {
        Log.d("permissionCheck", "同意")
    }
    override fun denied() {
        Log.d("permissionCheck", "拒绝")
    }
})



# 补充说明
同步完成了权限检测、大部分机型的适配、系统权限管理、管理提示弹窗等工作。 
需要开发者注意的是程序里做了几组权限的列举（详见PermissionList类）， 可自行添加和补充。



不管我们代码里动态监测什么权限，AndroidManifest.xml里都需要配置上， 
不仅为了适配6.0以下机型静态权限的使用，也是为了动态申请和检测权限时系 统权限管理界面能显示出需要申请的权限列表。
