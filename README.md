# README.md

some android project stuff


### install ant
```
# http://ant.apache.org/bindownload.cgi  |  http://archive.apache.org/dist/ant/binaries/
wget http://mirrors.advancedhosters.com/apache//ant/binaries/apache-ant-1.9.4-bin.zip
unzip apache-ant-1.9.4-bin.zip
sudo mv apache-ant-1.9.4-bin /opt/
sudo ln -s /opt/apache-ant-1.9.4-bin  /opt/ant
sudo ln -s /opt/ant /usr/bin/ant
```


### install android
```
# download http://developer.android.com/sdk/index.html#download
wget ...
# copy to /opt/adt
sudo mv ... /opt/adt
sudo ln -s ... /opt/adt
sudo ln -s ... /opt/android
sudo ln -s ... /opt/eclipse-android

# update
android list sdk --all
android update sdk -u
android update sdk -u -a
```

http://developer.android.com/sdk/installing/installing-adt.html


### Create Android Application Project
```
# navigate to new project directory
cd ~/universe/repos/fa/android

# list of all available android targets
android list targets

# create project
android create project  --target 29  --name CaseStudy  --path ./CaseStudy  --activity StartupActivity  --package com.richtastic.casestudy
```


### Update Android Project
```
android update project  --name CaseStudy  --path ./CaseStudy  --target 29
# options
--library path/to/library_projectA
```


### Build/Run Project
```
# build
ant debug
ant release

# run in sim
android avd &
android install  ./bin/CaseStudy-debug-unaligned.apk

# list available devices
adb devices

# run on device
adb -s "emulator-5554" install ./bin/CaseStudy-debug-unaligned.apk

# logged output
adb logcat  -s "tag1","tag2"

```

### when adb is stupid
```
adb kill-server
adb start-server
```

### device shell
```
adb shell
```



### App Signing
```
# generate private key -> .keystore
keytool -genkey -v -keystore case_study_release_private_key.keystore -alias release -keyalg RSA -keysize 2048 -validity 11000 # 30 years

# release build -> CaseStudy-release-unsigned.apk
ant clean release

# sign relase apk
jarsigner -keystore case_study_release_private_key.keystore -digestalg SHA1 -sigalg MD5withRSA ./bin/CaseStudy-release-unsigned.apk release

# verify signing
jarsigner -verify ./bin/CaseStudy-release-unsigned.apk

# align zip to boundares
zipalign -v 4 ./bin/CaseStudy-release-unsigned.apk ./bin/CaseStudy_Release_2014_07_18.apk

```



Test http://developer.android.com/tools/testing/testing_android.html


e build-tools and platform-tools.

### Library Android Project
```
android create lib-project ...
android update lib-project  --path ./CaseStudy  --target 29
```



### reference
```
adb -e # target emulator (if only 1)
adb -d # target device (if only 1)

ant clean debug install

/opt/adt/sdk/tools/monitor # not ddms

```



### other
- Settings > About Phone > tap Build Number seven times




### android studio





### command line references
[http://developer.android.com/tools/projects/projects-cmdline.html](http://developer.android.com/tools/projects/projects-cmdline.html)
[http://developer.android.com/tools/help/adb.html](http://developer.android.com/tools/help/adb.html)
[http://developer.android.com/tools/workflow/index.html](http://developer.android.com/tools/workflow/index.html)
[http://incise.org/android-development-on-the-command-line.html](http://incise.org/android-development-on-the-command-line.html)
[https://agiliq.com/blog/2012/03/developing-android-applications-from-command-line/](https://agiliq.com/blog/2012/03/developing-android-applications-from-command-line/)




