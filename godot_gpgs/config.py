def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] != 'android'):
		return

	env.android_add_dependency(
		"compile 'com.google.android.gms:play-services:11.0.4'")
	env.android_add_default_config(
	    "compileSdkVersion 26")
	env.android_add_java_dir("android")
	# Some of Manifest entries must be added manualy
        # to the <application /> section, see the file listed below
	env.android_add_to_manifest("android/AndroidManifestChunk.xml")
	env.disable_module()
