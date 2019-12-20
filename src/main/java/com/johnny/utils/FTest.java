package com.johnny.utils;

public class FTest {
    public static void main(String args[]){
        System.out.println(System.currentTimeMillis());
        System.out.println(fab(45));
        System.out.println(System.currentTimeMillis());
    }

    private static int fab(int index){
        if(index==1 || index==2){
            return 1;
        }else{
            return fab(index-1)+fab(index-2);
        }
    }
}
