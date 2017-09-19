def can_build(plat):
    return plat=="android"

def configure(env):
    if (env['platform'] != 'android'):
        return

    env.android_add_dependency(
        "compile 'com.google.android.gms:play-services:11.0.4'")
    # You need to change applicationId to your games pacakge unique ID
    # If you are to use the module for more then one project.
    # If you don't change it you'll encounter an error
    # during the installation of an apk
    env.android_add_default_config(
        "applicationId 'com.your.app.id'")
    env.android_add_default_config(
        "compileSdkVersion 26")
    env.android_add_java_dir("android")
    env.android_add_to_manifest("android/AndroidManifestChunk.xml")
    env.disable_module()
