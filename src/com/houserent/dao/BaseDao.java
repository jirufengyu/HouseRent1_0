package com.houserent.dao;
import java.sql.*;
import java.io.*;
import java.util.*;

/**
 * @author 86133
 */
public class BaseDao {
    public static String DRIVER;
    public static String URL;
    public static String DBNAME;
    public static String DBPASS;

    public static void init(){
        Properties params=new Properties();
        String config="database.properties";
        InputStream is=BaseDao.class.getClassLoader().getResourceAsStream(config);
        try{
            params.load(is);
        }catch (IOException e){
            e.printStackTrace();
        }
        DRIVER=params.getProperty("driver");
        URL=params.getProperty("url");
        DBNAME=params.getProperty("dbname");
        DBPASS=params.getProperty("dbpass");
    }
}
