package com.sadgames.sysutils.common;

public class ArraysUtils {

    public static short chain(int j, int i, int imax){
        return (short) (i+j*(imax+1));
    }

    public static int coord2idx(int i, int j, int i_max, int el_size){
        return j * (i_max + 1) * el_size + i * el_size;
    }

}
