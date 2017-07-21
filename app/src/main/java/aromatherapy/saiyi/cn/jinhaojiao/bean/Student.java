package aromatherapy.saiyi.cn.jinhaojiao.bean;

import java.io.Serializable;

/**
 * Created by ys on 2017/7/8.
 */

public class Student extends User implements Serializable {
    private String heartrate;
    private String Time;
    private String steps;
    private String distance;
    private String speed;
    private String Calorie;
    private String equipmentID;
    private String strength;
    private String load;
    private String sportMin;

    public String getSportMin() {
        return sportMin;
    }

    public void setSportMin(String sportMin) {
        this.sportMin = sportMin;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getCalorie() {
        return Calorie;
    }

    public void setCalorie(String calorie) {
        Calorie = calorie;
    }

    @Override
    public String getEquipmentID() {
        return equipmentID;
    }

    @Override
    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }
}
