package com.app.eventify.modal;

public class UserInformation
{
    private String userName, emailId, className, rollNo, mobileNo, profilePic;

    public UserInformation()
    {


    }



    public UserInformation(String userName, String emailId, String className, String rollNo, String mobileNo, String profilePic) {
        this.userName = userName;
        this.emailId = emailId;
        this.className = className;
        this.rollNo = rollNo;
        this.mobileNo = mobileNo;
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

}
