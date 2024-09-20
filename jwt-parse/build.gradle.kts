import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

/**
 * 使用插件之前需要先声明
 */
plugins {
    application
    jacoco
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.dorongold.task-tree") version "3.0.0"
    id("idea")
}

// 项目坐标
group = "com.laolang.jx"
version = "0.1"

/**
 * 声明启动类
 */
application {
    mainClass = "com.laolang.jx.JwtParseApplication"
}

// 配置依赖
dependencies {

    implementation("io.jsonwebtoken:jjwt:0.12.5")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    implementation("cn.hutool:hutool-all:5.8.11")
    implementation("com.google.guava:guava:23.0")
    implementation("org.apache.commons:commons-lang3:3.15.0")

    implementation("ch.qos.logback:logback-classic:1.2.12")

    testImplementation("org.testng:testng:6.14.3")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
}

/**
 * 打包时生成 source.jar 和 javadoc.jar
 */
configure<JavaPluginExtension> {
    withSourcesJar()
    withJavadocJar()
}

/**
 * java 编译配置
 */
tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

/**
 * javadoc
 */
tasks.withType<Javadoc> {
    options {
        encoding = Charsets.UTF_8.name()
        charset(Charsets.UTF_8.name())
    }
    // 忽略 javadoc 报错
    isFailOnError = false
}

/**
 * 测试任务
 */
tasks.named<Test>("test") {
    useTestNG {
        suites("testng.xml")
    }
//    useTestNG()
    // 输出详细日志
    testLogging {
        // 记录日志的事件类型
        events("FAILED", "PASSED", "SKIPPED", "STANDARD_ERROR", "STANDARD_OUT", "STARTED")
        // 记录测试异常的格式
        // FULL: 完整显示异常
        // SHORT: 异常的简短显示
        exceptionFormat = TestExceptionFormat.FULL
        // 是否记录标准输出和标准错误的输出
        showStandardStreams = true
    }

    finalizedBy(tasks.jacocoTestReport)
}

/**
 * jacoco 任务
 */
tasks.named<JacocoReport>("jacocoTestReport") {
    // 依赖于测试任务
    dependsOn(tasks.test)

    reports {
        // 把不需要的报告去掉
        xml.required.set(false)
        csv.required.set(false)

        // 只启用 html 报告
        html.required.set(true)

        // jacoco 报告位置
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

/**
 * 打包可执行 jar
 */
tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveFileName.set(project.name + ".jar")

    destinationDirectory.set(layout.buildDirectory.dir("shaded"))
}

/**
 * idea 下载源码
 */
idea.module {
    isDownloadJavadoc = true
    isDownloadSources = true
}