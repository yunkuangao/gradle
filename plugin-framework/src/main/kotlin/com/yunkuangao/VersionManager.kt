package com.yunkuangao

interface VersionManager {

    fun checkVersionConstraint(version: String, constraint: String): Boolean

    fun compareVersions(v1: String, v2: String): Int
}