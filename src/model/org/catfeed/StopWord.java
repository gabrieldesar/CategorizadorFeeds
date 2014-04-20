package org.catfeed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StopWord 
{
	 public static final CharArraySet STOPWORDS_PORTUGUES_INGLES;
	    
	 static
	 {
	        List<String> listaStopWords = lerStopWordsArquivo("stopwords_pt.txt", "stopwords_en.txt");
	        
	        STOPWORDS_PORTUGUES_INGLES = new CharArraySet(Version.LUCENE_47, listaStopWords, true);
	 }
	    
	 private static List<String> lerStopWordsArquivo(String... arquivos)
	 {
		 BufferedReader bufferedReader = null;
		 
		 List<String> listaStopWords = new ArrayList<String>();
	        
		 for(int i = 0; i < arquivos.length; i++)
		 {
			 try
			 {
				 String linhaAtual;
				 
				 bufferedReader = new BufferedReader(new FileReader(StopWord.class.getClassLoader()
						 														  .getResource(arquivos[i])
						 														  .getPath()));
		 
				 while ((linhaAtual = bufferedReader.readLine()) != null)
				 {
					 listaStopWords.add(linhaAtual.trim());
				 }
		 
			 } 
			 catch (IOException e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 try
				 {
					 if (bufferedReader != null)
					 {
						 bufferedReader.close();
					 }
				 } 
				 catch (IOException ex)
				 {
					 ex.printStackTrace();
				 }
			 }
		 }
		 
		 return listaStopWords;
	 }
}
