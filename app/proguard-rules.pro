# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools.

# Keep ZXing classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# Keep model classes
-keep class com.estsb.smartattendance.models.** { *; }
