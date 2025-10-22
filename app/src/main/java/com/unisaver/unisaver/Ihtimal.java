package com.unisaver.unisaver;

import java.util.Random;

public class Ihtimal {
	private double agno;
	private int topkredi;
	private Ders ders;
	private String yuksekNot;
	private double etkiDuzeyi;
	private Random r = new Random();

	public Ihtimal(double agno, int kredi, Ders ders) {
		this.agno = agno;
		this.topkredi = kredi;
		this.ders = ders;
		this.yuksekNot = "";
		this.etkiDuzeyi = 0;
	}

	public int newRandomize(String minHarf, String maxHarf, String istekHarf) {
		String dersNotu = ders.getHarfNotu();
		boolean tekrarliMi = true;
		if (ders.getHarfNotu().equals(MainActivity.getAppContext().getString(R.string.yok))) {
			dersNotu = Ders.getMinHarf();
			tekrarliMi = false;
		}
		if (!istekHarf.equals("")) {
			this.yuksekNot = istekHarf;
		} else {
			String[] harfler = Ders.getSortedLetters();
			int minIndex = 0, maxIndex = 0;
			for (int i = 0; i < harfler.length; i++) {
				if (harfler[i].equals(minHarf)) minIndex = i;
				else if (harfler[i].equals(maxHarf)) maxIndex = i;
			}
			int secilenHarfIndex = r.nextInt(minIndex - maxIndex + 1);
			secilenHarfIndex += maxIndex;
			this.yuksekNot = harfler[secilenHarfIndex];
		}
		int eklenecekKredi = tekrarliMi ? 0 : ders.getCredit();
		int kredi = tekrarliMi ? ders.getCredit() : 0;
		double point = Ders.getPoint(yuksekNot) * ders.getCredit() - Ders.getPoint(dersNotu) * kredi ;
		double puan = agno * topkredi;
		puan += point;
		puan /= (topkredi + eklenecekKredi);
		this.etkiDuzeyi = puan - agno;
		return eklenecekKredi;
	}

	public Ders getDers() {
		return ders;
	}

	public String getYuksekNot() {
		return yuksekNot;
	}

	public double getEtkiDuzeyi() {
		return etkiDuzeyi;
	}

	@Override
	public String toString() {
		return MainActivity.getAppContext().getString(R.string.ihtimal_tostring, ders.toString(), yuksekNot, etkiDuzeyi);
	}
}
