package com.houserent.dao;

import java.sql.*;

/**
 * @author 86133
 */
public class EmployerDao extends BaseDao {
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;
    /**
     * 更新租出去的房屋
     * @param sql
     * @param param
     * @return
     */
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

    /**
     * 查询租出去的房屋
     * @param sql
     * @param param
     */
    public void CheckRentedHouse(String sql,String param){
        ResultSet resultSet=null;
        try {
            conn=getConn();
            PreparedStatement quest=conn.prepareStatement(sql);     //得到最大的house_ID
            quest.setString(1,param);
            resultSet=quest.executeQuery();
            while(resultSet.next())
            {
                System.out.println("房屋ID："+resultSet.getString(1));
                System.out.println("位置："+resultSet.getString(3));
                System.out.println("出租价格："+resultSet.getString(4)+"元每月");
                System.out.println("房屋类型："+resultSet.getString(5));
                System.out.println("楼层："+resultSet.getString(6));
                System.out.println("描述："+resultSet.getString(7));
                System.out.print("出租情况：");
                if(resultSet.getByte(8)==0) System.out.println("该房屋未出租");
                else System.out.println("该房屋已出租");
                QueryHouseComment(resultSet.getString(1));
                System.out.println("");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            super.closeAll(conn,pstmt,rs);
        }

    }
    public void QueryHouseComment(String HouseID) {
        try {


            String QueryHouseComment = "select Content,Tenant_ID,Comment_Time\n" +
                    "from House_Comment\n" +
                    "where House_ID=?";
            conn = getConn();
            PreparedStatement quest = conn.prepareStatement(QueryHouseComment);     //得到最大的house_ID
            PreparedStatement preparedStatement = conn.prepareStatement(QueryHouseComment);
            preparedStatement.setString(1, HouseID);
            ResultSet resultSet = preparedStatement.executeQuery();
            /**
             * 遍历评论查找结果集，打印评论信息
             */
            if (resultSet.next()) {
                System.out.println("in");
                System.out.println("该房屋收到如下评论：");
                do {
                    System.out.println("租客ID-" + resultSet.getString(2) + "  " + "评论时间-" + resultSet.getDate(3) + "  评论内容：" + resultSet.getString(1));
                } while (resultSet.next());
            }else{
                System.out.println("该房屋没有评论");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            super.closeAll(conn, pstmt, rs);
        }
    }
}
