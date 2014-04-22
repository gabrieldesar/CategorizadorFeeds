package org.catfeed.webservices;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.catfeed.Keyword;
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
		
		String mensagem1 = "luta luta luta programa";
		String mensagem2 = "programa 2014";
		String mensagem3 = "programa professores";
		
		List<String> mensagensTermosRelevantes = new ArrayList<String>();
		
		mensagensTermosRelevantes.add(mensagem1);
		mensagensTermosRelevantes.add(mensagem2);
		mensagensTermosRelevantes.add(mensagem3);
		
		
		List<Keyword> listaKeyWords = feedWS.prepararListaKeyWords(mensagensTermosRelevantes);
		
		assertEquals(4, listaKeyWords.size());
		assertEquals("programa", listaKeyWords.get(0).getText());
		assertEquals(new Integer(3), listaKeyWords.get(0).getWeight());
		assertEquals("luta", listaKeyWords.get(3).getText());
		assertEquals(new Integer(1), listaKeyWords.get(3).getWeight());
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
	
	@Test
	public void testObterCategoriaMensagem_PostSobreEsporte_DeveRetornarCategoriaCorreta() throws IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost =	"FANATISMO NA PELE! " +
								"Torcedor faz tatuagem para homenagear PROVOCAÇÃO de Felipe Melo a rival. Veja: http://glo.bo/1hFZQI2";
		
		String categoria = feedWS.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("esportes", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreEsporte_DeveRetornarCategoriaCorreta2() throws IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost =	"Lembra desse jogador aí da foto? Ele foi o maior pontuador do CartolaFC em 2013 e quer repetir a dose este ano. Já montou o seu time? O mercado fecha neste sábado, às 16h30. Corre lá! http://glo.bo/1r4SxdQ";
		
		String categoria = feedWS.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("esportes", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta() throws IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost =	"Quando que um jornal nacional faria uma matéria assim? " + 
								"A favela cansou de enterrar seus mortos calada.";
		
		String categoria = feedWS.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("outros", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta2() throws IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost =	"Churrasco de feriado tem que fazer bem feito, hein! Vem cá aprender: " +
								"http://papodehomem.com.br/como-fazer-um-churrasco-do-inicio-ao-fim/";
		
		String categoria = feedWS.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("outros", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta3() throws IOException
	{
		FeedWS feedWS = new FeedWS();
		String mensagemPost =	"Safadjeeenho!!! " +
								"Kkkkkkkkkkkkkkkkkkkkkkkk!!! " +
								"By Bruno Vettore ";
		
		String categoria = feedWS.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("outros", categoria);
	}
}
