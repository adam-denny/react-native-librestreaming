#!/bin/sh

ANDROID_NDK=$HOME/android-ndk-r14b
SYSROOT=$ANDROID_NDK/platforms/android-21/arch-x86_64
CROSS_PREFIX=$ANDROID_NDK/toolchains/x86_64-4.9/prebuilt/darwin-x86_64/bin/x86_64-linux-android-
EXTRA_CFLAGS="-D__ANDROID__ -D__x86_64__"
EXTRA_LDFLAGS="-nostdlib"
PREFIX=`pwd`/libs/x86_64

./configure --prefix=$PREFIX \
        --host=x86_64-linux \
        --sysroot=$SYSROOT \
        --cross-prefix=$CROSS_PREFIX \
        --extra-cflags="$EXTRA_CFLAGS" \
        --extra-ldflags="$EXTRA_LDFLAGS" \
        --enable-pic \
        --enable-static \
        --enable-strip \
        --disable-cli \
        --disable-win32thread \
        --disable-avs \
        --disable-swscale \
        --disable-lavf \
        --disable-ffms \
        --disable-gpac \
        --disable-lsmash \
        --disable-opencl \
        --disable-asm

make clean
make STRIP= -j8 install || exit 1

cp -f $PREFIX/lib/libx264.a $PREFIX
rm -rf $PREFIX/include $PREFIX/lib $PREFIX/pkgconfig
