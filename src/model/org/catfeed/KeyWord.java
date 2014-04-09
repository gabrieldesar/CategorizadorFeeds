package org.catfeed;

public class KeyWord implements Comparable<KeyWord>
{
	String text;
	
	Integer weight;

	public KeyWord(String text, Integer weight)
	{
		this.text = text;
		this.weight = weight;
	}

	public String getText()
	{
		return text;
	}

	public Integer getWeight()
	{
		return weight;
	}
	
	public int compareTo(KeyWord compareKeyWord)
	{
		Integer compareWeight = ((KeyWord) compareKeyWord).getWeight(); 
 
		return compareWeight - this.weight;
	}	
}
