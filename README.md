# README.md

some android project stuff


### install ant
```
sudo apt-get install ant
# manual alternative: http://ant.apache.org/bindownload.cgi  |  http://archive.apache.org/dist/ant/binaries/
wget http://mirrors.advancedhosters.com/apache//ant/binaries/apache-ant-1.9.4-bin.zip
unzip apache-ant-1.9.4-bin.zip
sudo mv apache-ant-1.9.4-bin /opt/
sudo ln -s /opt/apache-ant-1.9.4-bin  /opt/ant
sudo ln -s /opt/ant /usr/bin/ant
```

### install JAVA JDK
```
# open
sudo apt-get install openjdk-7-jdk
# export PATH=$PATH:$JAVA_HOME/bin
# oracle
# sudo apt-get install oracle-java7-installer
```

### install 32 bit support for adb:
```
sudo apt-get install libstdc++6:i386 libgcc1:i386 zlib1g:i386 libncurses5:i386
sudo apt-get install libsdl1.2debian:i386
sudo apt-get install ia32-libs
```

### install android
```
# download http://developer.android.com/sdk/index.html#download
wget http://dl.google.com/android/adt/adt-bundle-linux-x86_64-20140702.zip
unzip adt-bundle-linux-x86_64-20140702
# copy to /opt/adt
sudo mv adt-bundle-linux-x86_64-20140702 /opt/adt
sudo ln -s /opt/adt/sdk/platform-tools/adb /usr/bin/adb
sudo ln -s /opt/adt/sdk/tools/android /usr/bin/android
sudo ln -s /opt/adt/eclipse/eclipse /usr/bin/eclipse-android
sudo ln -s /opt/adt/sdk/build-tools/android-4.4W/zipalign /usr/bin/zipalign

# update
android list sdk --all
android update sdk -u
android update sdk -u -a
```
# http://developer.android.com/sdk/installing/installing-adt.html



