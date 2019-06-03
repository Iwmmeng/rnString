package com.xiaomi.jstool;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    //创建目录,args[0].getParent()
    public static boolean createDir(String destDirName){
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }



    //新创建的落盘文件需要跟输入路径的父文件路径一个级别，即需要跟输入路径在同一个级别
    //创建文件  传入路径为args[0].getParent()+fileName+.txt

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            FileHelper.createDir(file.getParentFile().toString());
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }



@Test
    public void test01(){
        String path = "/Users/huamiumiu/Desktop/rn框架";
    String outputPath =path+"/"+"report/"+"0000000.js".replace(".js","")+".txt";

    System.out.println(createFile(outputPath));

}




    }
