package com.climate.predictivetext;

import java.util.ArrayList;

/**
 * This implements an R-Way Trie where each node has R links. The radix (R) is
 * the length of the alphabet. In this case it is 8 (numbers from 2-9
 * inclusive). This Trie stores the sequence of numbers that can be typed though
 * a numeric keypad in a cellphone. At the end of every valid sequence is a map
 * of words and their respective frequency counts. Here, there is only one such
 * map for all possible numbers in a node, i.e, 22[2-9] have only one map which
 * is unlike the traditional Trie where each node has its own value. For our T9
 * use case, we don't require every node to have its own separate
 * value.(However, if we were to implement an address book instead, we *would*
 * require every node to have its own value). Before returning the value, this
 * class also handles sorting of the words in the hashMap based on their
 * frequency count
 * 
 * @author preetam
 */
public class RWayTrie implements Trie {
	private final static int radix = 8;
	private Node root;

	private interface Link {
	}

	/**
	 * Static nested class to depict the idea of a Trie Node
	 */
	private static class Node implements Link{
		private Link[] next = new Link[radix + 1]; // The last element is a
													// NodeVal
	}

	private static class NodeVal implements Link{
		private Value value = new Value();
	}

	/**
	 * Method to get a Value for a given key.
	 * 
	 * @param key
	 * @return Returns an ArrayList of strings, if the key is found in the trie
	 *         and null otherwise.
	 */
	public ArrayList<String> retrieve(String key) {
		NodeVal ret = (NodeVal) retrieve(root, key, 0, false);
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
		Node ret = (Node) retrieve(root, key, 0, true);
		/*
		 * Traverse through all the links in the retrieved node until we reach
		 * the end of the respective links
		 */
		ArrayList<String> prefixMatches = new ArrayList<String>();
		traverse(ret, key, prefixMatches);
		return prefixMatches; // This could be empty
	}

	private void traverse(Link node, String key, ArrayList<String> prefixMatches) {
		if (node == null)
			return;
		if (((NodeVal) (((Node) node).next[8])).value.getSortedSetOfWords() != null)
			prefixMatches.addAll(((NodeVal) (((Node) node).next[8])).value
					.getSortedSetOfWords());
		for (int c = 0; c < radix; c++) {
			traverse(((Node) node).next[c], key + c, prefixMatches);
		}
	}

	/**
	 * Helper function to recursively retrieve from the trie
	 */
	private Link retrieve(Link node, String key, int idx, boolean prefixMatch) {
		if (null == node)
			return null;
		if (idx == key.length() && prefixMatch) {
			return ((Node) node);
		} else if (idx == key.length() && !prefixMatch) {
			return ((Node) node).next[8];
		}
		int actualIdx = computeActualIndex(key.charAt(idx));
		if (actualIdx < 0)
			return null;
		return retrieve(((Node) node).next[actualIdx], key, idx + 1,
				prefixMatch);
	}

	/**
	 * Insert a value against a given key in the trie
	 * 
	 * @param key
	 *            Key to hold the value against
	 * @param value
	 *            Value to be stored against the input key
	 */
	public void insert(String key, String value) {
		root = insert(root, key, value, 0);
	}

	/**
	 * Helper function to recursively insert into the trie
	 */
	private Node insert(Link node, String key, String value, int idx) {
		if (null == node) {
			node = new Node();
			((Node) node).next[8] = new NodeVal();
		}
		if (idx == key.length()) {
			((NodeVal) (((Node) node).next[8])).value.addWord(value);
			return (Node) node;
		}
		int actualIdx = computeActualIndex(key.charAt(idx));
		((Node) node).next[actualIdx] = insert(((Node) node).next[actualIdx],
				key, value, idx + 1);
		return ((Node) node);
	}

	private int computeActualIndex(char link) {
		return link - '2';
	}
}
