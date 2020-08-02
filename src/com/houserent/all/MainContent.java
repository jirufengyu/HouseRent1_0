package com.houserent.all;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 租房系统初始主界面类
 * @description 主界面，用来选择用户注册、租客登录、雇主登录功能，其中包括了用户注册方法，用户雇主登录功能需要创建新的类以降低耦合性
 * @date 2020-7-28
 */
public class MainContent
{
    /**注册成功后插入用户数据的预备语句*/
    private final String InsertUser1="insert into Tenant values(?,?,?,?)";
    private final String InsertUser2="insert into Employer values(?,?,?,?,?)";
    
    
    MainContent() throws SQLException {
    
    }
    
    /**
     * 租房系统的初始界面
     * @throws SQLException
     * @decription 在此方法能通过调用其他方法实现对应的功能（注册、登录、退出）
     * @date 2020-7-28
     */
    public void ShowMain() throws SQLException
    {
        System.out.println("         欢迎来到房屋租赁系统");
        System.out.println("1.租客登录   2.雇主登录    3.用户注册   4.退出");
        Scanner scanner=new Scanner(System.in);
        int FunctionSelection=scanner.nextInt();
        while(FunctionSelection>4)
        {
            System.out.println("您的输入有误，请重新输入");
            FunctionSelection=scanner.nextInt();
        }
        /**输入数字选择不同的功能*/
        switch (FunctionSelection)
        {
            case 1: case 2:LogIn(FunctionSelection);break;
            case 3: Regester();break;
            case 4:System.exit(0);
        }
    }
    
    /**
     * 注册功能
     * @throws SQLException
     * @description 完成租客、雇主的注册，其中租客ID、雇主ID分别存储在数据库不同的表中
     * @date 2020-7-28
     */
    void Regester() throws SQLException {
        int Type=03;
        String UserName,PassWord,PhoneNumber;
        Scanner scanner=new Scanner(System.in);
        System.out.println("请输入注册的用户类型：1.租客 2.雇主");
        Type=scanner.nextInt();
        scanner.nextLine();
        
        System.out.println("请输入你的姓名：");
        UserName=scanner.nextLine();
        /**数据合法性检测，姓名长度不能超过15个字符*/
        while(UserName.length()>15)
        {
            System.out.println("姓名不能多于15个字符，请重新输入：");
            UserName=scanner.nextLine();
        }
        
        System.out.println("请输入你的密码：");
        PassWord=scanner.nextLine();
        /**数据合法性检测，密码必须为8位*/
        while(PassWord.length()<8||PassWord.length()>16)
        {
            System.out.println("密码长度必须为8-16，请重新输入：");
            PassWord=scanner.nextLine();
        }

        /*11位电话号码，使用了正则表达式*/
        System.out.println("请输入你的手机号码：");
        PhoneNumber = scanner.nextLine();
        boolean flag = true;
        String regExp = "^[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(PhoneNumber);
        flag = m.find();//boolean
        while(flag!=true){
            System.out.println("你输入的手机号格式不正确，请重新输入：");
            PhoneNumber = scanner.nextLine();
            m = p.matcher(PhoneNumber);
            flag = m.find();//boolean
        }

        try(Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord())){//查询手机是否已被注册
            PreparedStatement preparedStatement=null;
            if(Type==1){
                preparedStatement = connection.prepareStatement("select * from Tenant where PhoneNumber=?");
            }
            else if(Type==2){
                preparedStatement = connection.prepareStatement("select * from Employer where PhoneNumber=?");
            }

            preparedStatement.setString(1,PhoneNumber);
            ResultSet resultSet=null;
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                System.out.println("手机号码已被注册");
                this.Regester();
            }

        }






