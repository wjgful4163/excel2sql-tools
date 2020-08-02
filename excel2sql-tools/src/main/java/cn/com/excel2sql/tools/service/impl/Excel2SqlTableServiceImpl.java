package cn.com.excel2sql.tools.service.impl;

import cn.com.excel2sql.tools.service.Excel2SqlTableService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @version 1.0
 * @date 2020/8/118:38
 */
@Service
public class Excel2SqlTableServiceImpl implements Excel2SqlTableService {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    private Workbook workbook;

    @Override
    public Map<String, String> createMySqlTable(String path) throws Exception {
         /*DROP TABLE IF EXISTS table;
        CREATE TABLE PLS_CASE(
                ID bigint(20)  NOT NULL  COMMENT '主键',
                BIZ_TYPE char(2) NOT NULL  COMMENT '业务线',
                RULE_NAME varchar(64) NOT NULL  COMMENT '规则名称',
                CREATE_AT datetime DEFAULT NULL  COMMENT '创建时间',
                UPDATE_AT datetime DEFAULT NULL  COMMENT '更新时间',
                IS_DELETE char(1) DEFAULT NULL  COMMENT '是否删除',
                PRIMARY KEY (ID) USING BTREE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='案件分单规则表';

            create index IDX_NAME_TYPE ON PLS_CASE(RULE_NAME,BIZ_TYPE);
            */
        Map<String, String> sheetMap = new HashMap<>();
        if (StringUtils.isBlank(path)) {
            return sheetMap;
        }
        File file = new File(path);
        //检查文件是否为excel 并且创建sheet
        checkAndCreateSheet(file);
        if (workbook == null) {
            return sheetMap;
        }
        //文件名
        String excelName = file.getName().split("\\.")[0];

        //开始循环解析每一个sheet
        for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
            //读取sheet
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            int lastRowNum = sheet.getLastRowNum() + 1;
            int last = sheet.getPhysicalNumberOfRows();
            System.out.println(lastRowNum);
            System.out.println("物理行数--》" + last);
            StringBuilder sb = new StringBuilder();
            //表名
            String tableName = "";
            //主键
            String primaryKey = "";
            boolean isAppendEnd = false;

            //循环sheet中的内容
            for (int j = 1; j <= lastRowNum; ++j) {
                //行
                Row row = sheet.getRow(j);
                //表示出现的空格，需要拼接上创建主键内容
                if (row != null) {
                    Cell zeroCell = row.getCell(0);
                    Cell oneCell = row.getCell(1);

                    String indexZeroCell = zeroCell == null ? "" : zeroCell.toString();
                    String indexOneCell = oneCell == null ? "" : oneCell.toString();

                    //excel中第一个值为空，第二个值不为空的情况表示是表名
                    if (StringUtils.isBlank(indexZeroCell) && StringUtils.isNotBlank(indexOneCell)) {
                        tableName = indexOneCell;
                        isAppendEnd = true;
                        primaryKey = "";
                        sb.append("\n");
                        sb.append("DROP TABLE IF EXISTS  ");
                        sb.append(indexOneCell);
                        sb.append(";");
                        sb.append("\n");
                        //CREATE TABLE PLS_CASE_ASSIGN_RULE (
                        sb.append("CREATE TABLE ");
                        sb.append(indexOneCell);
                        sb.append(" (");
                        sb.append("\n");
                    }
                    //excel中第一个值不为空时开始为表的字段
                    if (StringUtils.isNotBlank(indexZeroCell) && !StringUtils.equals("INDEX", indexZeroCell.toUpperCase())) {
                        //ID bigint(20)  NOT NULL  COMMENT '主键',
                        //第四列表示 主键序号，存在序号则表示为主键
                        if (StringUtils.isNotBlank(row.getCell(4).toString())) {
                            //PRIMARY KEY (ID) USING BTREE
                            primaryKey = primaryKey + indexOneCell + ",";
                        }
                        //为了保持格式好看
                        indexOneCell = StringUtils.rightPad(indexOneCell, 36, " ");
                        sb.append("  ");
                        sb.append(indexOneCell);

                        //第三列表示 字段定义
                        String indexThreeCell = StringUtils.rightPad(row.getCell(3).toString(), 18, " ");
                        sb.append("  ");
                        sb.append(indexThreeCell);
                        String defaultValue = "";
                        //表示不能为空
                        if (StringUtils.isNotBlank(row.getCell(5).toString()) && "N".equals(row.getCell(5).toString().toUpperCase())) {
                            String sixCell = row.getCell(6).toString();
                            if (StringUtils.isNotBlank(sixCell)) {

                                if (isNumeric(sixCell)) {
                                    defaultValue = "DEFAULT " + Double.valueOf(sixCell).intValue();
                                } else {
                                    defaultValue = "DEFAULT " + sixCell;
                                }
                            } else {
                                defaultValue = "NOT NULL ";
                            }
                        } else {
                            //DEFAULT NULL
                            defaultValue = "DEFAULT NULL ";

                        }
                        sb.append(StringUtils.rightPad(defaultValue, 30, ""));
                        sb.append("COMMENT ");
                        sb.append("'");
                        //第二列表示 字段含义（中文解释）
                        sb.append(row.getCell(2).toString());
                        sb.append("',");
                        sb.append("\n");
                    }

                    if (StringUtils.isBlank(indexZeroCell) && StringUtils.isBlank(indexOneCell) && isAppendEnd) {
                    /*PRIMARY KEY (ID) USING BTREE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='案件分单规则表';*/
                        sb.append(appEndSql(primaryKey));
                        isAppendEnd = false;
                        //sb = new StringBuilder();
                    }
                    //第一个单元格为INDEX表示 需要创建的索引
                    if (StringUtils.isNotBlank(indexZeroCell) && StringUtils.equals("INDEX", indexZeroCell.toUpperCase())) {
                        //create index IDX_NAME_TYPE ON PLS_CASE_ASSIGN_RULE(RULE_NAME,BIZ_TYPE);

                        sb.append("CREATE INDEX ");
                        sb.append(indexOneCell);
                        sb.append(" ON ");
                        sb.append(tableName);
                        sb.append("(");
                        sb.append(row.getCell(3).toString());
                        sb.append(");");
                        sb.append("\n");
                    }


                } else {
                    if (isAppendEnd) {
                        sb.append(appEndSql(primaryKey));
                        isAppendEnd = false;
                    }

                }

            }
            //key将作为文件名
            sheetMap.put(excelName + "_" + sheetName, sb.toString());
            System.out.println(sb.toString());


        }
        return sheetMap;
    }

    private void checkAndCreateSheet(File file) throws Exception {

        //分割为文件名和后缀名
        System.out.println("文件名为--》" + file.getName());
        String[] split = file.getName().split("\\.");

        //根据文件名后缀创建Workbook (xls/xlsx)
        if (XLS.equals(split[1])) {
            FileInputStream excelFile = new FileInputStream(file);
            workbook = new HSSFWorkbook(excelFile);
        } else if (XLSX.equals(split[1])) {
            workbook = new XSSFWorkbook(file);
        } else {
            throw new Exception("文件类型错误，非excel文件类型!!!");
        }
    }

    /***
     * 判断字符串是否为数字（包含小数点或者非小数点）
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        if (StringUtils.isNumeric(str)) {
            return true;
        }
        Pattern pattern = Pattern.compile("[0-9]+.*[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    private String appEndSql(String primaryKey) {
        StringBuilder sb = new StringBuilder();
        String sqlStr = "";
        if (StringUtils.isNotBlank(primaryKey)) {
            sb.append("  PRIMARY KEY (");
            System.out.println("primaryKey-->" + primaryKey);
            if (primaryKey.contains(",")) {
                primaryKey = primaryKey.substring(0, primaryKey.lastIndexOf(","));
            }
            sb.append(primaryKey);
            sb.append(") USING BTREE");
            sb.append("\n");
        } else {
            sqlStr = sb.toString();
            sqlStr = sqlStr.substring(0, sqlStr.lastIndexOf(","));

        }
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 ;");
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }
}
