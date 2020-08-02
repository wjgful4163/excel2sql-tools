# EXCEL自动生成SQL建表脚本

## 1.自动生成MySQL建表脚本

### 1.1 Excel规范要求

1.第一行内容为表头，描述对应的信息

2.表名前面的序号为空

3.索引前面的序号写INDEX（大小写都可以）

4.两个表之间空一行，索引与表也空一行

示例：

| 序号  | 字段名称             | 字段含义     | 字段定义             | 主键序号 | 允许为空 | 缺省值            | 备注信息 | 值域 |
| ----- | -------------------- | ------------ | -------------------- | -------- | -------- | ----------------- | -------- | ---- |
|       | PLS_CASE_CHANNEL     | 案件表       |                      |          |          |                   |          |      |
| 1     | CHANNEL_NAME         | 名称         | VARCHAR(32)          | 1        | N        |                   |          |      |
| 2     | CHANNEL_TYPE         | 类型         | VARCHAR(32)          | 2        | N        |                   |          |      |
| 3     | CHANNEL_URL          | 地址         | VARCHAR(2000)        |          | N        |                   |          |      |
| 4     | NOTES                | 备注         | VARCHAR(512)         |          |          |                   |          |      |
| 5     | REC_CREATE_TIME      | 创建时间     | TIMESTAMP            |          | N        | CURRENT_TIMESTAMP |          |      |
| 6     | REC_LAST_UPDATE_TIME | 最后更新时间 | TIMESTAMP            |          | N        | CURRENT_TIMESTAMP |          |      |
| 7     | REC_VERSION          | 版本号       | INTEGER              |          | N        | 0                 |          |      |
|       |                      |              |                      |          |          |                   |          |      |
| INDEX | IDX_LAST_TIME        |              | REC_LAST_UPDATE_TIME |          |          |                   |          |      |
|       |                      |              |                      |          |          |                   |          |      |
|       | PLS_CASE_COUNTER     | 消息计数器   |                      |          |          |                   | 计数器   |      |
| 1     | COUNTER_KEY          | 消息类型代码 | VARCHAR(64)          | 1        | N        |                   |          |      |
| 2     | COUNTER              | 计数器值     | INTEGER              |          |          |                   |          |      |
| 3     | REC_CREATE_TIME      | 创建时间     | TIMESTAMP            |          | N        | CURRENT_TIMESTAMP |          |      |
| 4     | REC_LAST_UPDATE_TIME | 最后更新时间 | TIMESTAMP            |          | N        | CURRENT_TIMESTAMP |          |      |
| 5     | REC_VERSION          | 版本号       | INTEGER              |          | N        | 0                 |          |      |

### 2.2 操作说明

1.excel和对应的jar包放在同一个文件夹中

2.双击startcreate.bat

