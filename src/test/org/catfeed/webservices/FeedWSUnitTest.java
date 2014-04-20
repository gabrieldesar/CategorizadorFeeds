package org.catfeed.webservices;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
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
	public void testRemoverStopWords_DeveRemoverStopWords()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "Motivos de ainda se acreditar na humanidade !!!";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);
		
		assertEquals("motivos acreditar humanidade", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveConverterPalavrasMaiusculas()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "DIAMANTE NEGRO VEIO DA JOALHERIA?";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);
		
		assertEquals("diamante negro veio joalheria", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarPostsEmIngles()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "Featured Fan Art - Morgana by Maysiria";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);

		assertEquals("featured fan art morgana maysiria", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarPostsEmIngles2()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "To view previous winners please visit the \"Winners\" tab on the Daily";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);
		
		assertEquals("view previous winners please visit winners tab daily", mensagemPostSemStopWords);
	}
	

	@Test
	public void testRemoverStopWords_DeveTratarHashTags()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "#amiga #parceira #segundachata #cansadas #quecheguelogosexta";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);
		
		assertEquals("amiga parceira segundachata cansadas quecheguelogosexta", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarEmoticons()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "fresquin! :)";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);
		
		assertEquals("fresquin", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarEmoticonsEHashTags()
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost = "Sorte mesmo é comprar 455 figurinhas, ficar com 63 repetidas e terminar com 38 *-* #vício #álbumdacopa #vamoquevamo";
		
		String mensagemPostSemStopWords = feedWS.removerStopWords(mensagemPost);

		assertEquals("sorte comprar 455 figurinhas ficar 63 repetidas terminar 38 vício álbumdacopa vamoquevamo", mensagemPostSemStopWords);
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
	
	@Test
	public void testObterCategoriaMensagem_PostContidoNaBaseDeConhecimento_DeveExibirCategoriaCorreta() throws ClassNotFoundException, IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagem = "Bendito o que vem em nome do Senhor. Hosana nas alturas!";

		String categoria = feedWS.obterCategoriaMensagem(mensagem);
		
		assertEquals("religiao", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostInedito_DeveExibirCategoriaCorreta() throws ClassNotFoundException, IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagem = "CBF divulga tabela detalhada da segunda fase da Copa do Brasil" +
						  "Confira os jogos, as datas e os horários em http://glo.bo/1r4XMdt";

		String categoria = feedWS.obterCategoriaMensagem(mensagem);
		
		assertEquals("esportes", categoria);
	}
	
	@Test
	public void testCalcularTfIdf_DeveRetornarValorCorreto()
	{
		FeedWS feedWS = new FeedWS();
		String documento1 = "this is a a sample";
		String documento2 = "this is another another example example example";
		List<String> colecao = new ArrayList<String>();
		colecao.add(documento1);
		colecao.add(documento2);
		
		Double tfIdfExample = feedWS.calcularTfIdf("example", documento2, colecao);
		Double tfIdfThis = feedWS.calcularTfIdf("this", documento2, colecao);
		
		assertEquals(0.9030, tfIdfExample, 1);
		assertEquals(0, tfIdfThis, 0);
	}
}
