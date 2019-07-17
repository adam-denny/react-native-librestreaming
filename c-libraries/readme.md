NOTES ON COMPILING LIBRARIES

Needed:

install a version of the android ndk that uses gcc - this one was compiled with android-ndk-r14b. 
Newer versions of the ndk no longer contain gcc, but use clang++ instead. These libraries aren't set up to compile using clang++,
so if that ever becomes needed, you will need to edit the makefiles to enable that.

Also, if you need to rebuild libx264, you will need to edit the .sh files for each architecture to point to the correct location
of your android-ndk (the one built into Android Studio will not work owing to the lack of gcc).

Built the libs for arm, arm64, x86 and x86_64 on 7/17/19 Adam Denny
