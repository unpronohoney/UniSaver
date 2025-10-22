package com.unisaver.unisaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class IhtimallerDizisi {
	private HashMap<Integer, Double[]> eskiAgnoVeSapmalar ;
	private HashMap<Integer, ArrayList<Ihtimal>> eskiIhtimaller;
	private HashMap<Integer, Integer> krediler;
	private int counter;

	public IhtimallerDizisi() {
		this.eskiAgnoVeSapmalar = new HashMap<>();
		this.eskiIhtimaller = new HashMap<>();
		this.krediler = new HashMap<>();
		this.counter = 0;
	}

	public int getCounter() {
		return counter;
	}
	public boolean ihtimalleriEkle(ArrayList<Ihtimal> ihtimaller, double agno, double minAgno, double maxAgno, int kredi) {
		if (eskiAgnoVeSapmalar.isEmpty()) {
			counter++;
			for (Ihtimal ih : ihtimaller) {
				agno += ih.getEtkiDuzeyi();
				minAgno += ih.getEtkiDuzeyi();
				maxAgno += ih.getEtkiDuzeyi();
			}
			double real = Math.round(agno * 1000.0) / 1000.0;
			double min = Math.round(minAgno * 1000.0) / 1000.0;
			double max = Math.round(maxAgno * 1000.0) / 1000.0;
			real = Math.round(real * 100.0) / 100.0;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			min = real - min;
			max = max - real;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			Double[] tutacak = {agno, min, max};
			eskiAgnoVeSapmalar.put(counter, tutacak);
			eskiIhtimaller.put(counter, ihtimaller);
			krediler.put(counter, kredi);
			return true;
		} else {
			int aynilar = 0;
			for (Integer i : eskiIhtimaller.keySet()) {
				for (int j = 0; j < ihtimaller.size(); j++) {
					if (eskiIhtimaller.get(i).get(j).getYuksekNot().equals(ihtimaller.get(j).getYuksekNot())) {
						aynilar++;
					}
				}
				if (aynilar == ihtimaller.size()) {
					return false;
				}
				aynilar = 0;
			}
			counter++;
			for (Ihtimal ih : ihtimaller) {
				agno += ih.getEtkiDuzeyi();
				minAgno += ih.getEtkiDuzeyi();
				maxAgno += ih.getEtkiDuzeyi();
			}
			double real = Math.round(agno * 1000.0) / 1000.0;
			double min = Math.round(minAgno * 1000.0) / 1000.0;
			double max = Math.round(maxAgno * 1000.0) / 1000.0;
			real = Math.round(real * 100.0) / 100.0;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			min = real - min;
			max = max - real;
			min = Math.round(min * 100.0) / 100.0;
			max = Math.round(max * 100.0) / 100.0;
			Double[] tutacak = {agno, min, max};
			eskiAgnoVeSapmalar.put(counter, tutacak);
			eskiIhtimaller.put(counter, ihtimaller);
			krediler.put(counter, kredi);
			return true;
		}
	}

	public Map<Integer, Double[]> getEskiAgnolar() {
		return eskiAgnoVeSapmalar;
	}

	public void listByAgno() {
		HashMap<Integer, ArrayList<Ihtimal>> yeniEskiIhtimaller = new HashMap<>();
		HashMap<Integer, Double[]> yeniEskiAgnolar = new HashMap<>();
		Double maxAgno = -0.5;
		Integer maxAgnoInt = 0;
		Integer uzunluk = eskiAgnoVeSapmalar.size();
		for (Integer yeniCounter = 1; yeniCounter<uzunluk+1 ; yeniCounter++) {
			for (Integer j : eskiAgnoVeSapmalar.keySet()) {
				if (eskiAgnoVeSapmalar.get(j)[0]>maxAgno) {
					maxAgno = eskiAgnoVeSapmalar.get(j)[0];
					maxAgnoInt = j;
				}
			}
			yeniEskiIhtimaller.put(yeniCounter, eskiIhtimaller.get(maxAgnoInt));
			Double[] a = {maxAgno, eskiAgnoVeSapmalar.get(maxAgnoInt)[1], eskiAgnoVeSapmalar.get(maxAgnoInt)[2]};
			yeniEskiAgnolar.put(yeniCounter, a);
			maxAgno = -0.5;
			eskiIhtimaller.remove(maxAgnoInt);
			eskiAgnoVeSapmalar.remove(maxAgnoInt);
		}
		this.eskiAgnoVeSapmalar = yeniEskiAgnolar;
		this.eskiIhtimaller = yeniEskiIhtimaller;
	}
	public String toStringOnePossibility(Integer number) {
		StringBuilder message;
		if (number == 1) {
			message = new StringBuilder(MainActivity.getAppContext().getString(R.string.agnon_en_yuksek_olasilik));
		} else if (number == eskiAgnoVeSapmalar.size()) {
			message = new StringBuilder(MainActivity.getAppContext().getString(R.string.agnon_en_dusuk_olasilik));
		} else {
			message = new StringBuilder(MainActivity.getAppContext().getString(R.string.numarali_olasilik, number));
		}
		for (Ihtimal ihtimal : eskiIhtimaller.get(number)) {
			message.append(ihtimal.toString());
		}
		message.append(MainActivity.getAppContext().getString(R.string.son_agno_bulundu, krediler.get(number), Objects.requireNonNull(eskiAgnoVeSapmalar.get(number))[0]));
		message.append(getSapma(number));
		return message.toString();
	}

	public String getAgno(int number) {
        return String.format(Locale.ROOT, "%.2f", Objects.requireNonNull(eskiAgnoVeSapmalar.get(number))[0]);
	}

	public String getSapma(int number) {
		double sapmaNeg = eskiAgnoVeSapmalar.get(number)[1];
		double sapmaPos = eskiAgnoVeSapmalar.get(number)[2];
		StringBuilder message = new StringBuilder();

		if (sapmaNeg != 0.0 && Math.round(sapmaNeg * 100.0) / 100.0 == Math.round(sapmaPos * 100.0) / 100.0) {
			message.append(MainActivity.getAppContext().getString(R.string.sapma_bir_tarafta, sapmaNeg));
		} else if (sapmaNeg != 0.0 && sapmaPos != 0.0) {
			message.append(MainActivity.getAppContext().getString(R.string.sapma_iki_tarafta, sapmaPos, sapmaNeg));
		} else if (sapmaNeg != 0.0 && sapmaPos == 0.0) {
			message.append(MainActivity.getAppContext().getString(R.string.sapma_sadece_minus, sapmaNeg));
		} else if (sapmaNeg == 0.0 && sapmaPos != 0.0) {
			message.append(MainActivity.getAppContext().getString(R.string.sapma_sadece_plus, sapmaPos));
		}
		return message.toString();
	}

	public String getSapmaDegeri(int number) {
		return getSapma(number).substring(1, 5);
	}
}
