package com.unisaver.unisaver;

import java.util.ArrayList;

public class GenelNot{
	private int toplamKredi;
	private double currentAGNO;

	public GenelNot(int topKredi, double currentAGNO) {
		this.toplamKredi = topKredi;
		this.currentAGNO = currentAGNO;
	}

	public void yeniDers(int kredi, String letter) {
		currentAGNO *= toplamKredi;
		currentAGNO += (Ders.getPoint(letter) * kredi);
		toplamKredi += kredi;
		currentAGNO /= toplamKredi;
	}

	public void yeniKredi(int eskiKredi, String eskiHarf, String yeniHarf, int yeniKredi) {
		if (eskiHarf.equals(MainActivity.getAppContext().getString(R.string.yok))) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(yeniHarf) * eskiKredi);
			currentAGNO += (Ders.getPoint(yeniHarf) * yeniKredi);
			toplamKredi += yeniKredi - eskiKredi;
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO += (Ders.getPoint(eskiHarf) * eskiKredi);
			currentAGNO -= (Ders.getPoint(yeniHarf) * eskiKredi);
			currentAGNO -= (Ders.getPoint(eskiHarf) * yeniKredi);
			currentAGNO += (Ders.getPoint(yeniHarf) * yeniKredi);
			toplamKredi += yeniKredi - eskiKredi;
			currentAGNO /= toplamKredi;
		}
	}

	public void yeniEskiHarf(int kredi, String eskiHarf, String yeniEskiHarf) {
		if (eskiHarf.equals(MainActivity.getAppContext().getString(R.string.yok))) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(yeniEskiHarf) * kredi);
			toplamKredi -= kredi;
			currentAGNO /= toplamKredi;
		} else if (yeniEskiHarf.equals(MainActivity.getAppContext().getString(R.string.yok))) {
			currentAGNO *= toplamKredi;
			currentAGNO += (Ders.getPoint(eskiHarf) * kredi);
			toplamKredi += kredi;
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO += ((Ders.getPoint(eskiHarf) - Ders.getPoint(yeniEskiHarf)) * kredi);
			currentAGNO /= toplamKredi;
		}
	}

	public void dersSilme(Ders ders) {
		if (ders.getHarfNotu().equals(MainActivity.getAppContext().getString(R.string.yok))) {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(ders.getYeniHarf()) * ders.getCredit());
			toplamKredi -= ders.getCredit();
			currentAGNO /= toplamKredi;
		} else {
			currentAGNO *= toplamKredi;
			currentAGNO -= (Ders.getPoint(ders.getYeniHarf()) * ders.getCredit());
			currentAGNO += (Ders.getPoint(ders.getHarfNotu()) * ders.getCredit());
			currentAGNO /= toplamKredi;
		}
	}

	public void yeniYeniHarf(int kredi, String yeniHarf, String yeniYeniHarf) {
		currentAGNO *= toplamKredi;
		currentAGNO += ((Ders.getPoint(yeniYeniHarf) - Ders.getPoint(yeniHarf)) * kredi);
		currentAGNO /= toplamKredi;
	}

	public void eskiDers(int kredi, String eskiHarf, String yeniHarf) {
		currentAGNO *= toplamKredi;
		currentAGNO += ((Ders.getPoint(yeniHarf) - Ders.getPoint(eskiHarf)) * kredi);
		currentAGNO /= toplamKredi;
	}

	public void setToplamKredi(int kredi) {
		double temp = currentAGNO * toplamKredi;
		this.toplamKredi = kredi;
		temp /= toplamKredi;
		this.currentAGNO = temp;
	}

	public void setKrediSayisi(int kredi) {
		this.toplamKredi = kredi;
	}

	public int getKrediSayisi() {
		return toplamKredi;
	}

	public double getCurrentAGNO() {
		return currentAGNO;
	}

}
