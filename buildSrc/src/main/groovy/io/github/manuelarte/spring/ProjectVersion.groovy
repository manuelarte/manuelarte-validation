package io.github.manuelarte.spring

class ProjectVersion {
    Integer major
    Integer minor
    Integer bugFix
    Boolean release

    ProjectVersion(Integer major, Integer minor, Integer bugFix) {
        this(major, minor, bugFix, Boolean.FALSE)
    }

    ProjectVersion(Integer major, Integer minor, Integer bugFix, Boolean release) {
        this.major = major
        this.minor = minor
        this.bugFix = bugFix
        this.release = release
    }

    static ProjectVersion readVersion(File versionFile, gitDetails) {
        Properties versionProps = new Properties()
        versionFile.withInputStream { stream -> versionProps.load(stream) }
        def release = Boolean.FALSE
        if (versionProps.release != null) {
            release = versionProps.release.toBoolean()
        } else {
            if (gitDetails.branchName == 'master') {
                release = Boolean.TRUE
            }
        }
        new ProjectVersion(versionProps.major.toInteger(), versionProps.minor.toInteger(),
                versionProps.bugFix.toInteger(), release)
    }

    @Override
    String toString() {
        "$major.$minor.$bugFix${release ? '' : '-SNAPSHOT'}"
    }
}