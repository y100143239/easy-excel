package com.itoolshub.easy.springjdbc;

import com.itoolshub.easy.convert.FuncitionConvertUtil;
import com.itoolshub.easy.model.ExcelHeader;
import com.itoolshub.easy.template.AbstractSpringJdbcTemplate;
import com.itoolshub.easy.util.ExcelExportUtil;

import org.h2.tools.RunScript;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * https://docs.spring.io/spring/docs/4.0.x/spring-framework-reference/html/jdbc.html
 * @author Quding Ding
 * @since 2017/11/30
 */
public class QueryAndExportByH2 extends AbstractSpringJdbcTemplate{

  @Before
  public void before() throws SQLException {
    // init中初始化连接
    init("db1.properties");
    RunScript.execute(getConn(),
        new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("data.sql")));
  }

  @Test
  public void testQueryListMap() {
    final List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from user");
    Assert.assertEquals(result.size(),2);
  }

  @Test
  public void testQueryAndExport() {
    //查询出结果,使用Map存储,当然dbUtils也支持bean存储
    final List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from user");

    //表格对应的表头,以及该表头的数据处理
    LinkedHashMap<String, ExcelHeader> header = new LinkedHashMap<>();
    header.put("id", ExcelHeader.create("用户id"));
    header.put("username", ExcelHeader.create("用户名"));
    header.put("email", ExcelHeader.create("用户邮箱"));
    header.put("avatar", ExcelHeader.create("用户头像"));
    //对于日期使用转换器,转换器为java8的Function函数实现
    header.put("last_login_date", ExcelHeader.create("用户上次登录时间", FuncitionConvertUtil.date2String));
    header.put("status", ExcelHeader.create("用户id"));
    header.put("role", ExcelHeader.create("用户id"));
    //对于日期使用转换器,转换器为java8的Function函数实现
    header.put("gmt_create", ExcelHeader.create("用户id",FuncitionConvertUtil.date2String));

    //导出表
    /**
     * 查出来的map直接导出表格
     */
    ExcelExportUtil.fromMap(result)
        .displayHeader(header)
        .excelType(ExcelExportUtil.ExcelFileType.XLS)
        .build("用户表")
        .writeTo("/tmp/test1.xls");
    /**
     * 导出多张表
     */
    ExcelExportUtil.fromMap(result)
        .displayHeader(header)
        .excelType(ExcelExportUtil.ExcelFileType.XLSX)
        .build("用户表1")
        .andFormMap(result)
        .displayHeader(header)
        .build("用户表2")
        .writeTo("/tmp/test2.xlsx");
  }

}
