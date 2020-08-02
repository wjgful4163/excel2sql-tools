package cn.com.excel2sql.tools.service;

import java.util.Map;

/**
 * Excel表创建sql table
 * @author Administrator
 * @version 1.0
 * @date 2020/8/118:36
 */
public interface Excel2SqlTableService {

    Map<String, String> createMySqlTable(String path) throws Exception;
}
