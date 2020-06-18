package com.nids.data;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class VOUser implements Serializable {
    private String id;
    private String pw;
    private String name;
    private String age;
    private String phone;
    private int gender;
    private Timestamp signup;
    private String auth;
    private String platform;
    private String bd;
    private String email;

    public VOUser() {
    }

    private String getEncMD5(String txt){
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(txt.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(0xFF & b);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }


    public VOUser(String id, String pw, String name, String age,  String phone, int gender, String platform, String bd, String email) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.platform = platform;
        this.bd = bd;
        this.email = email;
        this.auth = getEncMD5(this.id).toUpperCase();
    }


    public String getId(){ return this.id; }
    public void setId(String id){ this.id = id; }
    public String getPw(){ return this.pw; }
    public void setPw(String pw){ this.pw = pw; }
    public String getName() { return this.name; }
    public void setName(String name) {this.name = name;}
    public String getAge() {return this.age;}
    public void setAge(String age){this.age = age;}

    public String getPhone(){return this.phone;}
    public void setPhone(String phone){this.phone = phone;}
    public int getGender(){return this.gender;}
    public void setGender(int gender){this.gender=gender;}
    public Timestamp getSignup(){return this.signup;}
    public void setSignup(Timestamp signup){this.signup=signup;}
    public String getAuth(){return this.auth;}
    public void setAuth(String auth){this.auth = auth;}
    public String getPlatform(){ return this.platform; }
    public void setPlatform(String platform){ this.platform = platform; }
    public String getBd(){ return this.bd; }
    public void setBd(String bd){ this.bd = bd; }
    public String getEmail(){ return this.email; }
    public void setEmail(String email){ this.email = email; }

}