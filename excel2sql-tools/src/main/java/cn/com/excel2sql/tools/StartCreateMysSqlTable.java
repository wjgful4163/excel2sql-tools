package cn.com.excel2sql.tools;

import cn.com.excel2sql.tools.config.DirectoryPathConfig;
import cn.com.excel2sql.tools.service.Excel2SqlTableService;
import cn.com.excel2sql.tools.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 项目启动时自动执行开始创建表MysqlSql Table
 *
 * @author Administrator
 * @version 1.0
 * @date 2020/8/118:26
 */
@Component
public class StartCreateMysSqlTable implements ApplicationRunner {
    @Autowired
    private Excel2SqlTableService excel2SqlTableService;
    @Autowired
    private DirectoryPathConfig directoryPathConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("start...");
        dealAllFile();

    }

    private void dealAllFile() {
        System.out.println("start dealAllFile...");
        //List<String> allFilePathList = FileUtil.getFolderAllFile(directoryPathConfig.getDirectoryPath(), false);
        //String directoryPath = directoryPathConfig.getDirectoryPath();
        String directoryPath = "G:/test/aaa";
        List<String> allFilePathList = FileUtil.getFolderAllFile(directoryPath, false);
        for (String filePath : allFilePathList) {
            try {
                System.out.println("文件路径--》"+filePath);
                Map<String, String> mySqlTableMap = excel2SqlTableService.createMySqlTable(filePath);
                //写入文件中
                for (Map.Entry<String,String> map:mySqlTableMap.entrySet()){
                    FileUtil.createFileAndWriter(directoryPath,map.getKey(),map.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
