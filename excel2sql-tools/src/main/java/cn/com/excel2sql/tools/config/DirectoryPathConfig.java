package cn.com.excel2sql.tools.config;

import org.springframework.stereotype.Component;

/**
 * 获取文件目录信息
 * @author Administrator
 * @version 1.0
 * @date 2020/8/118:41
 */
@Component
public class DirectoryPathConfig {
    private static final String OS_NAME="os.name";
    private static final String DOWS="dows";
    private static final String JAR="jar";

    /**
     * 获取与Jar包存放相同目录地址
     * @return
     */
    public String getDirectoryPath(){
        String directoryPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if(System.getProperty(OS_NAME).contains(DOWS)){
            //开头前面包含 file:/
            directoryPath=directoryPath.substring(6);
        }
        if(directoryPath.contains(JAR)){
            directoryPath=directoryPath.substring(0,directoryPath.lastIndexOf("."));
            return directoryPath.substring(0,directoryPath.lastIndexOf("/"));
        }
        return directoryPath.replace("target/classes/","");
    }
}