//上面的正则表达式来判断是否是手机号
//        if(PhoneNumber.length()>21) flag=false;
//        else {
//            for (int i = 0; i < PhoneNumber.length(); ++i) {
//                if (PhoneNumber.charAt(i) < '0' || PhoneNumber.charAt(i) > '9') {
//                    flag = false;
//                    break;
//                }
//            }
//        }
//        /**数据合法性检测，电话号码不能包含除数字0~9以外的字符*/
//        while(flag==false)
//        {
//            System.out.println("你输入的手机号格式不正确，请重新输入：");
//            PhoneNumber=scanner.nextLine();
//            flag=true;
//            if(PhoneNumber.length()>21) flag=false;
//            else {
//                for (int i = 0; i < PhoneNumber.length(); ++i) {
//                    if (PhoneNumber.charAt(i) < '0' || PhoneNumber.charAt(i) > '9') {
//                        flag = false;
//                        break;
//                    }
//                }
//            }
//        }

        /**
         * 注册信息输入完成后，建立数据库链接
         * 向数据库响应的用户表中插入用户信息
         * 根据已有的最大ID分配新ID，ID为Max(ID)+1
         */
        try(Connection connection= DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
        {
            String MaxUserId;
            Statement statement=connection.createStatement();
            ResultSet resultSet;
            if(Type==1) resultSet=statement.executeQuery("select max(Tenant_ID) from Tenant");
            else resultSet=statement.executeQuery("select max(Employer_ID) from Employer");
            resultSet.next();
            MaxUserId=resultSet.getString(1);
            //System.out.println(MaxUserId);
            if(MaxUserId==null) MaxUserId="10000000";
            int n=Integer.valueOf(MaxUserId)+1;
            String UserId=String.valueOf(n);
            //System.out.println(UserId);
            //System.out.println(UserId);
            /**
             * if-else分别为向租客、雇主表格中插入数据
             */
            if(Type==1)
            {
                PreparedStatement preparedStatement=connection.prepareStatement(InsertUser1);
                preparedStatement.setString(1,UserId);
                preparedStatement.setString(2,UserName);
                preparedStatement.setString(3,PassWord);
                preparedStatement.setString(4,PhoneNumber);
                preparedStatement.executeUpdate();
                System.out.println("注册成功，您的ID为："+UserId);
                this.ShowMain();
            }
            else
            {
                PreparedStatement preparedStatement=connection.prepareStatement(InsertUser2);
                preparedStatement.setString(1,UserId);
                preparedStatement.setString(2,UserName);
                preparedStatement.setString(3,null);
                preparedStatement.setString(4,PassWord);
                preparedStatement.setString(5,PhoneNumber);
                preparedStatement.executeUpdate();
                System.out.println("注册成功，您的ID为："+UserId);
                this.ShowMain();
            }
        }
    }
    
    /**
     * 登录功能
     * @param n  1对应租客  2对应雇主
     * @throws SQLException
     * @date 2020-7-28
     * @description 通过参数判断，完成租客或雇主的登录
     */
    void LogIn(int n)throws SQLException
    {
        String ID,InputPassword;
        Scanner scanner=new Scanner(System.in);
        System.out.println("请输入您的手机/ID:");
        ID=scanner.nextLine();
        System.out.println("请输入您的密码:");
        InputPassword=scanner.nextLine();
        /**
         * 建立数据库连接
         * 根据输入的ID或手机请求对应的密码
         * 对比实际密码和输入的密码判断是否登录成功
         */
        try(Connection connection=DriverManager.getConnection(Main.GetURL(),Main.GetUser(),Main.GetPassWord()))
            {
            String RealPassword;
            PreparedStatement preparedStatement0=null;//查询ID
            PreparedStatement preparedStatement1=null;//查询手机
            if(n==1) {
                preparedStatement0 = connection.prepareStatement("select Password from Tenant where Tenant_ID=?");
                preparedStatement1 = connection.prepareStatement("select Password from Tenant where PhoneNumber =?");
            }
            else
            {
                preparedStatement0 = connection.prepareStatement("select Password from Employer where Employer_ID=?");
                preparedStatement1 = connection.prepareStatement("select Password from Employer where PhoneNumber=?");
            }
            preparedStatement0.setString(1,ID);
            preparedStatement1.setString(1,ID);
            ResultSet resultSet=null;
            if(ID.length()==8){//查询ID
                resultSet=preparedStatement0.executeQuery();
            }
            else{//查询手机
                resultSet=preparedStatement1.executeQuery();
            }


            if(resultSet.next()==false)
            {
                System.out.println("请输入正确的ID/手机和密码");
                this.LogIn(n);
            }
            else {
                RealPassword=resultSet.getString(1);
                if ( RealPassword.equals(InputPassword) == false) {
                    System.out.println("请输入正确的ID/手机和密码");
                    this.LogIn(n);
                } else {
                    System.out.println("登录成功");
                    if (n == 1) TenantFunction(ID, InputPassword);
                    else EmployerFunction(ID, InputPassword);
                }
            }
        }
    }
    
    /**
     * 租客用户功能
     * @param ID 租客ID
     * @param Password  密码
     * @description 初始化一个租客对象，然后可以调用租客对应的方法，实现功能
     * @date 2020-7-28
     */
    void TenantFunction(String ID, String Password) throws SQLException
    {
        Tenant tenant=new Tenant(ID,Password);
        tenant.ShowMain();
    }
    
    /**
     *雇主用户功能
     * @param ID 雇主ID
     * @param Password  密码
     * @throws SQLException
     * @description 初始化一个雇主对象，然后可以调用租客对应的方法，实现功能
     * @date 2020-7-28
     */
    void EmployerFunction(String ID, String Password) throws SQLException
    {
        Employer employer=new Employer(ID,Password);
        employer.ShowMain();
    }
}