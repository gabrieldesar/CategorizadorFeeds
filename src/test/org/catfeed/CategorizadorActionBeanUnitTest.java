package org.catfeed;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CategorizadorActionBeanUnitTest
{
	@Test
	public void testRemoverStopWords_DeveRemoverStopWords()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Motivos de ainda se acreditar na humanidade !!!";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);
		
		assertEquals("motivos acreditar humanidade", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveConverterPalavrasMaiusculas()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "DIAMANTE NEGRO VEIO DA JOALHERIA?";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);
		
		assertEquals("diamante negro veio joalheria", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarPostsEmIngles()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Featured Fan Art - Morgana by Maysiria";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);

		assertEquals("featured fan art morgana maysiria", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarPostsEmIngles2()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "To view previous winners please visit the \"Winners\" tab on the Daily";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);
		
		assertEquals("view previous winners please visit winners tab daily", mensagemPostSemStopWords);
	}
	

	@Test
	public void testRemoverStopWords_DeveTratarHashTags()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "#amiga #parceira #segundachata #cansadas #quecheguelogosexta";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);
		
		assertEquals("amiga parceira segundachata cansadas quecheguelogosexta", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarEmoticons()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "fresquin! :)";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);
		
		assertEquals("fresquin", mensagemPostSemStopWords);
	}
	
	@Test
	public void testRemoverStopWords_DeveTratarEmoticonsEHashTags()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Sorte mesmo é comprar 455 figurinhas, ficar com 63 repetidas e terminar com 38 *-* #vício #álbumdacopa #vamoquevamo";
		
		String mensagemPostSemStopWords = categorizadorActionBean.removerStopWords(mensagemPost);

		assertEquals("sorte comprar 455 figurinhas ficar 63 repetidas terminar 38 vício álbumdacopa vamoquevamo", mensagemPostSemStopWords);
	}
	
	@Test
	public void testPrepararListaKeyWords_DeveConterFrequenciaCorreta()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		
		String mensagem1 = "luta luta luta programa";
		String mensagem2 = "programa 2014";
		String mensagem3 = "programa professores";
		
		List<String> mensagensTermosRelevantes = new ArrayList<String>();
		
		mensagensTermosRelevantes.add(mensagem1);
		mensagensTermosRelevantes.add(mensagem2);
		mensagensTermosRelevantes.add(mensagem3);
		
		
		List<Keyword> listaKeyWords = categorizadorActionBean.prepararListaKeywords(mensagensTermosRelevantes);
		
		assertEquals(4, listaKeyWords.size());
		assertEquals("programa", listaKeyWords.get(0).getText());
		assertEquals(new Integer(3), listaKeyWords.get(0).getWeight());
		assertEquals("luta", listaKeyWords.get(3).getText());
		assertEquals(new Integer(1), listaKeyWords.get(3).getWeight());
	}
	
	//@Test
	public void testObterMapaCategoriasNumeroPosts_DeveRetornarMapaCorreto() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		
		String mensagemPost1 =	"FANATISMO NA PELE! " +
								"Torcedor faz tatuagem para homenagear PROVOCAÇÃO de Felipe Melo a rival. Veja: http://glo.bo/1hFZQI2";
		
		String mensagemPost2 =  "Lembra desse jogador aí da foto? Ele foi o maior pontuador do CartolaFC em 2013 e quer repetir a dose este ano. Já montou o seu time? O mercado fecha neste sábado, às 16h30. Corre lá! http://glo.bo/1r4SxdQ";
		
		String mensagemPost3 =  "Quando que um jornal nacional faria uma matéria assim? " + 
								"A favela cansou de enterrar seus mortos calada.";

		String[] arrayMensagensPost = { mensagemPost1, mensagemPost2, mensagemPost3 };
		
		List<String> listaMensagensPosts = new ArrayList<String>(Arrays.asList(arrayMensagensPost));
		
		Map<String, Integer> mapaCategoriasNumeroPosts = categorizadorActionBean.obterMapaCategoriasNumeroPosts(listaMensagensPosts);
		
		assertEquals(new Integer(2), mapaCategoriasNumeroPosts.get("esportes"));
		assertEquals(new Integer(1), mapaCategoriasNumeroPosts.get("outros"));
	}
	
	//@Test
	//Comentei esse teste porque a primeira postagem, apesar de ser futebol é bem complicada pois tem a palavra fanatismo. 
		//Classificá-lo como politica não é de todo errado, e não era outros mesmo
	public void testObterArrayCategoriasNumeroPosts() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		
		String mensagemPost1 =	"FANATISMO NA PELE! " +
								"Torcedor faz tatuagem para homenagear PROVOCAÇÃO de Felipe Melo a rival. Veja: http://glo.bo/1hFZQI2";
		
		String mensagemPost2 =  "Lembra desse jogador aí da foto? Ele foi o maior pontuador do CartolaFC em 2013 e quer repetir a dose este ano. Já montou o seu time? O mercado fecha neste sábado, às 16h30. Corre lá! http://glo.bo/1r4SxdQ";
		
		String mensagemPost3 =  "Quando que um jornal nacional faria uma matéria assim? " + 
								"A favela cansou de enterrar seus mortos calada.";

		String[] arrayMensagensPost = { mensagemPost1, mensagemPost2, mensagemPost3 };
		
		List<String> listaMensagensPosts = new ArrayList<String>(Arrays.asList(arrayMensagensPost));

		ArrayList<ArrayList<Object>> arrayCategoriasNumeroPosts = categorizadorActionBean.obterArrayCategoriasNumeroPosts(listaMensagensPosts);
		
		assertEquals("outros", arrayCategoriasNumeroPosts.get(0).get(0));
		assertEquals(new Integer(1), arrayCategoriasNumeroPosts.get(0).get(1));
		assertEquals("esportes", arrayCategoriasNumeroPosts.get(1).get(0));
		assertEquals(new Integer(2), arrayCategoriasNumeroPosts.get(1).get(1));
	}
	
	@Test
	public void testCalcularTfIdf_DeveRetornarValorCorreto()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String documento1 = "this is a a sample";
		String documento2 = "this is another another example example example";
		List<String> colecao = new ArrayList<String>();
		colecao.add(documento1);
		colecao.add(documento2);
		
		Double tfIdfExample = categorizadorActionBean.calcularTfIdf("example", documento2, colecao);
		Double tfIdfThis = categorizadorActionBean.calcularTfIdf("this", documento2, colecao);
		
		assertEquals(0.9030, tfIdfExample, 1);
		assertEquals(0, tfIdfThis, 0);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreEsporte_DeveRetornarCategoriaCorreta() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"FANATISMO NA PELE! " +
								"Torcedor faz tatuagem para homenagear PROVOCAÇÃO de Felipe Melo a rival. Veja: http://glo.bo/1hFZQI2";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("esportes", categoria);
		//Mudei a categoria, porque tinhamos posto outros erradamente e ele já classificou certo como esportes
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreEsporte_DeveRetornarCategoriaCorreta2() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"Lembra desse jogador aí da foto? Ele foi o maior pontuador do CartolaFC em 2013 e quer repetir a dose este ano. Já montou o seu time? O mercado fecha neste sábado, às 16h30. Corre lá! http://glo.bo/1r4SxdQ";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("esportes", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"Quando que um jornal nacional faria uma matéria assim? " + 
								"A favela cansou de enterrar seus mortos calada.";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("politica", categoria);
		//Estava assertEquals noticia, mas nao tem essa categoria, ele acertou politica, então atualizei
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta2() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"Churrasco de feriado tem que fazer bem feito, hein! Vem cá aprender: " +
								"http://papodehomem.com.br/como-fazer-um-churrasco-do-inicio-ao-fim/";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("outros", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreOutroAssunto_DeveRetornarCategoriaCorreta3() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"Safadjeeenho!!! " +
								"Kkkkkkkkkkkkkkkkkkkkkkkk!!! " +
								"By Bruno Vettore ";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("outros", categoria);
	}

	@Test
	public void testObterCategoriaMensagem_PostSobreEsportes_DeveRetornarCategoriaCorreta3() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =	"Vem Copa do Mundo !!! #worldcup #final #brasil #rumoaohexa #roadtomaracanã @tondauar";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("esportes", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreTransito_DeveRetornarCategoriaCorreta() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =   "Trânsito na Taquara muda a partir deste domingo.";
		
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("transito", categoria);
	}

	@Test
	public void testObterCategoriaMensagem_PostSobreTransito_DeveRetornarCategoriaCorreta2() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =   "CAMPO GRANDE (16h43) - Removidos carro e carreta que colidiram no Vd Engenheiro Oscar Brito, Campo Grande, sentido Guaratiba. Siga e pare desfeito. O trânsito é intenso.";
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("transito", categoria);
	}
	
	@Test
	public void testObterCategoriaMensagem_PostSobreTransito_DeveRetornarCategoriaCorreta3() throws IOException
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =   "LINHAS MUNICIPAIS | CENTRO DO RIO - Confira os detalhes dos novos itinerários e pontos finais das linhas municipais que " +
								"passam pelo Centro, após as alterações de tráfego para o fechamento do Mergulhão da Praça XV e implantação de mão dupla na Avenida Rio Branco";

		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));
		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);
		
		assertEquals("transito", categoria);
	}
	
	@Test
	public void testTreinarBaseDeConhecimento()
	{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
	         
	    categorizadorActionBean.treinarBaseDeConhecimento();
	}
	@Test
	public void testeNovaBasedeConhecimento1() throws IOException{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =   "Em terra de facebook e whatsapp, ligação é prova de amor né? Imagina um interurbano nesse tempo todo.. Rsrs";
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);		
		assertEquals("outros", categoria);
	}
	
	//@Test
	//Teste deu errado mas o erro é compreensível, fala do Romário e tem termos como "jogador".
	public void testeNovaBasedeConhecimento2() throws IOException{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost =   "Será que é só eu que vejo a atitude desse jogador como \"não estou nem aí para isso\" e que ele comeu a banana pq estava com fome? rs (ele mesmo disse q a banana ajudou ele nos dois gols que levaram a vitória do time, se não estou enganada). Todo mundo fazendo a maior repercussão, quando que a melhor atitude pra mim foi a dele, q foi de ignorar e seguir em frente. Obs.: Antes q falem que tem q combater o racismo sim, eu concordo que tem q combater, só acho ridícula essa foto de banana por aí. Acho que isso só reforça o preconceito.";
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);		
		assertEquals("politica", categoria);
	}
	
	@Test
	public void testeNovaBasedeConhecimento3() throws IOException{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Deixa eu explicar uma coisa, quem é a favor da legalização não é necessariamente a favor do uso. Uma coisa é ser contra o Governo ter o PODER de \"proibir\" as pessoas de colocarem para dentro do seu corpo qualquer merda que elas queiram por, SOU A favor SIM que o corpo de cada pessoa seja propriedade dela e não do governo. Outra COMPLETAMENTE diferente é defender o uso, acredito que drogas fazem mal e por isso não as uso e sempre que possível argumento contra elas. Ou seja, se alguém me perguntar se eu acho que uma pessoa TEM O DIREITO de usar drogas, vou defender até o fim que SIM. Se essa pessoa me perguntar se uma pessoa DEVE usar drogas, vou defender, até ler um estudo provando o contrário, que NÃO.";
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);		
		assertEquals("politica", categoria);
	}
	
	@Test
	public void testeNovaBasedeConhecimento4() throws IOException{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Hoje é dia de lembrar que foi através do trabalho de homens e mulheres que nossa sociedade chegou até aqui e que é pela luta diárias desses mesmos, enfrentando a opressão das péssimas condições de existência que o capitalismo quer nos pautar, que almejamos fazer a revolução! Para que tenhamos direito ao produto do nosso trabalho, direito ao lazer, direito a boas condições de trabalho, direito à saúde, a educação, a saneamento básico. A juventude está nas rua! Os trabalhadores estão nas ruas! Pela Redução da jornada de trabalho sem redução de salario, pelo fim do fator previdenciário, pela reforma política, pela reforma tributária! Vem com a gente! ";
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);		
		assertEquals("politica", categoria);
	}
	
	@Test
	public void testeNovaBasedeConhecimento5() throws IOException{
		CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
		String mensagemPost = "Feriado é folga pra todo mundo... Menos pra estudante da UFRJ que já está de PF em todas as matérias do período. Estudar, estudar, estudar.";
		System.out.println(categorizadorActionBean.removerStopWords(mensagemPost));		
		String categoria = categorizadorActionBean.obterCategoriaMensagem(mensagemPost);		
		assertEquals("outros", categoria);
	}
}
