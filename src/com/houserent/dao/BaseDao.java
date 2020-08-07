package com.houserent.dao;
import java.sql.*;
import java.io.*;
import java.util.*;

/**
 * @author 86133
 */
public class BaseDao {
    public static String DRIVER;
    public static String URL="jdbc:mysql://39.97.176.127:3306/houserent";
    public static String DBNAME="houserenter";
    public static String DBPASS="123455";
    Connection conn=null;//数据库连接对象
    static{
        init();
    }
    public static void init(){
//        Properties params=new Properties();
//        String config="database.properties";
//        InputStream is=BaseDao.class.getClassLoader().getResourceAsStream(config);
//        try{
//            params.load(is);
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        DRIVER=params.getProperty("driver");
//        System.out.println(DRIVER);
//        URL=params.getProperty("url");
//        DBNAME=params.getProperty("dbname");
//        DBPASS=params.getProperty("dbpass");
    }

    /**
     *
     * @return 数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConn() throws  ClassNotFoundException,SQLException{
        Connection conn=null;
        try{
            conn=DriverManager.getConnection(URL,DBNAME,DBPASS);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }
    /**
     * 释放资源
     * @param conn     数据库连接
     * @param pstmt     PreparedStatement对象
     * @param rs    结果集
     */
    public void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs) {

        /* 如果rs不空，关闭rs */
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        /* 如果pstmt不空，关闭pstmt */
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        /* 如果conn不空，关闭conn */
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 执行SQL语句，可以进行增、删、改的操作，不能执行查询
     * @param preparedSql     预编译的 SQL 语句
     * @param param     预编译的 SQL 语句中的‘？’参数的字符串数组
     * @return 影响的条数
     */
    public int executeSQL(String preparedSql, Object[] param) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int num = 0;
        /* 处理SQL,执行SQL */
        try {
            conn = getConn(); // 得到数据库连接
            pstmt = conn.prepareStatement(preparedSql); // 得到PreparedStatement对象
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 为预编译sql设置参数
                }
            }

            num = pstmt.executeUpdate(); // 执行SQL语句
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // 处理ClassNotFoundException异常
        } catch (SQLException e) {
            e.printStackTrace(); // 处理SQLException异常
        } finally {
            this.closeAll(conn, pstmt, null);
        }
        return num;
    }

}
