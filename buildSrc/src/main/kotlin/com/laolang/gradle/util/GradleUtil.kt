package com.laolang.gradle.util

object GradleUtil {

    fun getProfile(defaultProfile: String): String {
        var profile: String? = System.getProperty("profile")
        if (profile.isNullOrBlank()) {
            profile = defaultProfile
        }
        return profile
    }
}