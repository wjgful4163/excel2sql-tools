package cn.com.excel2sql.tools.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件信息处理
 *
 * @author Administrator
 * @version 1.0
 * @date 2020/8/118:54
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 获取指定文件路径下所有文件
     *
     * @param directoryPath  指定目录地址
     * @param isAddSubFolder 是否获取子文件夹下的文件
     * @return
     */
    public static List<String> getFolderAllFile(String directoryPath, boolean isAddSubFolder) {
        System.out.println("目录地址-->"+directoryPath);
        List<String> allFileList=new ArrayList<>();
        //读取路径文件
        File baseFile = new File(directoryPath);
        if(baseFile.isFile()||!baseFile.exists()){
            return allFileList;
        }
        //获取当前路径下所有文件
        File[] files = baseFile.listFiles();
        for(File file:files){
            if(isAddSubFolder&&file.isDirectory()){
                allFileList.addAll(getFolderAllFile(file.getAbsolutePath(),isAddSubFolder));
            }

            if(file.isFile()){
                String[] split = file.getName().split("\\.");
                //xls/xlsx
                if(split.length>1&&("xls".equals(split[1])||"xlsx".equals(split[1]))){
                    //存放在集合中的路径都是全路径
                    allFileList.add(file.getAbsolutePath());
                }

            }
        }
        return allFileList;
    }

    /**
     * 创建文件，并写入数据
     * @param directoryPath 目录地址
     * @param fileName 文件名称
     * @param data 数据
     */
    public static void createFileAndWriter(String directoryPath,String fileName,String data) throws Exception{
        File file = new File(directoryPath + "/" + fileName+".SQL");
        System.out.println(directoryPath + "/" + fileName);
        if(!file.exists()){
           file.createNewFile();
        }
        FileWriter  out = new FileWriter(file,false);
        System.out.println(data);
        out.write(data);
        out.flush();
        out.close();
    }
}
