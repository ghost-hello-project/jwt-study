import com.laolang.gradle.mavenAlibaba

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
