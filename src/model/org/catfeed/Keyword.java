package org.catfeed;

public class Keyword implements Comparable<Keyword>
{
	String text;
	
	Integer weight;

	public Keyword(String text, Integer weight)
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
	
	public int compareTo(Keyword compareKeyWord)
	{
		Integer compareWeight = ((Keyword) compareKeyWord).getWeight(); 
 
		return compareWeight - this.weight;
	}	
}
