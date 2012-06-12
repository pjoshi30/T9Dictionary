package com.climate.predictivetext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Value {

	private HashMap<String, Integer> mapOfKeyIndex;

	public Value() {
		mapOfKeyIndex = new HashMap<String, Integer>();
	}

	public ArrayList<String> getSortedSetOfWords() {
		return sortMap(mapOfKeyIndex);
	}

	public void addWord(String key) {
		// Check if the key exists in the hashMap
		if (mapOfKeyIndex.containsKey(key)) {
			mapOfKeyIndex.put(key, (mapOfKeyIndex.get(key)) + 1);
		} else {
			mapOfKeyIndex.put(key, 1);
		}
	}

	private ArrayList<String> sortMap(HashMap<String, Integer> inpMap) {
		if (inpMap.size() == 0)
			return null;
		// Copy the map into an auxillary map
		HashMap<Integer, Set<String>> auxMap = copyIntoAux(inpMap);

		// Sort the array of input values
		Object[] freqArr = inpMap.values().toArray();
		DecreasingOrderComparator decComp = new DecreasingOrderComparator();
		Arrays.sort(freqArr, decComp);

		// Iterate through the array and generate the final return list
		ArrayList<String> retList = generateArrayList(freqArr, auxMap);
		return retList;
	}

	private HashMap<Integer, Set<String>> copyIntoAux(
			HashMap<String, Integer> inpMap) {
		HashMap<Integer, Set<String>> auxMap = new HashMap<Integer, Set<String>>();
		for (String key : inpMap.keySet()) {
			int val = inpMap.get(key);
			if (auxMap.containsKey(val)) {
				auxMap.get(val).add(key);
			} else {
				Set<String> arrList = new HashSet<String>();
				arrList.add(key);
				auxMap.put(val, arrList);
			}
		}
		return auxMap;
	}

	private ArrayList<String> generateArrayList(Object[] freqArr,
			HashMap<Integer, Set<String>> auxMap) {
		ArrayList<String> retList = new ArrayList<String>();
		Set<Integer> alreadySeen = new HashSet<Integer>();
		for (int count = 0; count < freqArr.length; count++) {
			if (!alreadySeen.contains(freqArr[count])) {
				for (String s : auxMap.get(freqArr[count])) {
					retList.add(s);
				}
			}
			alreadySeen.add((Integer) freqArr[count]);
		}
		return retList;
	}

	private class DecreasingOrderComparator implements Comparator<Object> {

		public int compare(Object a, Object b) {
			if ((Integer) a < (Integer) b) {
				return 1;
			} else if ((Integer) a == (Integer) b) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}
