import com.laolang.gradle.Version
import com.laolang.gradle.mavenAlibaba
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

/**
 * 使用插件之前需要先声明
 */
plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    id("maven-publish")
    id("java")
    id("com.dorongold.task-tree") version "3.0.0"
}

/**
 * 所有工程的配置, 包含根项目
 */
allprojects {
    repositories {
        mavenAlibaba()
        mavenLocal()
        mavenCentral()
    }
}

/**
 * 所有子工程的配置, 不包含根项目
 */
subprojects {
    // 不处理主工程, 主工程单独写构建文件
    if (name != "jx-app") {
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")
        apply(plugin = "com.dorongold.task-tree")
        apply(plugin = "io.spring.dependency-management")

        group = "com.laolang.jx"
        version = "0.1"

        // 导入 Spring Boot 的 bom
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
        }

        // 注意这里 全局导入 lombok
        dependencies {
            compileOnly(rootProject.libs.lombok)
            annotationProcessor(rootProject.libs.lombok)
        }

        // 打包时生成 source.jar 和 javadoc.jar
        configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }

        /**
         * java 编译配置
         */
        tasks.withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            sourceCompatibility = Version.sourceCompatibility.toString()
            targetCompatibility = Version.targetCompatibility.toString()
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
         * 发布到本地
         */
        publishing {
            repositories {
                mavenLocal()
            }
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()

                    from(components["java"])
                }
            }
        }
    }
}

/**
 * 注意这里
 * 由于根项目添加了 spring-boot 插件, 所有根项目在打包时也会执行 bootJar
 * 而根项目又没有 java 启动类, 就会导致打包报错
 * 下面的配置可忽略根项目的 bootJar 任务
 */
tasks.bootJar {
    enabled = false
}