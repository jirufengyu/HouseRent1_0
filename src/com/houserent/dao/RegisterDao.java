package com.houserent.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 86133
 */
public class RegisterDao extends BaseDao {
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;
    public ResultSet getIsRegisted(String sql,String param){
        try {
            conn=getConn();
            pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,param);
            rs=pstmt.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            super.closeAll(conn,pstmt,rs);
        }
        return rs;
    }
    public int updateUser(String sql,Object[] param){
        int count=super.executeSQL(sql,param);
        return count;
    }
}
