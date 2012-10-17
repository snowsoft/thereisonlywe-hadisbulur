package info.thereisonlywe.hadisbulur.data;

import info.thereisonlywe.core.search.SearchResult;

public class HadithSearchResult extends SearchResult {

	private final int index;

	public HadithSearchResult(String value, int index, int strength)
	{
		super(value, strength);
		this.index = index;
	}

	public int getHadithIndex()
	{
		return index;
	}

	public Hadith getHadith()
	{
		return HadithCollection.getHadith(index);
	}

}
