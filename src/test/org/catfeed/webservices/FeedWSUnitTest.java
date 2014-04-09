package org.catfeed.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.catfeed.KeyWord;
import org.junit.Test;

public class FeedWSUnitTest
{
	@Test
	public void testTransformarJSONEmString_DeveRetornarStringCorreta()
	{
		FeedWS feedWS = new FeedWS();
		String entrada = "{\"accessToken\":\"CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD\"}";
		
		String saida = feedWS.transformarJSONEmString(entrada, "accessToken");
		String esperado = "CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD";
		
		assertEquals(esperado, saida);
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveRemoverStopWords()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "Motivos de ainda se acreditar na humanidade !!!";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(4, palavrasChave.size());
		assertEquals("motivos",palavrasChave.get(0));
		assertEquals( "ainda", palavrasChave.get(1));
		assertEquals("acreditar", palavrasChave.get(2));
		assertEquals("humanidade", palavrasChave.get(3));
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveConverterPalavrasMaiusculas()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "DIAMANTE NEGRO VEIO DA JOALHERIA?";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(4, palavrasChave.size());
		assertEquals("diamante", palavrasChave.get(0));
		assertEquals("negro", palavrasChave.get(1));
		assertEquals("veio", palavrasChave.get(2) );
		assertEquals("joalheria", palavrasChave.get(3));
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarPostIngles()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "Featured Fan Art - Morgana by Maysiria";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(5, palavrasChave.size());
		assertEquals(palavrasChave.get(0), "featured");
		assertEquals(palavrasChave.get(1), "fan");
		assertEquals(palavrasChave.get(2), "art");
		assertEquals(palavrasChave.get(3), "morgana");
		assertEquals(palavrasChave.get(4), "maysiria");
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarPostIngles2()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "To view previous winners please visit the \"Winners\" tab on the Daily";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(8, palavrasChave.size());
		assertEquals(palavrasChave.get(0), "view");
		assertEquals(palavrasChave.get(1), "previous");
		assertEquals(palavrasChave.get(2), "winners");
		assertEquals(palavrasChave.get(3), "please");
		assertEquals(palavrasChave.get(4), "visit");
		assertEquals(palavrasChave.get(5), "winners");
		assertEquals(palavrasChave.get(6), "tab");
		assertEquals(palavrasChave.get(7), "daily");
	}
	

	@Test
	public void testExtrairPalavrasChave_DeveTratarHashTags()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "#amiga #parceira #segundachata #cansadas #quecheguelogosexta";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(5, palavrasChave.size());
		assertEquals("amiga", palavrasChave.get(0));
		assertEquals("parceira", palavrasChave.get(1));
		assertEquals("segundachata", palavrasChave.get(2));
		assertEquals("cansadas", palavrasChave.get(3));
		assertEquals("quecheguelogosexta", palavrasChave.get(4));
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarEmoticons()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "fresquin! :)";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(1, palavrasChave.size());
		assertEquals("fresquin", palavrasChave.get(0));

	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarEmoticonsEHashTags()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "Sorte mesmo é comprar 455 figurinhas, ficar com 63 repetidas e terminar com 38 *-* #vício #álbumdacopa #vamoquevamo";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(12, palavrasChave.size());
		assertEquals("comprar", palavrasChave.get(1));
		assertFalse(palavrasChave.contains("*-*"));
		assertFalse(palavrasChave.contains("#"));
	}
	
	@Test
	public void testPrepararListaKeyWords_DeveConterFrequenciaCorreta()
	{
		FeedWS feedWS = new FeedWS();
		
		List<String> palavrasChave = new ArrayList<String>();
		palavrasChave.add("amiga");
		palavrasChave.add("amiga");
		palavrasChave.add("amiga");
		palavrasChave.add("colega");
		palavrasChave.add("colega");
		palavrasChave.add("parceira");
		palavrasChave.add("conhecido");
		
		List<KeyWord> listaKeyWords = feedWS.prepararListaKeyWords(palavrasChave);
		
		assertEquals(4, listaKeyWords.size());
		
		assertEquals("amiga", listaKeyWords.get(0).getText());
		assertEquals(new Integer(3), listaKeyWords.get(0).getWeight());
		
		assertEquals("colega", listaKeyWords.get(1).getText());
		assertEquals(new Integer(2), listaKeyWords.get(1).getWeight());
	}
}
