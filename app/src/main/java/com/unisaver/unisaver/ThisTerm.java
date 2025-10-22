package com.unisaver.unisaver;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ThisTerm {
	private GenelNot pastTerms;
	private GenelNot minGeNot;
	private GenelNot maxGeNot;
	private ArrayList<Ders> dersler;
	private boolean agnoKurtaricisi;
	private IhtimallerDizisi possibilities;
	private double minAgno;
	private double maxAgno;

	public ThisTerm(GenelNot pastTerms, boolean agnoKurtaricisi) {
		this.pastTerms = pastTerms;
		this.agnoKurtaricisi = agnoKurtaricisi;
		this.dersler = new ArrayList<>();
	}

	public ThisTerm(GenelNot pastTerms) {
		this.pastTerms = pastTerms;
		this.dersler = new ArrayList<>();
	}

	public ArrayList<Ders> getDersler() {
		return dersler;
	}

	public void manuelDersGirisi(Ders ders) {
		dersler.add(ders);
		if (ders.getHarfNotu().equals(MainActivity.getAppContext().getString(R.string.yok))) {
			pastTerms.yeniDers(ders.getCredit(), ders.getYeniHarf());
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
		} else {
			pastTerms.eskiDers(ders.getCredit(), ders.getHarfNotu(), ders.getYeniHarf());
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
		}
	}

	public void dersKrediGuncelle(Ders ders, int newKredi) {
		if (ders.getCredit() != newKredi) {
			pastTerms.yeniKredi(ders.getCredit(), ders.getHarfNotu(), ders.getYeniHarf(), newKredi);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setCredit(newKredi);
		}
	}

	public void dersEskiHarfGuncelle(Ders ders, String newEskiHarf) {
		if (!ders.getHarfNotu().equals(newEskiHarf)) {
			pastTerms.yeniEskiHarf(ders.getCredit(), ders.getHarfNotu(), newEskiHarf);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setHarfNotu(newEskiHarf);
		}
	}

	public void dersYeniHarfGuncelle(Ders ders, String newYeniHarf) {
		if (!ders.getYeniHarf().equals(newYeniHarf)) {
			pastTerms.yeniYeniHarf(ders.getCredit(), ders.getYeniHarf(), newYeniHarf);
			minAgno = pastTerms.getCurrentAGNO() - 0.005;
			maxAgno = pastTerms.getCurrentAGNO() + 0.005;
			dersler.get(ders.getDersNo() - 1).setYeniHarf(newYeniHarf);
		}
	}

	public void dersSilme(Ders ders) {
		dersler.remove(findDers(ders.getDersNo()));
		for (int i = ders.getDersNo(); i < dersler.size(); i++) {
			dersler.get(i).setDersNo(i+1);
		}
		pastTerms.dersSilme(ders);
		minAgno = pastTerms.getCurrentAGNO() - 0.005;
		maxAgno = pastTerms.getCurrentAGNO() + 0.005;
	}

	private Ders findDers(int no) {
		for (Ders d : dersler) {
			if (d.getDersNo() == no) {
				return d;
			}
		}
		return null;
	}

	public String getAgnoInfo() {
		String ret;
		double real = Math.round(pastTerms.getCurrentAGNO() * 1000.0) / 1000.0;
		double min = Math.round(minAgno * 1000.0) / 1000.0;
		double max = Math.round(maxAgno * 1000.0) / 1000.0;
		real = Math.round(real * 100.0) / 100.0;
		min = Math.round(min * 100.0) / 100.0;
		max = Math.round(max * 100.0) / 100.0;
		min = real - min;
		max = max - real;
		min = Math.round(min * 100.0) / 100.0;
		max = Math.round(max * 100.0) / 100.0;
		if (min > 0.0 && max > 0.0 && min == max) {
			ret = MainActivity.getAppContext().getString(R.string.agno_info, real, "±", min, pastTerms.getKrediSayisi());
		} else if (min > 0.0 && max == 0.0) {
			ret = MainActivity.getAppContext().getString(R.string.agno_info, real, "-", min, pastTerms.getKrediSayisi());
		} else if (min == 0.0 && max > 0.0) {
			ret = MainActivity.getAppContext().getString(R.string.agno_info, real, "+", max, pastTerms.getKrediSayisi());
		} else if (min == 0.0 && max == 0.0) {
			ret = MainActivity.getAppContext().getString(R.string.agno_cred, real, pastTerms.getKrediSayisi());
		} else {
			ret = MainActivity.getAppContext().getString(R.string.agno_cred, real, pastTerms.getKrediSayisi());
		}
		return ret;
	}

	public void yuvarlamaPaylari() {
		int kredi = pastTerms.getKrediSayisi();
		double agno = pastTerms.getCurrentAGNO();
		this.minGeNot = new GenelNot(kredi, agno-0.005);
		this.maxGeNot = new GenelNot(kredi, agno+0.005);
		this.minAgno = pastTerms.getCurrentAGNO() - 0.005;
		this.maxAgno = pastTerms.getCurrentAGNO() + 0.005;
	}

	public void dersGirisiArr(ArrayList<Ders> derss) {
        dersler.addAll(derss);
	}

	public boolean ihtimallerDizisi() {
		if (agnoKurtaricisi) {
			this.possibilities = new IhtimallerDizisi();
			return true;
		} else
			return false;
	}

	public boolean newKsh(double minAgno, double maxAgno, String minHarf, String maxHarf, int komSayisi) {
		if (maxAgno == -1) {
			maxAgno = 4.0;
		}
		if (minHarf.equals(MainActivity.getAppContext().getString(R.string.unlimited))) {
			minHarf = Ders.getMinHarf();
		}
		if (maxHarf.equals(MainActivity.getAppContext().getString(R.string.unlimited))) {
			maxHarf = Ders.getMaxHarf();
		}

		int olusanKomlar = 0;

		ArrayList<Ihtimal> ihtimaller = new ArrayList<>();
		double denemePuani = pastTerms.getCurrentAGNO();
		int denemeKredi = pastTerms.getKrediSayisi();
		for (Ders d : dersler) {
			Ihtimal ih = new Ihtimal(denemePuani, denemeKredi, d);
			denemeKredi += ih.newRandomize(minHarf, maxHarf, maxHarf);
			denemePuani += ih.getEtkiDuzeyi();
			ihtimaller.add(ih);
		}

		if (denemePuani < maxAgno && denemePuani > minAgno) {
			possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO(), denemeKredi);
			olusanKomlar++;
		}

		if (olusanKomlar == komSayisi) {
			return true;
		}
		ihtimaller = new ArrayList<>();

		denemePuani = pastTerms.getCurrentAGNO();
		denemeKredi = pastTerms.getKrediSayisi();
		for (Ders d : dersler) {
			Ihtimal ih = new Ihtimal(denemePuani, denemeKredi, d);
			denemeKredi += ih.newRandomize(minHarf, maxHarf, minHarf);
			denemePuani += ih.getEtkiDuzeyi();
			ihtimaller.add(ih);
		}
		if (denemePuani < maxAgno && denemePuani > minAgno) {
			possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO(), denemeKredi);
			olusanKomlar++;
		}
		if (olusanKomlar == komSayisi) {
			return true;
		}

		int denemeSayisi = 0;
		while (true) {
			denemeSayisi++;
			ihtimaller = new ArrayList<>();
			denemePuani = pastTerms.getCurrentAGNO();
			denemeKredi = pastTerms.getKrediSayisi();
			for (Ders d : dersler) {
				Ihtimal ih = new Ihtimal(denemePuani, denemeKredi, d);
				denemeKredi += ih.newRandomize(minHarf, maxHarf, "");
				denemePuani += ih.getEtkiDuzeyi();
				ihtimaller.add(ih);
			}
			boolean dene = false;
			if (denemePuani < maxAgno && denemePuani > minAgno) {
				dene = possibilities.ihtimalleriEkle(ihtimaller, pastTerms.getCurrentAGNO(), minGeNot.getCurrentAGNO(), maxGeNot.getCurrentAGNO(), denemeKredi);
			}
			if (dene) {
				olusanKomlar++;
				denemeSayisi = 0;
			}
			if (olusanKomlar == komSayisi)
				return true;
			if (denemeSayisi == 200) {
				return false;
			}
		}
	}

	public IhtimallerDizisi getPossibilities() {
		return possibilities;
	}
	public GenelNot getPastTerms() {
		return pastTerms;
	}
}
