package com.example.petsmate.table;

public class PetInfo {
    private String code;

    public String getCode() {
        return code;
    }
    public int getCodeInt() {
        int codeInt;
        try {
            codeInt= Integer.parseInt(code);
        }catch (Exception e) {
            codeInt = -1;
        }

        return codeInt;

    }

    public void setCode(String code) {
        this.code = code;
    }

    private String name;
    private String weight;
    private String ps;

    public PetInfo(String code, String name, String weight, String ps) {
        this.code = code;
        this.name = name;
        this.weight = weight;
        this.ps = ps;
    }

    public PetInfo(String name, String weight, String ps) {
        this.name = name; this.weight = weight; this.ps = ps;
    }

    public PetInfo() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public String getPs() {
        return ps;
    }
}
