package aromatherapy.saiyi.cn.jinhaojiao.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/19.
 */
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 8809870642386137570L;
    private String heartrate;
    private String Time;
    private String steps;
    private String distance;
    private String speed;
    private String Calorie;
    private String equipmentID;

    public String getEquipmentID() {
        return equipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        this.equipmentID = equipmentID;
    }

    public DeviceInfo() {
    }

    public String getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    public String getCalorie() {
        return Calorie;
    }

    public void setCalorie(String calorie) {
        Calorie = calorie;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
