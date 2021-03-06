package com.example.pleasehoyar;


import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mKey;
    public Upload(){
        //empty constructor
    }
    public Upload(String name,String imageurl){
        if (name.trim().equals("")){
            name="No Name";
        }
        mName=name;
        mImageUrl=imageurl;
    }
    @Exclude

    public String getKey() {
        return mKey;
    }
@Exclude
    public void setKey(String Key) {
        mKey = Key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
      mName=name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
