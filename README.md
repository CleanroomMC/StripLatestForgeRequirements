## StripLatestForgeRequirements

This is intended purely for dev environments where a mod that you depend on, explicitly depends on a Forge version that's above 2847.

### Usage:

Add the following dependency in `build.gradle`
```java
repositories {
    maven {
        name 'CleanroomMC Maven'
        url 'https://maven.cleanroommc.com'
    }
}

dependencies {
    runtimeOnly 'com.cleanroommc:strip-latest-forge-requirements:1.0'
}
```