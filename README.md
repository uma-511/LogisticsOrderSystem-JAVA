##新建数据库
新建一个空的数据库，例如：losys

##修改配置 
1.用解压缩工具打开 losys.war 

2.修改文件 losys.war\WEB-INF\classes\config\custom\db.properties

    #jdbc:mysql://[ip]:[port]/[数据库名称]?useUnicode=true&characterEncoding=utf8
    db.url=jdbc:mysql://localhost:3306/losys?useUnicode=true&characterEncoding=utf8
    #数据库用户名
    db.username=root
    #数据库密码
    db.password=123456
    
    db.validationQuery=select 1
    db.maxActive=100
    db.testWhileIdle=true
    db.filters=mergeStat
    db.connectionProperties=druid.stat.slowSqlMillis=2000
    db.defaultAutoCommit=true

3.把修改好的配置文件覆盖原来的文件

##把losys.war复制到tomcat/webapp文件夹

##运行tomcat