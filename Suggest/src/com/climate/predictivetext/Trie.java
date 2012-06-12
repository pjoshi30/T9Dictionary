package com.climate.predictivetext;

import java.util.ArrayList;

public interface Trie {

	//Retrieves a list of strings for a given input sequence
	public ArrayList<String> retrieve(String key);

	//Retrieves a list of matching strings corresponding to a given input prefix
	public ArrayList<String> retrievePrefixMatches(String key);

	//Inserts a value against a given key in the trie
	public void insert(String key, String value);
}
