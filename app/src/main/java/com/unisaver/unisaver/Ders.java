package com.unisaver.unisaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ders {
	private int credit;
	private String harfNotu;
	public static final Map<String , Double> harfNotlari = new HashMap<>();
	private String isim = null;
	private String yeniHarf = null;
	private int dersNo = -1;

	public Ders(int no, int credit, String harfNotu, String isim, String yeniHarf) {
		this.dersNo = no;
		this.credit=credit;
		this.harfNotu=harfNotu;
		this.isim = isim;
		this.yeniHarf = yeniHarf;
	}

	public String toStringShare() {
		if (isim != null) {
            return MainActivity.getAppContext().getString(R.string.ders_tostring, isim, harfNotu, yeniHarf);
		}
		return MainActivity.getAppContext().getString(R.string.ders_tostring_unname, credit, harfNotu, yeniHarf);
	}

	public Ders(int credit, String harfNotu, String isim) {
		this.credit=credit;
		this.harfNotu=harfNotu;
		this.isim = isim;
	}
	public Ders(int credit, String harfNotu) {
		this.credit=credit;
		this.harfNotu=harfNotu;
	}

	public static String getMaxHarf() {
		double point = 0;
		String maxHarf = "";
		for (String s: harfNotlari.keySet()) {
			point = harfNotlari.get(s);
			maxHarf = s;
			break;
		}
		for (String s : harfNotlari.keySet()) {
			if (harfNotlari.get(s) > point) {
				point = harfNotlari.get(s);
				maxHarf = s;
			}
		}
		return maxHarf;
	}

	public static String getMinHarf() {
		double point = 0;
		String minHarf = "";
		for (String s: harfNotlari.keySet()) {
			point = harfNotlari.get(s);
			minHarf = s;
			break;
		}
		for (String s : harfNotlari.keySet()) {
			if (harfNotlari.get(s) < point) {
				minHarf = s;
				point = harfNotlari.get(s);
			}
		}
		return minHarf;
	}

	public static String[] getSortedLetters() {
		int len = harfNotlari.keySet().size();
		String[] harfler = new String[len];
		Double[] degerler = harfNotlari.values().toArray(new Double[0]);
		double min = getPoint(getMinHarf());
		int j = 0;
		for (; j < len; j++) {
			double max = min;
			int indexMax = 0;
			for (int i = 0; i < degerler.length; i++) {
				if (degerler[i] != null && degerler[i] > max) {
					max = degerler[i];
					indexMax = i;
				}
			}
			degerler[indexMax] = null;
			for (String s : harfNotlari.keySet()) {
				if (harfNotlari.get(s) == max) {
					harfler[j] = s;
				}
			}
		}

		return harfler;
	}

	public static void fillMap() {
		harfNotlari.clear();
		new Thread(() -> {
			GradingSystemDao dao = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao();
			List<GradingSystemEntity> systems = dao.getAllSystems();
			GradingSystemEntity system = null;
			for (GradingSystemEntity sys : systems) {
				if (sys.isSelected) {
					system = sys;
					break;
				}
			}
			if (system == null) {
				system = systems.get(0);
			}
			List<GradeMappingEntity> mappings = dao.getMappingsForSystem(system.id);
			for (GradeMappingEntity map : mappings) {
				harfNotlari.put(map.grade, map.value);
			}
		}).start();
		/*String[] harfler = {"AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF"};
		Double[] puanlar = {4.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 0.5, 0.0};
		for(int i = 0; i<9;i++) {
			harfNotlari.put(harfler[i], puanlar[i]);
		}*/
	}

	public static double getPoint(String harfNot) {
		if (harfNot.equals(MainActivity.getAppContext().getString(R.string.yok))) {
			return 0.010101;
		}
		for (String harf : harfNotlari.keySet()) {
			if (harf.equals(harfNot)) {
				return harfNotlari.get(harf);
			}
		}
		return 0.001;
	}
	public void setCredit(int kredi) {
		this.credit = kredi;
	}
	public void setYeniHarf(String harf) {
		this.yeniHarf = harf;
	}

	public void setDersNo(int a) {
		this.dersNo = a;
	}

	public String getYeniHarf() {
		return yeniHarf;
	}

	public int getDersNo() {
		return dersNo;
	}

	public String getIsim() {
		return isim;
	}

	public int getCredit() {
		return credit;
	}

	public String getHarfNotu() {
		return harfNotu;
	}

	public void setHarfNotu(String harf) {
		this.harfNotu = harf;
	}

	@Override
	public String toString() {
		if (isim != null) {
			return MainActivity.getAppContext().getString(R.string.ders_tostring_kom, isim);
		}
		return MainActivity.getAppContext().getString(R.string.ders_tostring_kom_unname, credit, harfNotu);
	}

}
