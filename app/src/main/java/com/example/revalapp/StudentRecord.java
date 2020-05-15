package com.example.revalapp;

public class StudentRecord {
    private String name;
    private String code;
    private String grade;
    private String newGrade;

    private StudentRecord() {}

    private StudentRecord(String name, String code, String grade, String newGrade) {
        this.name = name;
        this.code = code;
        this.grade = grade;
        this.newGrade = newGrade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNewGrade() {
        return newGrade;
    }

    public void setNewGrade(String newGrade) {
        this.newGrade = newGrade;
    }
}
