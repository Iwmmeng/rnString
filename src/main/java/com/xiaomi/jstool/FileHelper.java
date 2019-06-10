package com.xiaomi.jstool;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
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
            LOGGER.info("创建单个文件 {} 失败，目标文件已存在！",destFileName);
        }
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            FileHelper.createDir(file.getParentFile().toString());
        }else {
            //todo 删除该文件夹！！！！！！
            Boolean flag1 = deleteDir(file.getParentFile());
LOGGER.info("删除目标文件所在的目录 is true ? {}",flag1);
            Boolean flag2= FileHelper.createDir(file.getParentFile().toString());
            LOGGER.info("重新创建目录 is true ? {}",flag2);
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

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    @Test
    public void testDelete(){
        File  path =new File("/Users/huamiumiu/Desktop/rn框架/report");
        System.out.println(deleteDir(path));

    }
@Test
    public void test01(){
        String path = "/Users/huamiumiu/Desktop/rn框架";
    String outputPath =path+"/"+"report/"+"0000000.js".replace(".js","")+".txt";

    System.out.println(createFile(outputPath));

}




    }
