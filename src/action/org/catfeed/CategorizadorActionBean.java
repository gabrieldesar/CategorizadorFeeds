package org.catfeed;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.catfeed.dao.PostDAO;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.catfeed.exceptions.DiretorioInvalidoException;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.ConditionalClassifier;
import com.aliasi.classify.TradNaiveBayesClassifier;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.CollectionUtils;
import com.aliasi.util.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CategorizadorActionBean
{
	private static final File ARQUIVO_CLASSIFIER_NAO_COMPILADO = new File(System.getProperty("user.dir") + "/src/classifierTreinado");
	
	private static final File ARQUIVO_CLASSIFIER_COMPILADO = new File(CategorizadorActionBean.class.getClassLoader().getResource("classifierTreinado").getPath());

	private static final File DIRETORIO_CATEGORIAS = new File("src/categorias");
	
	private static final String EXPRESSAO_REGULAR_SEQUENCIA_DE_CARACTERES_NAO_BRANCOS = "\\P{Z}+";

	private static final String[] CATEGORIAS =	{ "esportes", "outros", "politica", "transito", "clima", "parabens" };
	
	private static final double TFIDF_MINIMO_TERMO_RELEVANTE = 3.5;

	private static Logger log = Logger.getLogger(CategorizadorActionBean.class.getName());
	
	private PostDAO postDAO = new PostDAO();
	
	public List<Keyword> obterListaKeywords(List<String> listaMensagensPosts)
	{
		List<String> listaMensagensSemStopWords = obterListaMensagensSemStopWords(listaMensagensPosts);
		List<String> listaMensagensTermosRelevantes = obterListaMensagensTermosRelevantes(listaMensagensSemStopWords);
		List<Keyword> listaKeyWords = prepararListaKeywords(listaMensagensTermosRelevantes);
		
		return listaKeyWords;
	}
	
	public List<String> obterListaMensagensPosts(String nomeUsuarioLogado)
	{
		List<org.catfeed.Post> listaPostsUsuarioLogado = postDAO.recuperarPostsPorUsuario(nomeUsuarioLogado);
		List<String> listaMensagensPosts = new ArrayList<String>();
		
		 for(org.catfeed.Post post : listaPostsUsuarioLogado)
		 {
			 listaMensagensPosts.add(post.getMensagem());
		 }
		 
		 return listaMensagensPosts;
	}
	
	 protected void treinarBaseDeConhecimento()
     {
             TokenizerFactory tf     = new RegExTokenizerFactory(EXPRESSAO_REGULAR_SEQUENCIA_DE_CARACTERES_NAO_BRANCOS);
             Set<String> setCategorias = CollectionUtils.asSet(CATEGORIAS);
                     
             TradNaiveBayesClassifier classifier     = new TradNaiveBayesClassifier(setCategorias, tf);
                     
             for(int i = 0; i < CATEGORIAS.length; i++)
             {
                     File diretorioCategoria = new File(DIRETORIO_CATEGORIAS, CATEGORIAS[i]);
                     
                     if(!diretorioCategoria.isDirectory())
                     {
                             throw new DiretorioInvalidoException();
                     }
                             
                     String[] arquivosBaseConhecimento = diretorioCategoria.list();
                             
                     for(int j = 0; j < arquivosBaseConhecimento.length; j++)
                     {
                             File arquivoBaseConhecimento = new File(diretorioCategoria, arquivosBaseConhecimento[j]);
                             String texto;
                             try
                             {
                                     texto = Files.readFromFile(arquivoBaseConhecimento, "ISO-8859-1");
                                     Classification classification = new Classification(CATEGORIAS[i]);
                                     classifier.handle(new Classified<CharSequence>(texto, classification));
                             } 
                             catch (IOException e)
                             {
                                     e.printStackTrace();
                             }
                     }
             }
             
             try
             {
                     AbstractExternalizable.compileTo(classifier, ARQUIVO_CLASSIFIER_NAO_COMPILADO);
             } 
             catch (IOException e)
             {
                     e.printStackTrace();
             }
     }

	 @SuppressWarnings("unchecked")
     protected String obterCategoriaMensagem(String mensagem)
     {
             try 
             {                       
                     ConditionalClassifier<CharSequence> classifier = (ConditionalClassifier<CharSequence>) AbstractExternalizable.readObject(ARQUIVO_CLASSIFIER_COMPILADO);
                     
                     String mensagemSemStopWords = removerStopWords(mensagem);
                     ConditionalClassification cc = classifier.classify(mensagemSemStopWords);

                     log.debug("Entrada=" + mensagem);
                     for(int rank = 0; rank < cc.size(); rank++)
                     {
                             String categoria = cc.category(rank);
                             double condProb = cc.conditionalProbability(rank);
                             
                             log.debug("Rank=" + rank + 
                                               " Categoria=" + categoria 
                                               + " P(" + categoria + "|Entrada)=" + condProb);
                     }
                     log.debug("-------------------------");

                     return cc.category(0);
             } 
             catch (ClassNotFoundException | IOException e)
             {
                     e.printStackTrace();
             };
             
             return "erro";
     }
		
	protected List<Keyword> prepararListaKeywords(List<String> listaMensagensTermosRelevantes)
	{
		Map<String, Integer> mapaFrequenciaTermos = new HashMap<String, Integer>();
		
		for(String mensagem : listaMensagensTermosRelevantes)
		{
			String[] termos = mensagem.split(" ");
			Set<String> conjuntoTermos = new HashSet<String>(Arrays.asList(termos));
			
			for(String termo : conjuntoTermos)
			{
				Integer frequencia = mapaFrequenciaTermos.get(termo);
				mapaFrequenciaTermos.put(termo, (frequencia == null) ? 1 : frequencia + 1);
			}
		}
		
		List<Keyword> listaKeyWords = new ArrayList<Keyword>();
		
		for (Map.Entry<String, Integer> entry : mapaFrequenciaTermos.entrySet())
		{
			Keyword palavraChave = new Keyword(entry.getKey(), entry.getValue());

			listaKeyWords.add(palavraChave);
		}
		
		Collections.sort(listaKeyWords);
		
		return listaKeyWords;
	}

	protected String removerStopWords(String mensagem)
	{
		StringBuilder stringBuilderMensagemSemStopWords = new StringBuilder();
		
		Tokenizer tokenizer = new StandardTokenizer(Version.LUCENE_47, new StringReader(mensagem.toLowerCase()));

	    final StandardFilter standardFilter = new StandardFilter(Version.LUCENE_47, tokenizer);
	    
	    final StopFilter stopFilter = new StopFilter(Version.LUCENE_47, standardFilter, StopWord.STOPWORDS_PORTUGUES_INGLES);
	    
	    final CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);

	    try
	    {
			stopFilter.reset();
		} 
	    catch (IOException e)
	    {
			e.printStackTrace();
		}
	    
	    try
	    {
			while(stopFilter.incrementToken())
			{
			    final String token = charTermAttribute.toString().toString();
			    stringBuilderMensagemSemStopWords.append(token);
			    stringBuilderMensagemSemStopWords.append(" ");
			}
			stopFilter.close();
		} 
	    catch (IOException e)
	    {
			e.printStackTrace();
		}
	    
	    if(stringBuilderMensagemSemStopWords.length() > 0)
	    {
	    	stringBuilderMensagemSemStopWords.deleteCharAt(stringBuilderMensagemSemStopWords.length() - 1);
	    }
	    
	    String mensagemSemStopWords = stringBuilderMensagemSemStopWords.toString();
	    String mensagemSemStopWordsESemAcentos = Normalizer.normalize(mensagemSemStopWords, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	    
		return mensagemSemStopWordsESemAcentos;
	}
	
	protected Double calcularTfIdf(String termo, String documento, List<String> colecao)
	{
		Integer tf = calcularTf(termo, documento);
		Double idf = calcularIdf(termo, colecao);
		
		Double tfIdf = tf * idf;
		
		return tfIdf;
	}
	
	protected String transformarJSONEmString(String json, String parametro)
	{
		 JsonElement je = new JsonParser().parse(json);
		 String string = je.getAsJsonObject().get(parametro).getAsString();
		 
		 return string;
	}

	private Double calcularIdf(String termo, List<String> colecao)
	{
		Double numeroDocumentosContemTermo = new Double(0);
		
		for(String documento : colecao)
		{
			if(documento.contains(termo))
			{
				numeroDocumentosContemTermo++;
			}
		}
		
		if(numeroDocumentosContemTermo == 0)
		{
			numeroDocumentosContemTermo = new Double(1);
		}
			
		Integer numeroDocumentos = colecao.size();
		Double numeroDocumentosDouble = numeroDocumentos.doubleValue();
		
		Double idf = Math.log10(numeroDocumentosDouble / numeroDocumentosContemTermo); 
		
		return idf;
	}

	private Integer calcularTf(String termo, String documento)
	{
		List<String> listaTermos = Arrays.asList(documento.split(" "));
		
		Integer tf = 0;
		
		for(String termoIterator : listaTermos)
		{
			if(termoIterator.equals(termo))
			{
				tf++;
			}
		}
		
		return tf;
	}
	
	private List<String> obterListaMensagensTermosRelevantes(List<String> colecao)
	{
		List<String> listaMensagensTermosRelevantes = new ArrayList<String>(); 
		
		for(String documento : colecao)
		{
			List<String> listaTermos = Arrays.asList(documento.split(" "));
			
			StringBuilder mensagemTermosRelevantes = new StringBuilder();
			
			for(String termo : listaTermos)
			{
				Double tfIdf = calcularTfIdf(termo, documento, colecao);
				
				if(tfIdf >= TFIDF_MINIMO_TERMO_RELEVANTE)
				{
					mensagemTermosRelevantes.append(termo);
					mensagemTermosRelevantes.append(" ");
				}
			}
			
			if(mensagemTermosRelevantes.length() > 0)
			{
				mensagemTermosRelevantes.deleteCharAt(mensagemTermosRelevantes.length() - 1);
			}
			
			if(mensagemTermosRelevantes.length() > 0)
			{
				listaMensagensTermosRelevantes.add(mensagemTermosRelevantes.toString());
			}
		}
		
		return listaMensagensTermosRelevantes;
	}

	private List<String> obterListaMensagensSemStopWords(List<String> listaMensagensPosts)
	{
		 List<String> listaMensagensSemStopWords = new ArrayList<String>();

		 for(String mensagem : listaMensagensPosts)
		 {
				 String mensagemSemStopWords = removerStopWords(mensagem);
				 listaMensagensSemStopWords.add(mensagemSemStopWords);
		 }

		 return listaMensagensSemStopWords;
	}
	
	public ArrayList<ArrayList<Object>> obterArrayCategoriasNumeroPosts(List<String> listaMensagensPosts) throws IOException
	{
		Map<String, Integer> mapaCategoriasNumeroPosts = obterMapaCategoriasNumeroPosts(listaMensagensPosts);
		
		ArrayList<ArrayList<Object>> arrayCategoriasNumeroPosts = new ArrayList<ArrayList<Object>>();

		for(String chave : mapaCategoriasNumeroPosts.keySet())
		{
			ArrayList<Object> categoriaNumeroPost = new ArrayList<Object>();
			categoriaNumeroPost.add(chave);
			categoriaNumeroPost.add(mapaCategoriasNumeroPosts.get(chave));
			arrayCategoriasNumeroPosts.add(categoriaNumeroPost);
		}
		
		return arrayCategoriasNumeroPosts;
	} 

	protected Map<String, Integer> obterMapaCategoriasNumeroPosts(List<String> listaMensagensPosts) throws IOException
	{
		Map<String, Integer> mapaCategoriasNumeroPosts = new HashMap<String, Integer>();
		
		for(String mensagem : listaMensagensPosts)
		{
			 String mensagemSemStopWords = removerStopWords(mensagem);
			 String categoria = obterCategoriaMensagem(mensagemSemStopWords);
			 
			 Integer numeroPosts = mapaCategoriasNumeroPosts.get(categoria);
			 mapaCategoriasNumeroPosts.put(categoria, (numeroPosts == null) ? 1 : numeroPosts + 1);
		}
		
		return mapaCategoriasNumeroPosts;
	}

	public List<Post> obterListaPostsPorCategoria(String nomeUsuarioLogado, String categoria)
	{
		List<Post> listaPostsUsuarioLogado = postDAO.recuperarPostsPorUsuario(nomeUsuarioLogado);
		List<Post> listaPostsEsportes = new ArrayList<Post>();
		
		for(Post post : listaPostsUsuarioLogado)
		{
			String categoriaPost = obterCategoriaMensagem(post.getMensagem());
			
			if(categoria.equals(categoriaPost))
			{
				listaPostsEsportes.add(post);
			}
		}
		
		return listaPostsEsportes;
	}
}
