package com.GLocanicKT.locanic;

public class Dosya {
    private String dosyaAdi;
    private String yuklemeTarihi;




    public Dosya(String dosyaAdi, String yuklemeTarihi) {
        this.dosyaAdi = dosyaAdi;
        this.yuklemeTarihi = yuklemeTarihi;
    }

    public String getDosyaAdi() {
        return dosyaAdi;
    }

    public String getYuklemeTarihi() {
        return yuklemeTarihi;
    }
}
