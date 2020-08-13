package com.houserent.all;

import com.houserent.dao.EmployerDao;

import java.sql.*;
import java.util.Scanner;

public class Employer extends User{
    /**插入新的出租房信息预备语句*/
    private final String InsertHouse="insert into House values(?,?,?,?,?,?,?,0,1)";
    /**查询已出租的房屋信息预备语句*/
    private final String QueryRentedHouse="select * from House where Employer_ID=?";
    /**查询名下租客信息预备语句*/
    private final String QueryTenants="select Tenant_ID,Name,PhonerNumber from\n"+
                                      "Rent a join Tenant b on a.Tenant_ID=b.Tenant_ID\n"+
                                      "join House c on a.House_ID=c.House_ID\n"+
                                      "where House.Employer_ID=?";
    /**查询租用历史预备语句*/
    private final String QueryRentHistory="select * from Rent_History\n" +
                                          "where Employer_ID=?";
    /**查询租客预备语句*/
    private final String QueryMyTenants="select r.Tenant_ID,h.House_ID\n" +
                                        "from Employer e join House h on e.Employer_ID = h.Employer_ID\n" +
                                        "     join Rent r on h.House_ID = r.House_ID\n" +
                                        "where e.Employer_ID=?";
    /**查询房屋评价预备语句*/
    private final String QueryHouseComment="select Content,Tenant_ID,Comment_Time\n" +
                                           "from House_comment\n" +
                                           "where House_ID=?";
    
    Employer(String ID, String Password) {
        super(ID, Password);
    }
    private EmployerDao employerDao=new EmployerDao();
    /**
     * 雇主的主界面
     * @throws SQLException
     * @description 雇主的主界面，显示提供给雇主的功能，通过输入对应的序号，进入对应的功能模块
     * @date 2020-7-28
     */
    public void ShowMain() throws SQLException
    {
        System.out.println("                        欢迎来到雇主用户界面");
        System.out.println("1.出租房屋   2.查看我出租的房屋  3.查看我的租客  4.查看我的出租历史  5.退出系统");
        Scanner scanner=new Scanner(System.in);
        int FunctionSelection=scanner.nextInt();
        FunctionSelect(FunctionSelection);
    }
    
    /**
     * 选择调用方法
     * @param n 不同的n选择调用不同的方法
     * @throws SQLException
     * @date 2020-7-28
     */
    void FunctionSelect(int n)  throws SQLException
    {
        switch (n)
        {
            case 1:RentHouse();break;
            case 2:CheckRentedHouse();break;
            case 3:CheckTenants();break;
            case 4:CheckRentHistory();break;
            case 5:System.exit(1);
        }
    }
    
    /**
     * 出租房屋功能
     * @throws SQLException
     * @description 雇主输入选择出租自己的房屋，需要输入房屋的信息，自动生成房屋ID
     * @date 2020-7-28
     */
    void RentHouse() throws SQLException
    {
        String Position,Disciption1,Disciption3;
        int Floor;
        double Price;
        Scanner scanner=new Scanner(System.in);
        System.out.println("     出租房屋");
        System.out.print(" 请输入房屋的位置：");
        Position=scanner.nextLine();
        System.out.print(" 请输入房屋类型：");
        Disciption1=scanner.nextLine();
        System.out.print(" 请输入房屋的楼层：");
        Floor=scanner.nextInt();
        scanner.nextLine(); //由于nextInt不读取流中int后面的回车符，这里用nextLine舍弃这个回车符，否则会对下面的输入造成影响（下同）
        System.out.print(" 请输入房屋的描述：");
        Disciption3=scanner.nextLine();
        System.out.print(" 请输入房屋租用的价格（元/月）：");
        Price=scanner.nextDouble();
        /**检测输入数据的合法性，输入的价格必须为一个正数*/
        while(Price<0)
        {
            System.out.println("价格必须为正数，请重新输入：");
            Price=scanner.nextDouble();
        }

        String HouseID=null;
        Object[] param={HouseID,super.getID(),Position,Price,Disciption1,Floor,Disciption3};

        employerDao.updateHouse(InsertHouse,param);
        /**
         * 建立数据库连接，向数据中插入对应的房屋信息
         被DAO代替
        try(Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
        {
            Statement statement=connection.createStatement();

            ResultSet resultSet=statement.executeQuery("select max(House_ID) from House");
            resultSet.next();
            String MaxHouseID=resultSet.getString(1);
            if(MaxHouseID==null) MaxHouseID="00000000"; //最小房间号为 00000001
            int n=Integer.valueOf(MaxHouseID)+1;
            String HouseID=String.valueOf(n);

            while(HouseID.length()<8)
            {
                HouseID="0"+HouseID;
            }
            
            PreparedStatement preparedStatement=connection.prepareStatement(InsertHouse);
            preparedStatement.setString(1,HouseID);
            preparedStatement.setString(2,super.getID());
            preparedStatement.setString(3,Position);
            preparedStatement.setDouble(4,Price);
            preparedStatement.setString(5,Disciption1);
            preparedStatement.setInt(6,Floor);
            preparedStatement.setString(7,Disciption3);
            preparedStatement.executeUpdate();
        }
         */
        System.out.println("出租房屋成功");
        ShowMain();
    }
    
    /**
     * 查看出租房屋功能
     * @throws SQLException
     * @description 显示自己名下已发布的房屋的详情信息
     * @date 2020-7-28
     */
    void CheckRentedHouse() throws SQLException
    {

//        try(Connection connection=DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
//        {
//            PreparedStatement preparedStatement=connection.prepareStatement(QueryRentedHouse);
//            preparedStatement.setString(1,super.getID());
//            ResultSet resultSet=preparedStatement.executeQuery();
//
//            /**
//             * 遍历查询到的房屋结果集，依次访问每一行，打印出房屋的信息
//             */
//            while(resultSet.next())
//            {
//                System.out.println("房屋ID："+resultSet.getString(1));
//                System.out.println("位置："+resultSet.getString(3));
//                System.out.println("出租价格："+resultSet.getString(4)+"元每月");
//                System.out.println("房屋类型："+resultSet.getString(5));
//                System.out.println("楼层："+resultSet.getString(6));
//                System.out.println("描述："+resultSet.getString(7));
//                System.out.print("出租情况：");
//                if(resultSet.getByte(8)==0) System.out.println("该房屋未出租");
//                else System.out.println("该房屋已出租");
//                QueryHouseComment(resultSet.getString(1),connection);
//                System.out.println("");
//            }
//        }
        employerDao.CheckRentedHouse(QueryRentedHouse,super.getID());
        ShowMain();
    }
    
    /**
     * 查看租客功能
     * @throws SQLException
     * @description 查看自己当前所有的租客
     * @date 2020-7-29
     */
    void CheckTenants() throws SQLException
    {

        try(Connection connection=DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
        {
            PreparedStatement preparedStatement=connection.prepareStatement(QueryMyTenants);
            preparedStatement.setString(1,super.getID());
            ResultSet resultSet=preparedStatement.executeQuery();
            /**
             * 查找名下的租客，遍历查找结果集的每一行，打印租客信息
             */
            if(resultSet.next()==false)
            {
                System.out.println("您没有任何租客");
            }else{
                System.out.println("您的所有租客如下");
                do{
                    System.out.println("租客ID："+resultSet.getString(1)+"  "+"租用的房间ID："+resultSet.getString(2));
                }while(resultSet.next());
            }
        }
        ShowMain();
    }
    
    /**
     * 查看出租历史功能
     * @throws SQLException
     * @description 查看自己所有出租房屋的历史记录，包括现在正在进行的
     * @date 2020-7-29
     */
    void CheckRentHistory() throws SQLException
    {
        try (Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
        {
            PreparedStatement preparedStatement=connection.prepareStatement(QueryRentHistory);
            preparedStatement.setString(1,super.getID());
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()==false)
            {
                System.out.println("您没有任何租用历史");
            }
            else
            {
                do {
                    System.out.println("租客ID:"+resultSet.getString(2)+"  "+
                            "起始时间："+resultSet.getDate(3)+"  "+"结束时间："+resultSet.getDate(4)
                            +"  "+"总金额："+resultSet.getDouble(5)
                    );
                }while (resultSet.next());
            }
        }
        ShowMain();
    }
    
    /**
     * 查询房间评论
     * @param HouseID 需要查询评价的房间ID
     * @param connection 数据库连接
     * @throws SQLException
     * @description 通过房间的ID号查询该房间的评论
     */
    void QueryHouseComment(String HouseID,Connection connection) throws SQLException
    {
        PreparedStatement preparedStatement=connection.prepareStatement(QueryHouseComment);
        preparedStatement.setString(1,HouseID);
        ResultSet resultSet=preparedStatement.executeQuery();
        /**
         * 遍历评论查找结果集，打印评论信息
         */
        System.out.println("out");
        if(resultSet.next())
        {
            System.out.println("in");
            System.out.println("该房屋收到如下评论：");
            do {
                System.out.println("租客ID-"+resultSet.getString(2)+"  "+"评论时间-"+resultSet.getDate(3)+"  评论内容："+resultSet.getString(1));
            }while(resultSet.next());
        }
    }
}
