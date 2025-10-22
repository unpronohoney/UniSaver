package com.unisaver.unisaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BelgeliHesap {
    private TranskriptOkuyucu machine = null;
    public BelgeliHesap(String text) throws IOException {
        this.machine = new TranskriptOkuyucu(text);
    }



    public TranskriptOkuyucu getMachine() {
        return machine;
    }
    public List<DersTrans> createLessonsTable() {
        List<DersTrans> dersler = new ArrayList<>();
        int dersNo = 1;
        for (int i = 0; i < machine.getLessonPoints().size(); i++) {
            dersler.add(new DersTrans(dersNo, machine.getLessonNames().get(i),
                    machine.getCredits().get(i), machine.getLessonPoints().get(i)));
            dersNo++;
        }
        return dersler;
    }
}