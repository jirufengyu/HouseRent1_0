package WorkPackage;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public class Tenant extends User{
    
    /**根据指定条件搜索房屋预备语句*/
    private final String SearchHouse="select * from House\n" +
                                     "where Position like ? and Floor >= ? and Floor <= ? and Price >= ? and Price <= ? and House_Type=? and isRented=0";
    /**租用房屋预备语句*/
    private final String RentHouse="insert into Rent values(?,?)";
    /**更新房屋租用标志预备语句*/
    private final String SetHouseRented="update House set isRented=1 where House_ID=?";
    /**更新租用历史表预备语句*/
    private final String UpdateRentHistory="insert into Rent_History values(?,?,?,?,?)";
    /**查看租用房屋预备语句*/
    private final String QueryRentedHouse="select h.House_ID,h.Position,h.Floor,h.House_Type,h.Price,h.Discription\n" +
                                          "from Rent join House h on Rent.House_ID = h.House_ID\n" +
                                          "where Rent.Tenant_ID=?";
    /**查询租用历史*/
    private final String QueryRentHistory="select * from Rent_History\n" +
                                          "where Tenant_ID=?";
    /**房屋退租预备语句*/
    private final String DeleteRent="delete from Rent where House_ID=?";
    private final String RentBackHouse="update House set isRented=0 where House_ID=?";
    /**房屋评论预备语句*/
    private final String CommentHouse="insert into House_Comment values(?,?,?,?)";
    /**查询房屋评价预备语句*/
    private final String QueryHouseComment="select Content,Tenant_ID,Comment_Time\n" +
            "from House_Comment\n" +
            "where House_ID=?";
    
    Tenant(String ID, String Password) {
        super(ID, Password);
    }
    
    /**
     * 租客的主界面
     * @throws SQLException
     * @description 租客的主界面，显示提供给租客的功能，通过输入对应的序号，进入对应的功能模块
     * @date 2020-7-28
     */

    public void ShowMain() throws SQLException
    {
        System.out.println("              欢迎来到租客用户界面");
        System.out.println("1.租用房屋   2.房屋退租  3.查看我租用的房屋  4.查看租用历史  5.退出系统");
        Scanner scanner=new Scanner(System.in);
        int FunctionSelection=scanner.nextInt();
        scanner.nextLine();
        FunctionSelect(FunctionSelection);
    }
    
    /**
     * 选择调用方法
     * @param n 不同的n选择调用不同的方法
     * @throws SQLException
     * @date 2020-7-29
     */
    public void FunctionSelect(int n)  throws SQLException
    {
        switch (n)
        {
            case 1:RentHouse();break;
            case 2:HouseRentBack();break;
            case 3:CheckMyHouse();break;
            case 4:CheckRentHistory();break;
            case 5:System.exit(0);
        }
    }
    
    /**
     * 搜索租用房屋功能
     * @throws SQLException
     * @description 通过输入具体期望的房间的条件来筛选房间，然后给定对应的房屋ID来租用房屋
     * @date 2020-7-29
     */
    void RentHouse() throws SQLException
    {
        String Posintion,Type,HouseId;
        double Price1,Price2;
        int Floor1,Floor2;
        Scanner scanner=new Scanner(System.in);
        System.out.println("欢迎来到租用房屋界面");
        System.out.print("请输入预期的位置：");
        Posintion=scanner.nextLine();
        System.out.println("请输入预期的价格区间");
        System.out.print("最低价格：");
        Price1=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("最高价格：");
        Price2=scanner.nextDouble();
        scanner.nextLine();
        System.out.print("请输入期望的户型：");
        Type=scanner.nextLine();
        System.out.println("请输入你期望的楼层区间");
        System.out.print("最低楼层：");
        Floor1=scanner.nextInt();
        scanner.nextLine();
        System.out.print("最高楼层：");
        Floor2=scanner.nextInt();
        scanner.nextLine();
        Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord());
        connection.setAutoCommit(false);
        try
        {
            /**
             * 执行询问查找符合条件的房屋
             */
            PreparedStatement preparedStatement=connection.prepareStatement(SearchHouse);
            preparedStatement.setString(1,'%'+Posintion+'%');
            preparedStatement.setInt(2,Floor1);
            preparedStatement.setInt(3,Floor2);
            preparedStatement.setDouble(4,Price1);
            preparedStatement.setDouble(5,Price2);
            preparedStatement.setString(6,Type);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next())
            {
                System.out.println("符合条件的未出租的房屋有：");
                System.out.println("房屋ID："+resultSet.getString(1));
                System.out.println("雇主ID："+resultSet.getString(2));
                System.out.println("位置："+resultSet.getString(3));
                System.out.println("出租价格："+resultSet.getString(4)+"元每月");
                System.out.println("房屋类型："+resultSet.getString(5));
                System.out.println("楼层："+resultSet.getString(6));
                System.out.println("描述："+resultSet.getString(7));
                QueryHouseComment(resultSet.getString(1),connection);
                System.out.println("");
            }
            System.out.print("请输入需要租用的房屋的ID：");
            String SelectID=null;
            String EmployerID=null;
            double Price=0;
            int MouthCount;
            boolean flag=false;
            /**
             * 判断输入的房屋ID是否合法，输入的ID必须在符合条件的
             * 房间的ID集合中
             */
            while(flag==false) {
                SelectID = scanner.nextLine();
                resultSet.afterLast();
                while (resultSet.previous()) {
                    if (resultSet.getString(1).equals(SelectID)) {
                        flag = true;
                        EmployerID=resultSet.getString(2);
                        Price=resultSet.getDouble(4);
                        break;
                    }
                }
                if(flag==false)System.out.print("请输入正确的房屋ID：");
            }
            System.out.print("请输入租用的时长（月）：");
            MouthCount=scanner.nextInt();
            scanner.nextLine();
            /**
             * 租用成功，向租用关系（租客，房屋ID）表中插入数据
             */
            PreparedStatement preparedStatement2=connection.prepareStatement(RentHouse);
            preparedStatement2.setString(1,super.getID());
            preparedStatement2.setString(2,SelectID);
            preparedStatement2.executeUpdate();
            /**
             * 租用成功，更新房屋信息中的租用记录（isrented）为已租用
             */
            PreparedStatement preparedStatement3=connection.prepareStatement(SetHouseRented);
            preparedStatement3.setString(1,SelectID);
            preparedStatement3.executeUpdate();
            /**
             * 租用成功，向租用历史表(雇主ID，租客ID，起始时间，终止时间，总价）中插入数据
             * 其中终止时间初始化为，退租时再更新此属性
             */
            PreparedStatement preparedStatement4=connection.prepareStatement(UpdateRentHistory);
            preparedStatement4.setString(1,EmployerID);
            preparedStatement4.setString(2,super.getID());
            Date CurrentDate=Date.valueOf(LocalDate.now());
            Date EndDate=Date.valueOf(LocalDate.now().plus(Period.ofMonths(MouthCount)));
            preparedStatement4.setDate(3,CurrentDate);
            preparedStatement4.setDate(4,EndDate);
            preparedStatement4.setDouble(5,Price*MouthCount);
            preparedStatement4.executeUpdate();
            System.out.println("您已成功租用"+SelectID+"号房");
            connection.commit();
            ShowMain();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            connection.rollback(); //该方法涉及多个数据库操作，需要保证其原子性，捕获异常时进行回滚（下同）
        }
        finally {
            connection.close();
        }
    }
    
    /**
     * 房屋退租功能
     * @throws SQLException
     * @description 房屋退租，从租用表格中删除相关数据，设置房屋属性isrented为0
     * @date 2020-7-29
     */
    void HouseRentBack() throws SQLException
    {
        Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord());
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(QueryRentedHouse);
            preparedStatement.setString(1, super.getID());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() == false) {
                System.out.println("您还没有租用任何房屋");
            } else {
                do {
                    System.out.println("房屋ID：" + resultSet.getString(1));
                    System.out.println("位置：" + resultSet.getString(2));
                    System.out.println("楼层：" + resultSet.getString(3));
                    System.out.println("类型：" + resultSet.getString(4));
                    System.out.println("价格：" + resultSet.getDouble(5) + "元/月");
                    System.out.println("描述：" + resultSet.getString(6));
                    System.out.println(" ");
                } while (resultSet.next());
            }
            String RentBackID = null;
            Scanner scanner = new Scanner(System.in);
            boolean flag = false;
            System.out.print("请输入你想要退租的房屋ID：");
            /**
             * 判断输入的房屋ID是否合法，输入的ID必须在符合条件的
             * 房间的ID集合中
             */
            while (flag == false) {
                RentBackID = scanner.nextLine();
                resultSet.afterLast();
                while (resultSet.previous()) {
                    if (resultSet.getString(1).equals(RentBackID)) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false) System.out.print("请输入正确的房屋ID：");
            }
            PreparedStatement preparedStatement1=connection.prepareStatement(DeleteRent);
            preparedStatement1.setString(1,RentBackID);
            preparedStatement1.executeUpdate();
            PreparedStatement preparedStatement2=connection.prepareStatement(RentBackHouse);
            preparedStatement2.setString(1,RentBackID);
            preparedStatement2.executeUpdate();
            System.out.println("您已成功退租"+RentBackID+"号房屋");
            System.out.println("是否对此房屋进行评价？1.是 2.否");
            int n=scanner.nextInt();
            scanner.nextLine();
            if(n==1) HouseComment(RentBackID,connection);
            connection.commit();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
            connection.rollback();
        }
        finally {
            connection.close();
        }
        ShowMain();
    }
    
    /**
     * 查看租用房屋功能
     * @throws SQLException
     * @description 查看名下已经正在租用的房屋的功能，显示房屋的详情信息
     * @date 2020-7-29
     */
    void CheckMyHouse() throws SQLException
    {
        try(Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
        {
            PreparedStatement preparedStatement=connection.prepareStatement(QueryRentedHouse);
            preparedStatement.setString(1,super.getID());
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()==false)
            {
                System.out.println("您还没有租用任何房屋");
            }
            else
            {
                do {
                System.out.println("房屋ID："+resultSet.getString(1));
                System.out.println("位置："+resultSet.getString(2));
                System.out.println("楼层："+resultSet.getString(3));
                System.out.println("类型："+resultSet.getString(4));
                System.out.println("价格："+resultSet.getDouble(5)+"元/月");
                System.out.println("描述："+resultSet.getString(6));
                System.out.println(" ");
                }while(resultSet.next());
            }
        }
        ShowMain();
    }
    
    /**
     * 查看租用历史功能
     * @throws SQLException
     * @description 查看自己所有租用房屋的历史记录，包括现在正在进行的
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
                    System.out.println("房主ID:"+resultSet.getString(1)+"  "+
                            "起始时间："+resultSet.getDate(3)+"  "+"结束时间："+resultSet.getDate(4)
                            +"  "+"总金额："+resultSet.getDouble(5)
                    );
                }while (resultSet.next());
            }
        }
        ShowMain();
    }
    
    /**
     * 房屋评论功能
     * @param HouseID 需要评论房屋的ID
     * @throws SQLException
     * @description 根据房屋ID号对房屋添加评论
     * @date 2020-7-29
     */
    void HouseComment(String HouseID,Connection connection) throws SQLException
    {
        Scanner scanner=new Scanner(System.in);
        String Comment=null;
        System.out.println("请输入您的评论：");
        Comment=scanner.nextLine();
        PreparedStatement preparedStatement=connection.prepareStatement(CommentHouse);
        preparedStatement.setString(1,HouseID);
        preparedStatement.setString(2,super.getID());
        preparedStatement.setDate(3,Date.valueOf(LocalDate.now()));
        preparedStatement.setString(4,Comment);
        preparedStatement.executeUpdate();
        System.out.println("你的评论已成功提交");
        ShowMain();
    }
    void QueryHouseComment(String HouseID,Connection connection) throws SQLException
    {
        PreparedStatement preparedStatement=connection.prepareStatement(QueryHouseComment);
        preparedStatement.setString(1,HouseID);
        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next())
        {
            System.out.println("该房屋收到如下评论：");
            do {
                System.out.println("租客ID-"+resultSet.getString(2)+"  "+"评论时间-"+resultSet.getDate(3)+"  评论内容："+resultSet.getString(1));
            }while(resultSet.next());
        }
    }
}
