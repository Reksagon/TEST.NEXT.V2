-keep class yourpackage.** { *; }
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-keepattributes Signature
-keepclassmembers class test.next.constant.** {
    *;
}

-obfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt