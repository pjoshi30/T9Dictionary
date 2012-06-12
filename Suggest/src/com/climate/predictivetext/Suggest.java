package com.climate.predictivetext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Preetam's implementation of a Predictive text suggester
 * 
 * @author preetam
 */
public class Suggest {

	private static Trie trie = new RWayTrie();
	private static HashSet<String> alreadyPrinted = new HashSet<String>();

	/**
	 * Please implement this method and any supporting methods/classes
	 */
	private static void printSuggestions(File f, String seq) {

		// Always validate input! Fail if input is invalid
		try {
			validateInput(seq);
		} catch (RuntimeException ex) {
			log(ex.getMessage());
			System.exit(1);
		}
		log("Parsing file " + f.getAbsolutePath());
		populateTrieFromFile(f);
		log("Exact matches for " + seq + ":");
		retrieveSequencesFromTrie(seq);
		log("Prefix matches for " + seq + ": ");
		retrievePrefixMatchesFromTrie(seq);
	}

	/**
	 * A sanity check to see if the input is valid. Function throws a run time
	 * exception if an invalid input is encountered.
	 * 
	 * @param inp
	 *            Input sequece to be validated
	 */
	private static void validateInput(String inp) {
		for (Character c : inp.toCharArray()) {
			if (c < '2' || c > '9')
				throw new RuntimeException(
						"Invalid input sequence, out of range [2-9]");
		}
	}

	/**
	 * Method to print matching sequences corresponding to a given input
	 * sequence
	 * 
	 * @param seq
	 *            Input Sequence
	 */
	private static void retrieveSequencesFromTrie(String seq) {
		// Get a copy of the heap
		ArrayList<String> result = trie.retrieve(seq);
		if (null == result)
			log("No sequences found!");
		else {
			for (String res : result) {
				log(res);
				alreadyPrinted.add(res);
			}
		}
	}

	/**
	 * Method to print longer sequences of words matching the input sequence
	 * 
	 * @param seq
	 *            Input Sequence
	 */
	private static void retrievePrefixMatchesFromTrie(String seq) {
		// Get a copy of the heap
		ArrayList<String> result = trie.retrievePrefixMatches(seq);
		if (result.size() == 0)
			log("No matches found for the given prefix!");
		else {
			for (String res : result) {
				if (!alreadyPrinted.contains(res))
					log(res);
			}
		}
	}

	/**
	 * Method to populate a trie from a given valid input file
	 * 
	 * @param f
	 *            Input File
	 */
	private static void populateTrieFromFile(File f) {
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(f);
			br = new BufferedReader(reader);
			String line = br.readLine();
			while (line != null) {
				insertWordsIntoTrie(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException fnfEx) {
			log("Error: File " + f.getAbsolutePath() + " Not Found!");
			fnfEx.printStackTrace();
			System.exit(1);
		} catch (IOException ioEx) {
			log("Error: IO Exception! for file: " + f.getAbsolutePath());
			ioEx.printStackTrace();
			System.exit(1);
		} finally {
			closeFile(reader, br);
		}
	}

	private static void closeFile(FileReader reader, BufferedReader br) {
		try {
			br.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void insertWordsIntoTrie(String line) {
		String[] lineSplit = line.trim().split(" ");
		for (String temp : lineSplit) {
			String[] dashSplit = temp.split("--");
			for (String word : dashSplit) {
				String key = "";
				String purgedWord = purgeWord(word);
				try {
					key = toNumeric(purgedWord.toLowerCase());
				} catch (RuntimeException rEx) {
					continue;
				}
				trie.insert(key, purgedWord);
			}
		}
	}

	/**
	 * Method to remove special characters from a given word
	 * 
	 * @param w
	 *            input word
	 * @return Returns a "purged" word
	 */
	private static String purgeWord(String w) {
		return w.replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase();
	}

	/**
	 * Utility method to convert a word (e.g. "cat") to its numeric
	 * representation (e.g. 228). Input must be lowercase. A runtime exception
	 * is thrown in case non alphabet characters are provided.
	 */
	private static String toNumeric(String word) throws RuntimeException {
		char[] numeric = new char[word.length()];
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			numeric[i] = convert(c);
		}
		return new String(numeric);
	}

	private static final Map<String, Character> KEYPAD_MAP = new HashMap<String, Character>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1474130272330004894L;

		{
			put("abc", '2');
			put("def", '3');
			put("ghi", '4');
			put("jkl", '5');
			put("mno", '6');
			put("pqrs", '7');
			put("tuv", '8');
			put("wxyz", '9');
		}
	};

	private static char convert(char c) throws RuntimeException {
		for (String s : KEYPAD_MAP.keySet()) {
			if (s.contains(Character.toString(c))) {
				return KEYPAD_MAP.get(s);
			}
		}
		throw new RuntimeException("Can't convert char: " + c);
	}

	public static void main(String... args) {
		if (args.length != 2) {
			log("Usage: java Suggest filename seq");
			System.exit(1);
		}
		File f = new File(args[0]);
		if (!f.exists() || !f.isFile()) {
			log(args[0] + " is not a valid file");
			System.exit(2);
		}
		String seq = args[1];

		printSuggestions(f, seq);
	}

	private static void log(String s) {
		System.out.println(s);
	}
}
