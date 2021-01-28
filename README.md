# ScanSdk
add in project root:

buildscript {

    repositories {
    
        maven { url 'http://developer.huawei.com/repo/' }
        
    }
    
}


allprojects {

    repositories {
    
        maven { url 'http://developer.huawei.com/repo/'}
        
    }
    
}

add in module:

implementation 'com.github.hehaihao:ScanSdk:1.2.0'

implementation 'com.huawei.hms:scan:1.3.0.300'

#about Proguard：

-ignorewarnings

-keepattributes *Annotation*

-keepattributes Exceptions

-keepattributes InnerClasses

-keepattributes Signature

-keepattributes SourceFile,LineNumberTable

-keep class com.huawei.hianalytics.**{*;}

-keep class com.huawei.updatesdk.**{*;}

-keep class com.huawei.hms.**{*;}

-keep class com.xm6leefun.scan_lib.**{*;}
