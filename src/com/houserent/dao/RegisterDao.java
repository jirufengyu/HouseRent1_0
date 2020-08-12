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
    public Boolean getIsRegisted(String sql,String param){
        Boolean re=null;
        try {
            conn=getConn();
            pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,param);
            rs=pstmt.executeQuery();
            re=rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            super.closeAll(conn,pstmt,rs);
        }

        return re;
    }

    /**
     * 输入电话返回ID
     * @param Phone
     * @return ID
     */
    public String getPhone2ID(String sql,String Phone){
        String ID=null;
        try {
            conn=getConn();
            pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,Phone);
            rs=pstmt.executeQuery();
            rs.next();
            ID=rs.getString(1);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            super.closeAll(conn,pstmt,rs);
        }

        return ID;
    }
    public int updateUser(String sql,Object[] param){
        int count=super.executeSQL(sql,param);
        return count;
    }
}
