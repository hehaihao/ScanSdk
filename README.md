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

