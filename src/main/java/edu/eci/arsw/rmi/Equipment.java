package edu.eci.arsw.rmi;

import java.io.Serializable;

public class Equipment implements Serializable {
    private String code;
    private String name;
    private String laboratory;
    private boolean reserved;

    public Equipment(String code, String name, String laboratory) {
        this.code = code; this.name = name;
        this.laboratory = laboratory; this.reserved = false;
    }

    public String getCode() { return code; }
    public boolean isReserved() { return reserved; }
    public void reserve() { this.reserved = true; }
    public void release() { this.reserved = false; }

    @Override
    public String toString() {
        return code + " | " + name + " | Lab: " + laboratory
                + " | " + (reserved ? "RESERVED" : "AVAILABLE");
    }
}