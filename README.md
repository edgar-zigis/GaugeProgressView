# GaugeProgressView [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bio-matic/gaugeprogressview/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bio-matic/gaugeprogressview)

Unique progress view with rich customisation options! You can hide value if you want too.
##### Minimum target SDK: 21. RTL SUPPORTED.

![alt text](https://github.com/edgar-zigis/GaugeProgressView/blob/master/sample.gif?raw=true)

### Gradle
Make sure you have **Maven Central** included in your gradle repositories.

```gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```
```gradle
implementation 'com.bio-matic:gaugeprogressview:1.2.1'
```
### Usage
``` xml
<com.zigis.gaugeprogressview.GaugeProgressView
    android:id="@+id/progressView"
    android:layout_width="220dp"
    android:layout_height="220dp"
    app:gpv_innerArcColor="@color/gray"
    app:gpv_innerArcDashDistance="8dp"
    app:gpv_innerArcDashThickness="2dp"
    app:gpv_innerArcThickness="10dp"
    app:gpv_isValueHidden="false"
    app:gpv_offsetBetweenArcs="15dp"
    app:gpv_outerArcColor="@color/red"
    app:gpv_outerArcThickness="8dp"
    app:gpv_progress="30"
    app:gpv_startAngle="-90"
    app:gpv_valueFont="@font/tt_norms_pro_medium"
    app:gpv_valueTextColor="@color/black"
    app:gpv_valueTextSize="50sp" />
```
