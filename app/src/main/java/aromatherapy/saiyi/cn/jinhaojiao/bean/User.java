package aromatherapy.saiyi.cn.jinhaojiao.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -256678834854180321L;
    private String username;
    private String userID;
    private String password;
    private String address;
    private String head_pic;
    private String nikename;
    private String phone;
    private String sex;
    private int type;
    private String school;
    private String banji;
    private String identity;
    private String birthday;
    private String club;
    private String height;
    private String weight;
    private String equipmentID;
    private String equipment;
    private String openid;
    private boolean isLine;

    public boolean isLine() {
        return isLine;
    }

    public void setIsLine(boolean isLine) {
        this.isLine = isLine;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private Bitmap bitmap;

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public User(String password, String phone, int type) {
        this.password = password;
        this.phone = phone;
        this.type = type;
    }

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public User(String username, String identity, String banji, String school, int type, String sex, String nikename, String phone, String head_pic, String address, String password) {
        this.username = username;
        this.identity = identity;
        this.banji = banji;
        this.school = school;
        this.type = type;
        this.sex = sex;
        this.nikename = nikename;
        this.phone = phone;
        this.head_pic = head_pic;
        this.address = address;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getBanji() {
        return banji;
    }

    public void setBanji(String banji) {
        this.banji = banji;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public User() {
    }
}