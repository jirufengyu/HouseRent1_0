package com.houserent.dao;

import java.sql.*;

/**
 * @author 86133
 */
public class EmployerDao extends BaseDao {
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;
    public int updateHouse(String sql,Object[] param){
        String MaxHouseID=null;
        int count=0;
        try {
            conn=getConn();
            Statement quest=conn.createStatement();     //得到最大的house_ID
            ResultSet quere=quest.executeQuery("select max(House_ID) from House");
            quere.next();
            MaxHouseID=quere.getString(1);
            if(MaxHouseID==null) MaxHouseID="00000000"; //最小房间号为 00000001
            int n=Integer.valueOf(MaxHouseID)+1;
            String HouseID=String.valueOf(n);
            /**
             * 如果最大房间ID数字小于8位数，补齐
             */
            while(HouseID.length()<8)
            {
                HouseID="0"+HouseID;
            }
            param[0]=HouseID;
            count=super.executeSQL(sql,param);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            super.closeAll(conn,pstmt,rs);
        }

        return count;
    }
}
