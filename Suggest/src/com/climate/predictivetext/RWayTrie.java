package com.climate.predictivetext;

import java.util.ArrayList;

/**
 * This implements an R-Way Trie where each node has R links.
 * The radix (R) is the length of the alphabet. In this case it is 8 (numbers from 2-9 inclusive).
 * This trie stores the sequence of numbers that can be typed though a numeric keypad in a cellphone.
 * At the end of every valid sequence is a map of words and their respective frequency counts.
 * Before returning the value, this class also handles sorting of the words in the hashMap based on their 
 * frequency count
 * @author preetam
 */
public class RWayTrie implements Trie {
	private final static int radix = 8;
	private Node root;

	/**
	 * Static nested class to depict the idea of a Trie Node
	 */
	private static class Node {
		private Value value = new Value();
		private Node[] next = new Node[radix];
	}

	/**
	 * Method to get a Value for a given key.
	 * 
	 * @param key
	 * @return Returns an ArrayList of strings, if the key is found in the trie
	 *         and null otherwise.
	 */
	public ArrayList<String> retrieve(String key) {
		Node ret = retrieve(root, key, 0);
		if (null == ret)
			return null;
		return ret.value.getSortedSetOfWords();
	}

	/**
	 * Method to retrieve a list of strings that match the given prefix value
	 * 
	 * @param key
	 *            Input prefix
	 * @return Returns an ArrayList of strings matching the given input prefix
	 */
	public ArrayList<String> retrievePrefixMatches(String key) {
		Node ret = retrieve(root, key, 0);
		/*
		 * Traverse through all the links in the retrieved node until we reach
		 * the end of the respective links
		 */
		ArrayList<String> prefixMatches = new ArrayList<String>();
		traverse(ret, key, prefixMatches);
		return prefixMatches; // This could be empty
	}

	private void traverse(Node node, String key, ArrayList<String> prefixMatches) {
		if (node == null)
			return;
		if (node.value.getSortedSetOfWords() != null)
			prefixMatches.addAll(node.value.getSortedSetOfWords());
		for (int c = 0; c < radix; c++) {
			traverse(node.next[c], key + c, prefixMatches);
		}
	}

	/**
	 * Helper function to recursively retrieve from the trie
	 */
	private Node retrieve(Node node, String key, int idx) {
		if (null == node)
			return null;
		if (idx == key.length()) {
			return node;
		}
		int actualIdx = computeActualIndex(key.charAt(idx));
		if (actualIdx == -1)
			return null;
		return retrieve(node.next[actualIdx], key, idx + 1);
	}

	/**
	 * Insert a value against a given key in the trie
	 * 
	 * @param key Key to hold the value against
	 * @param value Value to be stored against the input key
	 */
	public void insert(String key, String value) {
		root = insert(root, key, value, 0);
	}

	/**
	 * Helper function to recursively insert into the trie
	 */
	private Node insert(Node node, String key, String value, int idx) {
		if (null == node)
			node = new Node();
		if (idx == key.length()) {
			node.value.addWord(value);
			return node;
		}
		int actualIdx = computeActualIndex(key.charAt(idx));
		node.next[actualIdx] = insert(node.next[actualIdx], key, value, idx + 1);
		return node;
	}

	private int computeActualIndex(char link) {
		if (link < '2')
			return -1;
		return link - '2';
	}
}
