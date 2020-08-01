package WorkPackage;

public class User {
    private String ID;
    private String Password;
    private String Name;
    private String PhoneNumber;
    User(String ID,String Password)
    {
        this.ID=ID;
        this.Password=Password;
    }
    protected String getID()
    {
        return ID;
    }
    protected String getName()
    {
        return Name;
    }
    protected String getPhoneNumber()
    {
        return PhoneNumber;
    }
    public void showMain(){};
}
