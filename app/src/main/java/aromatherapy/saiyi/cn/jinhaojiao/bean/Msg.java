package aromatherapy.saiyi.cn.jinhaojiao.bean;

/**
 * Created by omni20170501 on 2017/6/12.
 */

public class Msg {

    /**
     * resCode : 0
     * resMessage : 注册成功！
     * resBody : {}
     */

    private String resCode;
    private String resMessage;
    private ResBodyBean resBody;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMessage() {
        return resMessage;
    }

    public void setResMessage(String resMessage) {
        this.resMessage = resMessage;
    }

    public ResBodyBean getResBody() {
        return resBody;
    }

    public void setResBody(ResBodyBean resBody) {
        this.resBody = resBody;
    }

    public static class ResBodyBean {
    }

    @Override
    public String toString() {
        return "Msg{" +
                "resCode='" + resCode + '\'' +
                ", resMessage='" + resMessage + '\'' +
                ", resBody=" + resBody +
                '}';
    }
}
