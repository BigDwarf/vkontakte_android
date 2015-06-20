package com.example.arslan.vkclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class WallPost implements Serializable{
    private String mFrom;
    private String mText;
    private String mAvatarURL;
    private ArrayList<String> mAttachments;
    private int mLikesCount;
    private Comment mComments;
    public WallPost(){

    };
    public String getFrom() {
        return mFrom;
    }

    public String getText() {
        return mText;
    }

    public String getAvatarURL() {
        return mAvatarURL;
    }

    public ArrayList<String> getAttachments() {
        return mAttachments;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public Comment getComments() {
        return mComments;
    }

    public void setFrom(String mFrom) {
        this.mFrom = mFrom;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setAvatarURL(String mAvatarURL) {
        this.mAvatarURL = mAvatarURL;
    }

    public void setAttachments(ArrayList<String> attachmentsURL) {
        this.mAttachments = attachmentsURL;
    }

    public void setLikesCount(int mLikesCount) {
        this.mLikesCount = mLikesCount;
    }

    public void setComments(Comment mComments) {
        this.mComments = mComments;
    }
    private void writeObject(ObjectOutputStream o)
            throws IOException {

        o.writeObject(mFrom);
        o.writeObject(mText);
        o.writeObject(mAvatarURL);
        o.writeObject(mAttachments);
    }
    private void readObject(ObjectInputStream o)
            throws IOException, ClassNotFoundException {
        mFrom= (String) o.readObject();
        mText = (String) o.readObject();
        mAvatarURL = (String)o.readObject();
        mAttachments = (ArrayList<String>)o.readObject();
    }
    public String toString(){
       return getFrom() + "  " + getText() + "  " + getAvatarURL();
    }
}
