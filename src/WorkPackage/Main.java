package WorkPackage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * 程序入口
 */
public class Main {
    
    /**
     * 静态连接信息，设置为静态便于每个类获取该连接
     */
    private static final String URL="jdbc:mysql://39.97.176.127:3306/houserent";
    private static final String User="houserenter";
    private static final String PassWord="123455";
    public  static void main(String[] argv) throws SQLException {
        new MainContent().ShowMain();
    }
    static String GetURL()
    {
        return URL;
    }
    static String GetUser()
    {
        return User;
    }
    static String GetPassWord()
    {
        return PassWord;
    }
}

