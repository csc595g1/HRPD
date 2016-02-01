package edu.depaul.csc595.jarvis.profile.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLDataException;
import java.util.ArrayList;

/**
 * Created by Ed on 1/30/2016.
 */
public class UserLoginDataSource {

    private static final String TAG = "UserLoginDataSource";

    private SQLiteDatabase db;
    private UserLoginDataHelper helper;
    private String[] allColumns = {"user_email","user_pw","last_login_dttm","is_logged_on"};

    protected UserLoginDataSource(Context context){
        helper = new UserLoginDataHelper(context);
    }

    protected void open() throws SQLDataException{
        db = helper.getWritableDatabase();
    }

    protected void close() throws SQLDataException{
        db.close();
    }

    //check if flag=1 exists if so return that row, if more than 1 exists, logged everyone out.
    //will require stringbuilder to accommodate pass by reference
    protected void getCurrentUserIfExists(StringBuilder email, StringBuilder pw){

    }

    protected int getUserCount(String email){
        int returnInt = 0;
        String sql = "select count(*) as returnCount from " + UserLoginDataHelper.TABLE_NAME + " where "
                + UserLoginDataHelper.user_email + " = '" + email + "';";
        Cursor c = db.rawQuery(sql,new String[]{});
        if(c.moveToFirst()){
            returnInt = Integer.parseInt(c.getString(c.getColumnIndexOrThrow("returnCount")));
        }
        return returnInt;
    }

    protected void updateFlagForUser(String email, int flag){
        if(!(flag == 0 || flag == 1)){return;}
        String sql = "update " + UserLoginDataHelper.TABLE_NAME + " set " + UserLoginDataHelper.is_logged_on
                + " = " + flag + " where " + UserLoginDataHelper.user_email + " = '" + email + "';";
    }

    //check to ensure that logged in flag is only one.
    protected int getLoggedInCount(){
        int returnInt = 0;
        String sql = "select count(*) as returnCount from " + UserLoginDataHelper.TABLE_NAME + " where "
                + UserLoginDataHelper.is_logged_on + " = 1;";
        Cursor c = db.rawQuery(sql,new String[]{});
        if(c.moveToFirst()){
            returnInt = Integer.parseInt(c.getString(c.getColumnIndexOrThrow("returnCount")));
        }
        return returnInt;
    }

    protected void resetAllLoggedInFlags(){
        String sql = "update " + UserLoginDataHelper.TABLE_NAME + " set " + UserLoginDataHelper.is_logged_on + " = 0;";
        db.execSQL(sql);
    }

    protected void insertUserNameAndPassword(String email, String pw){

        if(getUserCount(email) < 1) {
            ContentValues values = new ContentValues();

            values.put(UserLoginDataHelper.user_email, email);
            values.put(UserLoginDataHelper.user_pw, pw);
            values.put(UserLoginDataHelper.last_login_dttm, "sysdate");
            values.put(UserLoginDataHelper.is_logged_on, 1);

            db.insert(UserLoginDataHelper.TABLE_NAME, null, values);
        }
    }

    protected ArrayList<User> returnAllUsersForAutoComplettion(){
        ArrayList<User> userList = new ArrayList<>();
        String sql = "select " + UserLoginDataHelper.user_email + " as email, "
                + UserLoginDataHelper.user_pw + " as pw from " + UserLoginDataHelper.TABLE_NAME+";";
        Cursor c = db.rawQuery(sql, new String[]{});
        if(c.moveToFirst()){
            do{
                User temp = new User();
                temp.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                temp.setPw(c.getString(c.getColumnIndexOrThrow("pw")));
                userList.add(temp);
            }
            while(c.moveToNext());
        }
        return userList;
    }

    protected void clearTable(){
        String sql = "delete from " + UserLoginDataHelper.TABLE_NAME + ";";
        db.execSQL(sql);
    }

    protected String encryptPw(String pw){
        // TODO: 1/30/2016 Do or call some encryption for pw
        return pw;
    }

    protected String decryptPw(String pw){
        // TODO: 1/30/2016 Do or call some decryption for pw
        return pw;
    }
}
