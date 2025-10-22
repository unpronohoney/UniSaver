package com.unisaver.unisaver;

public class DersTrans {
    private int dersNo;
    private String dersAdi;
    private int dersKredi;
    private String dersNotu;

    public DersTrans(int dersNo, String dersAdi, int dersKredi, String dersNotu) {
        this.dersNo = dersNo;
        this.dersAdi = dersAdi;
        this.dersKredi = dersKredi;
        this.dersNotu = dersNotu;
    }

    public int getDersNo() {
        return dersNo;
    }
    public String getDersAdi() {
        return dersAdi;
    }
    public int getDersKredi() {
        return dersKredi;
    }
    public String getDersNotu() {
        return dersNotu;
    }
    public void setDersNotu(String not) {
        this.dersNotu = not;
    }
    public void setDersNo(int a) {
        this.dersNo = a;
    }
}