### x/ubuntu allow device usb access
```
# http://wiki.cyanogenmod.org/w/UDEV
# add user to plugdev group
groups
sudo gpasswd -a usernameGoesHere plugdev

# device shows up on lsusb
lsusb

# add rules to
sudo vi /etc/udev/rules.d/51-android.rules

#Acer
SUBSYSTEM=="usb", ATTR{idVendor}=="0502", MODE="0664", GROUP="plugdev"
#ASUS
SUBSYSTEM=="usb", ATTR{idVendor}=="0b05", MODE="0664", GROUP="plugdev"
#Dell
SUBSYSTEM=="usb", ATTR{idVendor}=="413c", MODE="0664", GROUP="plugdev"
#Foxconn
SUBSYSTEM=="usb", ATTR{idVendor}=="0489", MODE="0664", GROUP="plugdev"
#Fujitsu & Fujitsu Toshiba
SUBSYSTEM=="usb", ATTR{idVendor}=="04c5", MODE="0664", GROUP="plugdev"
#Garmin-Asus
SUBSYSTEM=="usb", ATTR{idVendor}=="091e", MODE="0664", GROUP="plugdev"
#Google
SUBSYSTEM=="usb", ATTR{idVendor}=="18d1", MODE="0664", GROUP="plugdev"
#Haier
SUBSYSTEM=="usb", ATTR{idVendor}=="201e", MODE="0664", GROUP="plugdev"
#Hisense
SUBSYSTEM=="usb", ATTR{idVendor}=="109b", MODE="0664", GROUP="plugdev"
#HTC
SUBSYSTEM=="usb", ATTR{idVendor}=="0bb4", MODE="0664", GROUP="plugdev"
#Huawei
SUBSYSTEM=="usb", ATTR{idVendor}=="12d1", MODE="0664", GROUP="plugdev"
#K-Touch
SUBSYSTEM=="usb", ATTR{idVendor}=="24e3", MODE="0664", GROUP="plugdev"
#KT Tech
SUBSYSTEM=="usb", ATTR{idVendor}=="2116", MODE="0664", GROUP="plugdev"
#Kyocera
SUBSYSTEM=="usb", ATTR{idVendor}=="0482", MODE="0664", GROUP="plugdev"
#Lenovo
SUBSYSTEM=="usb", ATTR{idVendor}=="17ef", MODE="0664", GROUP="plugdev"
#LG
SUBSYSTEM=="usb", ATTR{idVendor}=="1004", MODE="0664", GROUP="plugdev"
#Motorola
SUBSYSTEM=="usb", ATTR{idVendor}=="22b8", MODE="0664", GROUP="plugdev"
#MTK
SUBSYSTEM=="usb", ATTR{idVendor}=="0e8d", MODE="0664", GROUP="plugdev"
#NEC
SUBSYSTEM=="usb", ATTR{idVendor}=="0409", MODE="0664", GROUP="plugdev"
#Nook
SUBSYSTEM=="usb", ATTR{idVendor}=="2080", MODE="0664", GROUP="plugdev"
#Nvidia
SUBSYSTEM=="usb", ATTR{idVendor}=="0955", MODE="0664", GROUP="plugdev"
#OTGV
SUBSYSTEM=="usb", ATTR{idVendor}=="2257", MODE="0664", GROUP="plugdev"
#Pantech
SUBSYSTEM=="usb", ATTR{idVendor}=="10a9", MODE="0664", GROUP="plugdev"
#Pegatron
SUBSYSTEM=="usb", ATTR{idVendor}=="1d4d", MODE="0664", GROUP="plugdev"
#Philips
SUBSYSTEM=="usb", ATTR{idVendor}=="0471", MODE="0664", GROUP="plugdev"
#PMC-Sierra
SUBSYSTEM=="usb", ATTR{idVendor}=="04da", MODE="0664", GROUP="plugdev"
#Qualcomm
SUBSYSTEM=="usb", ATTR{idVendor}=="05c6", MODE="0664", GROUP="plugdev"
#SK Telesys
SUBSYSTEM=="usb", ATTR{idVendor}=="1f53", MODE="0664", GROUP="plugdev"
#Samsung
SUBSYSTEM=="usb", ATTR{idVendor}=="04e8", MODE="0664", GROUP="plugdev"
#Sharp
SUBSYSTEM=="usb", ATTR{idVendor}=="04dd", MODE="0664", GROUP="plugdev"
#Sony
SUBSYSTEM=="usb", ATTR{idVendor}=="054c", MODE="0664", GROUP="plugdev"
#Sony Ericsson
SUBSYSTEM=="usb", ATTR{idVendor}=="0fce", MODE="0664", GROUP="plugdev"
#Teleepoch
SUBSYSTEM=="usb", ATTR{idVendor}=="2340", MODE="0664", GROUP="plugdev"
#Toshiba
SUBSYSTEM=="usb", ATTR{idVendor}=="0930", MODE="0664", GROUP="plugdev"
#ZTE
SUBSYSTEM=="usb", ATTR{idVendor}=="19d2", MODE="0664", GROUP="plugdev"

# restart udev
sudo service udev restart
sudo udevadm control --reload-rules

# restart
sudo shutdown -r now
```



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

# install on device
adb -s "emulator-5554" install -r ./bin/CaseStudy-debug-unaligned.apk

# run on device
adb shell am start -n com.richtastic.casestudy/.StartupActivity

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
keytool -genkey -v -keystore ./keys/case_study_release_private_key.keystore -alias release -keyalg RSA -keysize 2048 -validity 11000 # 30 years

# release build -> CaseStudy-release-unsigned.apk
ant clean release

# sign relase apk
jarsigner -keystore ./keys/case_study_release_private_key.keystore -digestalg SHA1 -sigalg MD5withRSA ./bin/CaseStudy-release-unsigned.apk release

# verify signing
jarsigner -verbose -verify ./bin/CaseStudy-release-unsigned.apk

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
```
# download https://developer.android.com/sdk/installing/index.html?pkg=studio
tar -xvf android-studio-bundle-135.1245622-linux.tgz 
sudo mv ./android-studio /opt/android-studio
sudo ln -s /opt/android-studio/bin/studio.sh /usr/bin/android-studio
android-studio 
```


### references
[http://developer.android.com/sdk/installing/installing-adt.html](http://developer.android.com/sdk/installing/installing-adt.html)

### command line references
[http://developer.android.com/tools/projects/projects-cmdline.html](http://developer.android.com/tools/projects/projects-cmdline.html)
[http://developer.android.com/tools/help/adb.html](http://developer.android.com/tools/help/adb.html)
[http://developer.android.com/tools/workflow/index.html](http://developer.android.com/tools/workflow/index.html)
[http://incise.org/android-development-on-the-command-line.html](http://incise.org/android-development-on-the-command-line.html)
[https://agiliq.com/blog/2012/03/developing-android-applications-from-command-line/](https://agiliq.com/blog/2012/03/developing-android-applications-from-command-line/)




