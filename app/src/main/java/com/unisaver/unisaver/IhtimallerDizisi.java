package com.unisaver.unisaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
		if (!eskiAgnoVeSapmalar.isEmpty()) {
			String yeniHash = ihtimaller.stream()
					.map(Ihtimal::getYuksekNot)
					.collect(Collectors.joining(","));

			Set<String> eskiHashler = eskiIhtimaller.values().stream()
					.map(l -> l.stream().map(Ihtimal::getYuksekNot)
							.collect(Collectors.joining(",")))
					.collect(Collectors.toSet());

			if (eskiHashler.contains(yeniHash)) {
				return false;
			}
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

	public Map<Integer, Double[]> getEskiAgnolar() {
		return eskiAgnoVeSapmalar;
	}

	public void listByAgno() {
		// eskiAgnoVeSapmalar'ı AGNO değerine (index 0) göre azalan sırada sırala
		List<Map.Entry<Integer, Double[]>> sortedEntries = eskiAgnoVeSapmalar.entrySet()
				.stream()
				.sorted((e1, e2) -> Double.compare(e2.getValue()[0], e1.getValue()[0])) // azalan sıralama
				.collect(Collectors.toList());

		// yeni map'leri oluştur
		HashMap<Integer, ArrayList<Ihtimal>> yeniEskiIhtimaller = new HashMap<>();
		HashMap<Integer, Double[]> yeniEskiAgnolar = new HashMap<>();

		int counter = 1;
		for (Map.Entry<Integer, Double[]> entry : sortedEntries) {
			Integer key = entry.getKey();
			Double[] values = entry.getValue();

			yeniEskiIhtimaller.put(counter, eskiIhtimaller.get(key));
			yeniEskiAgnolar.put(counter, values);
			counter++;
		}

		// referansları güncelle
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
