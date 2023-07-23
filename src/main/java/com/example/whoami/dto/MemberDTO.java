package com.example.whoami.dto;

public class MemberDTO {

    private int indexNumber;
    private String id;
    private String password;
    private String sex;
    private String age;
    private String email;

    private float openness;
    private float conscientiousness;
    private float extraversion;
    private float agreeableness;
    private float neuroticism;
    private String testResult;

    public MemberDTO() {}

    public MemberDTO(String id, String password, String email) {
        this.id = id;
        this.password = password;
        this.email = email;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getOpenness() {
        return openness;
    }

    public void setOpenness(float openness) {
        this.openness = openness;
    }

    public float getConscientiousness() {
        return conscientiousness;
    }

    public void setConscientiousness(float conscientiousness) {
        this.conscientiousness = conscientiousness;
    }

    public float getExtraversion() {
        return extraversion;
    }

    public void setExtraversion(float extraversion) {
        this.extraversion = extraversion;
    }

    public float getAgreeableness() {
        return agreeableness;
    }

    public void setAgreeableness(float agreeableness) {
        this.agreeableness = agreeableness;
    }

    public float getNeuroticism() {
        return neuroticism;
    }

    public void setNeuroticism(float neuroticism) {
        this.neuroticism = neuroticism;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }
}
