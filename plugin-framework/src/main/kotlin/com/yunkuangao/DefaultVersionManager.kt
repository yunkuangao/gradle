package com.yunkuangao

import com.github.zafarkhaja.semver.Version

class DefaultVersionManager : VersionManager {

    override fun checkVersionConstraint(version: String, constraint: String): Boolean {
        return constraint.isEmpty() || "*" == constraint || Version.valueOf(version).satisfies(constraint)
    }

    override fun compareVersions(v1: String, v2: String): Int {
        return Version.valueOf(v1).compareTo(Version.valueOf(v2))
    }

}