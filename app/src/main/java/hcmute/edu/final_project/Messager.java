package hcmute.edu.final_project;

import android.graphics.Bitmap;

public class Messager {
    public static String SEND_BY_ME ="me";
    public static String SEND_BY_BOT ="bot";

    String messager;
    String sendBy ;

    Bitmap bitmap;

    //constructor


    public Messager(String messager, String sendBy) {
        this.messager = messager;
        this.sendBy = sendBy;
    }

    public Messager( String sendBy, Bitmap bitmap) {
        this.sendBy = sendBy;
        this.bitmap = bitmap;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
