package hcmute.edu.final_project;

public class Messager {
    public static String SEND_BY_ME ="me";
    public static String SEND_BY_BOT ="bot";

    String messager;
    String sendBy ;

    //constructor


    public Messager(String messager, String sendBy) {
        this.messager = messager;
        this.sendBy = sendBy;
    }

    public String getMessager() {
        return messager;
    }

    public void setMessager(String messager) {
        this.messager = messager;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}
