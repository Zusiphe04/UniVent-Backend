package za.ac.cput.util;

public class UserIdGenerator {
    private int currentId = 1000;
    private static UserIdGenerator instance;

    private UserIdGenerator(){

    }
    public static UserIdGenerator getInstance(){
        if (instance == null){
            instance = new UserIdGenerator();
        }
        return instance;
    }
    public int generateId(){
        return ++currentId;
    }
}
