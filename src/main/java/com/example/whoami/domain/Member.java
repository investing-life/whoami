package com.example.whoami.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_number")
    private int indexNumber;

    @Column(unique = true)
    private String memberId;

    private String password;

    private String sex;

    private String age;

    private String email;

    private String oauth;

    @Column(name = "join_date", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate joinDate;

    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    @Column(updatable = false, insertable = false)
    private boolean admin;

    private boolean deleted;

    private float openness;
    private float conscientiousness;
    private float extraversion;
    private float agreeableness;
    private float neuroticism;
    @Column(name = "test_result")
    private String testResult;

    @Column(name = "new_question")
    private boolean newQuestion;

    @Column(name = "new_answer")
    private boolean newAnswer;

    @Column(name = "new_notice")
    private boolean newNotice;

    public Member() {}

    public Member(String memberId, String password, String email) {
        this.memberId = memberId;
        this.password = password;
        this.email = email;
    }

    public int getIndexNumber() { return indexNumber; }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() { return sex; }

    public void setSex(String sex) { this.sex = sex; }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOauth() { return oauth; }

    public void setOauth(String oauth) { this.oauth = oauth; }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDateTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(LocalDateTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public boolean isNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(boolean newQuestion) {
        this.newQuestion = newQuestion;
    }

    public boolean isNewAnswer() {
        return newAnswer;
    }

    public void setNewAnswer(boolean newAnswer) {
        this.newAnswer = newAnswer;
    }

    public boolean isNewNotice() {
        return newNotice;
    }

    public void setNewNotice(boolean newNotice) {
        this.newNotice = newNotice;
    }
}
