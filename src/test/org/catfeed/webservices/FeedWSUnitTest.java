package org.catfeed.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Assert;
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
		assertEquals(palavrasChave.get(0), "motivos");
		assertEquals(palavrasChave.get(1), "ainda");
		assertEquals(palavrasChave.get(2), "acreditar");
		assertEquals(palavrasChave.get(3), "humanidade");
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveConverterPalavrasMaiusculas()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "DIAMANTE NEGRO VEIO DA JOALHERIA?";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(4, palavrasChave.size());
		assertEquals(palavrasChave.get(0), "diamante");
		assertEquals(palavrasChave.get(1), "negro");
		assertEquals(palavrasChave.get(2), "veio");
		assertEquals(palavrasChave.get(3), "joalheria");
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarHashTags()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "#amiga #parceira #segundachata #cansadas #quecheguelogosexta";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(5, palavrasChave.size());
		assertEquals(palavrasChave.get(0), "amiga");
		assertEquals(palavrasChave.get(1), "parceira");
		assertEquals(palavrasChave.get(2), "segundachata");
		assertEquals(palavrasChave.get(3), "cansadas");
		assertEquals(palavrasChave.get(4), "quecheguelogosexta");
	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarEmoticons()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "fresquin! :)";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(1, palavrasChave.size());
		assertEquals(palavrasChave.get(0), "fresquin");

	}
	
	@Test
	public void testExtrairPalavrasChave_DeveTratarEmoticonsEHashTags()
	{
		FeedWS feedWS = new FeedWS();
		
		String mensagemPost = "Sorte mesmo é comprar 455 figurinhas, ficar com 63 repetidas e terminar com 38 *-* #vício #álbumdacopa #vamoquevamo";
		
		List<String> palavrasChave = feedWS.extrairPalavrasChave(mensagemPost);
		
		assertEquals(12, palavrasChave.size());
		assertEquals(palavrasChave.get(1), "comprar");
		assertFalse(palavrasChave.contains("*-*"));
		assertFalse(palavrasChave.contains("#"));
	}
}
