-keepattributes Signature
-keepclassmembers class test.next.constant.** {*;}
-keep class yourpackage.** { *; }
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-obfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt