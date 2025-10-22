package com.unisaver.unisaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TranskriptOkuyucu {
    private double agno;
    private int scale;
    private int cred;
    private double computedAgno;
    private int computedCred;
    private String ogrIsmi;
    private ArrayList<String> lessonNames = new ArrayList<>();
    private ArrayList<Integer> credits = new ArrayList<>();
    private ArrayList<Integer> aktss = new ArrayList<>();
    private ArrayList<String> lessonPoints = new ArrayList<>();
    private ArrayList<String> geriAlLessonNames = new ArrayList<>();
    private ArrayList<Integer> geriAlCredits = new ArrayList<>();
    private ArrayList<String> geriAlLessonPoints = new ArrayList<>();
    private HashMap<String, Double> notes = new HashMap<>();
    private TranskriptUser tu = null;

    public TranskriptOkuyucu(String text) {
        this.tu = new TranskriptUser(text);
    }
    public class TranskriptUser {

        public TranskriptUser(String text) {

            Pattern p = Pattern.compile("Doğum Tarihi\\s*\\n\\d+\\s*\\n\\d+\\s*\\n([A-ZÇĞİÖŞÜ ]+)");
            Matcher m = p.matcher(text);
            if (m.find())
                ogrIsmi = m.group(1);
            p = Pattern.compile(":Soyadı ([A-ZÇĞİÖŞÜ ]+)");
            m = p.matcher(text);
            if (m.find())
                ogrIsmi += " " + m.group(1);
            ogrIsmi = capatilize(ogrIsmi);
            ogrIsmi = ogrIsmi.substring(0, ogrIsmi.length()-1);
            System.out.println(ogrIsmi);
            p = Pattern.compile("Başarılan Kredi (\\d+)");
            m = p.matcher(text);
            if (m.find())
                cred = Integer.parseInt(m.group(1));
            System.out.println(cred);
            p = Pattern.compile("\\((\\d).0 Scale\\)\\s*\\n(\\d+.\\d+):");
            m = p.matcher(text);
            if (m.find()) {
                scale = Integer.parseInt(m.group(1));
                agno = Double.parseDouble(m.group(2));
            }
            extractTableData(text);
        }
    }

    private String capatilize(String str) {
        String[] words = str.split(" ");
        StringBuilder capitalizedText = new StringBuilder();
        Locale turkishLocale = new Locale.Builder().setLanguage("tr").setRegion("TR").build();

        for (String word : words) {
            if (!word.isEmpty()) {
                String capitalizedWord = word.substring(0, 1).toUpperCase(turkishLocale) +
                        word.substring(1).toLowerCase(turkishLocale);
                capitalizedText.append(capitalizedWord).append(" ");
            }
        }
        return capitalizedText.toString();
    }

    public void removeLesson(int row) {
        lessonNames.remove(row);
        lessonPoints.remove(row);
        credits.remove(row);
        compute();
    }

    public void updateNotes(String newNote, int row) {
        lessonPoints.set(row, newNote);
        compute();
    }

    public void addLesson(String name, int credit, String note) {
        lessonNames.add(name);
        lessonPoints.add(note);
        credits.add(credit);
        compute();
    }

    private void compute() {
        double gercekAgno = 0;
        int credTop = 0;
        for (int i = 0; i<credits.size(); i++) {
            gercekAgno += credits.get(i) * notes.get(lessonPoints.get(i));
            credTop += credits.get(i);
        }
        gercekAgno /= credTop;
        this.computedAgno = gercekAgno;
        this.computedCred = credTop;
    }
    public void gerAl() {
        lessonNames.clear();
        credits.clear();
        lessonPoints.clear();
        lessonNames.addAll(geriAlLessonNames);
        credits.addAll(geriAlCredits);
        lessonPoints.addAll(geriAlLessonPoints);
        compute();
    }

    public void extractTableData(String text) {
        String[] lines = text.split("\n"); // Satır bazlı ayırma

        Pattern patt = Pattern.compile(" ([A-Z\\+-]{1,2}) ([\\d,.]+) ");

        Matcher match;
        for (int i = 0; i<lines.length; i++) {
            if (lines[i].isEmpty()) continue;
            match = patt.matcher(lines[i]);
            if (match.find()) {
                String point = match.group(2);
                if (point.length() > 1 && point.charAt(1) == ',') {
                    StringBuilder sb = new StringBuilder(point);
                    sb.setCharAt(1, '.');
                    point = sb.toString();
                }
                notes.put(match.group(1), Double.parseDouble(point));
            }

        }
        /*for (String s : notes.keySet()) {
        	System.out.println(s + " " + notes.get(s));
        }*/
        patt = Pattern.compile(" \\d+ \\d+ (\\d+) (\\d+) \\d+([\\w.\\-+]+) ([\\w -]+)");
        //int kre = 0;
        boolean donem = false;
        boolean compute = false;
        double dno = 0;
        int credOrAkts = 0;
        for (int i = 0; i<lines.length; i++) {
            if (lines[i].isEmpty()) continue;
            if (lines[i].contains("(Comment)")) {donem = true; continue;}
            if (lines[i].contains("DNO:") && !lines[i].contains("Dönem Not Ortalaması")) {
                donem = false;
                compute = true;
                dno = Double.parseDouble(lines[i+2].substring(0, lines[i+2].length()-5));
                System.out.println(dno);
                continue;
            }
            if (donem) {
                match = patt.matcher(lines[i]);
                if (match.find() && match.group(3).charAt(0) != '-') {
                    credits.add(Integer.parseInt(match.group(1)));
                    aktss.add(Integer.parseInt(match.group(2)));
                    if (match.group(3).charAt(0) == '.') {
                        lessonPoints.add(match.group(3).substring(2));
                    } else {
                        lessonPoints.add(match.group(3));
                    }
                }
            }
            if (compute) {
                compute = false;
                if (dno == 0.0) {continue;}
                double dnodeneme1 = 0;
                double dnodeneme2 = 0;
                int aktsTop = 0;
                int credTop = 0;
                for (int j = 0; j<lessonPoints.size(); j++) {
                    dnodeneme1 += notes.get(lessonPoints.get(j)) * credits.get(j);
                    dnodeneme2 += notes.get(lessonPoints.get(j)) * aktss.get(j);
                    credTop += credits.get(j);
                    aktsTop += aktss.get(j);
                }
                dnodeneme1 /= credTop;
                dnodeneme2 /= aktsTop;
                dnodeneme1 = Math.round(dnodeneme1 * 100.0) / 100.0;
                dnodeneme2 = Math.round(dnodeneme2 * 100.0) / 100.0;
                credits.clear();
                aktss.clear();
                lessonPoints.clear();
                if (dnodeneme1 == dno && dnodeneme2 != dno) {
                    credOrAkts = 1;
                    break;
                } else if (dnodeneme2 == dno && dnodeneme1 != dno) {
                    credOrAkts = 2;
                    break;
                }
                //System.out.println(dnodeneme1 + " " + dnodeneme2 + " " + dno);
            }
        }
        //System.out.println(credOrAkts);
        int oncekiIndex = 0;
        boolean isLong = false;
        for (int j = 0; j<lines.length; j++) {
            if (lines[j].isEmpty()) continue;
            if (lines[j].contains("(Comment)")) {oncekiIndex = j+1; continue;}
            match = patt.matcher(lines[j]);
            if (match.find()) {
                if (!match.group(credOrAkts).equals("0") && match.group(3).charAt(0) != '-') {
                    if (match.group(4).contains("YRN") || lines[oncekiIndex].charAt(0) == '*') {oncekiIndex = j+1; continue;}
                    Matcher m = patt.matcher(lines[j-1]);
                    int index = -1;
                    if (m.find()) {
                        String kes = lines[j].substring(0, lines[j].length()-m.group(0).length());
                        int bos = 0;
                        int a = 0;
                        while (bos != 2) {
                            a++;
                            if (kes.charAt(kes.length()-a) == ' ') {
                                bos++;
                            }
                        }
                        String ad = kes.substring(0, kes.length()-a);
                        for (String name : lessonNames) {
                            if (name.equals(ad)) {
                                index = lessonNames.indexOf(name);
                                break;
                            }
                        }
                        if (index == -1) {
                            lessonNames.add(ad);
                        }
                        if (index == -1)
                            credits.add(Integer.parseInt(match.group(credOrAkts)));

                        if (match.group(3).charAt(0) == '.') {
                            if (index == -1)
                                lessonPoints.add(match.group(3).substring(2));
                            else
                                lessonPoints.set(index, match.group(3).substring(2));
                        } else {
                            if (index == -1)
                                lessonPoints.add(match.group(3));
                            else
                                lessonPoints.set(index, match.group(3));
                        }

                        oncekiIndex = j+1;
                        int cont = oncekiIndex;
                        while (!lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")")) {
                            oncekiIndex++;
                        }
                        if (lines[oncekiIndex+1].contains("(You can")) {
                            oncekiIndex += 2;
                            while (!lines[oncekiIndex-1].contains("YOK")) {
                                oncekiIndex++;
                            }
                        }
                        boolean b = false;
                        if (oncekiIndex != cont) {
                            if (lines[oncekiIndex+1].contains("(Comment)")) {
                                oncekiIndex++;
                            } else if ((!lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")"))){
                                oncekiIndex++;
                                b = true;
                            }
                            if (b && !lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")")) {
                                isLong = true;
                            }
                        }
                        continue;

                    }
                    if (isLong) {
                        isLong = false;
                        String uzun = "";
                        int[] indexs = new int[j-2-oncekiIndex+2];
                        for (int i = 0; i<indexs.length; i++) {
                            indexs[i] = oncekiIndex-1+i;
                        }
                        for (int i = 0; i<indexs.length; i++) {
                            uzun += lines[indexs[i]].substring(0, lines[indexs[i]].length()-1) + " ";
                        }
                        uzun += "\n";

                        for (String name : lessonNames) {
                            if (name.equals(uzun)) {
                                index = lessonNames.indexOf(name);
                                break;
                            }
                        }
                        if (index == -1) {
                            lessonNames.add(uzun);
                        }

                    } else {
                        String n = lines[oncekiIndex];

                        for (String name : lessonNames) {
                            if (name.equals(n)) {
                                index = lessonNames.indexOf(name);
                                break;
                            }
                        }
                        if (index == -1) {
                            lessonNames.add(n);
                        }
                    }
                    oncekiIndex = j+1;
                    if (index == -1)
                        credits.add(Integer.parseInt(match.group(credOrAkts)));

                    if (match.group(3).charAt(0) == '.') {
                        if (index == -1)
                            lessonPoints.add(match.group(3).substring(2));
                        else
                            lessonPoints.set(index, match.group(3).substring(2));
                    } else {
                        if (index == -1)
                            lessonPoints.add(match.group(3));
                        else
                            lessonPoints.set(index, match.group(3));
                    }

                    int cont = oncekiIndex;
                    while (!lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")")) {
                        oncekiIndex++;
                    }
                    if (lines[oncekiIndex+1].contains("(You can")) {
                        oncekiIndex += 2;
                        while (!lines[oncekiIndex-1].contains("YOK")) {
                            oncekiIndex++;
                        }
                    }
                    boolean a = false;
                    if (oncekiIndex != cont) {
                        if (lines[oncekiIndex+1].contains("(Comment)")) {
                            oncekiIndex++;
                        } else if ((!lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")"))){
                            oncekiIndex++;
                            a = true;
                        }
                        if (a && !lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")")) {
                            isLong = true;
                        }
                    }
                } else {
                    oncekiIndex = j+1;
                    int cont = oncekiIndex;
                    while (!lines[oncekiIndex+1].contains("(") && !lines[oncekiIndex+1].contains(")")) {
                        oncekiIndex++;
                    }
                    if (lines[oncekiIndex+1].contains("(You can")) {
                        oncekiIndex += 2;
                        while (!lines[oncekiIndex-1].contains("YOK")) {
                            oncekiIndex++;
                        }
                    }
                    if (oncekiIndex != cont) {
                        if (lines[oncekiIndex+1].contains("(Comment)")) {
                            oncekiIndex++;
                        } else {
                            isLong = true;
                        }
                    }
                }

            }
        }
        compute();

        geriAlLessonNames.addAll(lessonNames);
        geriAlCredits.addAll(credits);
        geriAlLessonPoints.addAll(lessonPoints);
    }

    public double getAgno() {
        return agno;
    }

    public int getScale() {
        return scale;
    }

    public int getCred() {
        return cred;
    }

    public double getComputedAgno() {
        return computedAgno;
    }

    public int getComputedCred() {
        return computedCred;
    }

    public String getOgrIsmi() {
        return ogrIsmi;
    }

    public ArrayList<String> getLessonNames() {
        return lessonNames;
    }

    public ArrayList<Integer> getCredits() {
        return credits;
    }

    public ArrayList<Integer> getAktss() {
        return aktss;
    }

    public ArrayList<String> getLessonPoints() {
        return lessonPoints;
    }

    public HashMap<String, Double> getNotes() {
        return notes;
    }
}
