#include "com_sadgames_sysutils_common_RG_ETC1_JniWrapper.h"
#include "rg_etc1.h"

JNIEXPORT void JNICALL Java_com_sadgames_sysutils_common_RG_1ETC1_1JniWrapper_packETC1BlockInit
        (JNIEnv *, jclass) {

    rg_etc1::pack_etc1_block_init();
}

JNIEXPORT jint JNICALL Java_com_sadgames_sysutils_common_RG_1ETC1_1JniWrapper_packETC1Block
        (JNIEnv *, jclass, jintArray, jintArray) {

    return (jint)1;
}

JNIEXPORT jboolean JNICALL Java_com_sadgames_sysutils_common_RG_1ETC1_1JniWrapper_unpackETC1Block
        (JNIEnv *, jclass, jintArray, jintArray) {

    return (jboolean) true;
}