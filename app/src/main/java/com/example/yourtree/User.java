package com.example.yourtree;

// 친구 유저 정보 클래스
public class User {

    String userID;
    String userPassword;
    String userName;
    String userBirth;
    String userIMG;


    public User(String userID, String userPassword, String userName, String userBirth, String IMG) {
        this.userID = userID;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userIMG = IMG;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserIMG() {return userIMG;}

    public void setUserIMG(String userIMG) {this.userIMG = userIMG;}

}
