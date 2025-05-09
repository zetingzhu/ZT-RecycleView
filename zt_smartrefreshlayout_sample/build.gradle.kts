plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.zzt.zt_smartrefreshlayout_sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zzt.zt_smartrefreshlayout_sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")
    implementation("androidx.activity:activity:1.10.1")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50")
    implementation("com.github.czy1121:loadinglayout:1.0.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.3")
    implementation("com.github.bumptech.glide:glide:4.13.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

//    implementation("androidx.appcompat:appcompat:1.0.0")                 //必须 1.0.0 以上

    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")    //核心必须依赖
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")    //经典刷新头
    implementation("io.github.scwang90:refresh-header-radar:3.0.0-alpha")        //雷达刷新头
    implementation("io.github.scwang90:refresh-header-falsify:3.0.0-alpha")      //虚拟刷新头
    implementation("io.github.scwang90:refresh-header-material:3.0.0-alpha")    //谷歌刷新头
    implementation("io.github.scwang90:refresh-header-two-level:3.0.0-alpha")   //二级刷新头
    implementation("io.github.scwang90:refresh-footer-ball:3.0.0-alpha")        //球脉冲加载
    implementation("io.github.scwang90:refresh-footer-classics:3.0.0-alpha")    //经典加载

    implementation("com.github.mmin18:realtimeblurview:1.2.1")
//    implementation("com.flyco.roundview:FlycoRoundView_Lib:1.1.4@aar")
//    implementation("io.github.h07000223:flycoTabLayout:3.0.0")
    implementation("com.wang.avi:library:2.1.3")
//    implementation("com.youth.banner:banner:2.0.1")
    implementation("io.github.youth5201314:banner:2.2.3")


//    implementation(project(":refresh-header"))
}