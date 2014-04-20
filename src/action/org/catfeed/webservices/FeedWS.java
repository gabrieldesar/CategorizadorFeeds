package org.catfeed.webservices;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.catfeed.KeyWord;
import org.catfeed.StopWord;
import org.catfeed.dao.PostDAO;
import org.catfeed.exceptions.DiretorioInvalidoException;
import org.codehaus.jackson.annotate.JsonProperty;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConfusionMatrix;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.JointClassifierEvaluator;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import com.restfb.types.User;

@Path("feed")
public class FeedWS
{
	private static final double TFIDF_MINIMO_TERMO_RELEVANTE = 3.0;

	private static final File DIRETORIO_CATEGORIAS = new File("src/categorias");

	private static final boolean OPCAO_ARMAZENAR_CATEGORIAS = true;

	private static final int CONSTANTE_NGRAM = 6;

	private static final int ID_SEM_CATEGORIA = 1;
	
	private static final String[] CATEGORIAS = { "cotidiano",
                                                 "esportes",
                                                 "politica",
                                                 "religiao",
                                                 "tecnologia" };
	
	private static Logger log = Logger.getLogger(FeedWS.class.getName());
	
	private PostDAO postDAO = new PostDAO();
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
    public String exibirFeed(@JsonProperty("accessToken") String accessToken)
	{
		 String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		 List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		 
		 return new Gson().toJson(listaMensagensPosts);
    }
	
	@POST
	@Path("salvarFeed")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
    public void salvarFeed(@JsonProperty("accessToken") String accessToken)
	{
		 String stringAccessToken = transformarJSONEmString(accessToken, "accessToken");
		 FacebookClient facebookClient = new DefaultFacebookClient(stringAccessToken);
		 
		 Connection<Post> newsFeed = facebookClient.fetchConnection("me/home", Post.class, Parameter.with("since", "today"), Parameter.with("limit", "150"));
		 User usuarioLogado = facebookClient.fetchObject("me", User.class);
		 
		 persistirPostsSemCategoria(newsFeed, usuarioLogado);
    }
	
	@POST
	@Path("mapaFrequencias")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String prepararCloudFeed(@JsonProperty("accessToken") String accessToken)
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		List<String> listaMensagensSemStopWords = obterListaMensagensSemStopWords(listaMensagensPosts);
		List<String> listaMensagensTermosRelevantes = obterListaMensagensTermosRelevantes(listaMensagensSemStopWords);
		
		List<KeyWord> listaKeyWords = prepararListaKeyWords(listaMensagensTermosRelevantes);
		 
		return new Gson().toJson(listaKeyWords);
	}
	
	protected String obterCategoriaMensagem(String mensagem) throws ClassNotFoundException, IOException
	{		
		DynamicLMClassifier<NGramProcessLM> classifier = DynamicLMClassifier.createNGramProcess(CATEGORIAS, CONSTANTE_NGRAM);
		
		treinarBaseConhecimento(classifier);
		
        String bestCategory = classificarMensagem(mensagem, classifier);
        
        return bestCategory;
	}

	@SuppressWarnings("unchecked")
	private String classificarMensagem(String mensagem,	DynamicLMClassifier<NGramProcessLM> classifier)	throws ClassNotFoundException, IOException
	{
		log.debug("Compiling");
			
	    JointClassifier<CharSequence> compiledClassifier = (JointClassifier<CharSequence>) AbstractExternalizable.compile(classifier);
	    JointClassifierEvaluator<CharSequence> evaluator = new JointClassifierEvaluator<CharSequence>(compiledClassifier, CATEGORIAS, OPCAO_ARMAZENAR_CATEGORIAS);

    	log.info("Testing on " + CATEGORIAS[0] + "/" + mensagem + " ");
            
    	Classification classification = new Classification(CATEGORIAS[0]);
    	Classified<CharSequence> classified = new Classified<CharSequence>(mensagem, classification);
    	
    	evaluator.handle(classified);
    	
    	JointClassification jc = compiledClassifier.classify(mensagem);
    	
        String bestCategory = jc.bestCategory();
        String details = jc.toString();
        
        log.info("Got best category of: " + bestCategory);
        log.debug(details);
        log.info("---------------");
	
        ConfusionMatrix confMatrix = evaluator.confusionMatrix();
        log.debug("Total Accuracy: " + confMatrix.totalAccuracy());

        log.debug("\nFULL EVAL");
        log.debug(evaluator);
		return bestCategory;
	}

	private void treinarBaseConhecimento(DynamicLMClassifier<NGramProcessLM> classifier) throws IOException
	{
		for(int i = 0; i < CATEGORIAS.length; ++i)
		{
			File diretorioCategoriasBaseConhecimento = new File(DIRETORIO_CATEGORIAS, CATEGORIAS[i]);
	        
            if (!diretorioCategoriasBaseConhecimento.isDirectory())
            {
                throw new DiretorioInvalidoException();
            }

            String[] arquivosBaseConhecimento = diretorioCategoriasBaseConhecimento.list();
            
            for (int j = 0; j < arquivosBaseConhecimento.length; ++j)
            {
                File file = new File(diretorioCategoriasBaseConhecimento, arquivosBaseConhecimento[j]);
                
                String text = Files.readFromFile(file,"ISO-8859-1");
                
                log.debug("Training on " + CATEGORIAS[i] + "/" + arquivosBaseConhecimento[j]);
                
                Classification classification = new Classification(CATEGORIAS[i]);
                
                Classified<CharSequence> classified = new Classified<CharSequence>(text, classification);
                
                classifier.handle(classified);
            }
		}
	}

	protected String transformarJSONEmString(String json, String parametro)
	{
		 JsonElement je = new JsonParser().parse(json);
		 String string = je.getAsJsonObject().get(parametro).getAsString();
		 
		 return string;
	}
	
	protected List<KeyWord> prepararListaKeyWords(List<String> listaMensagensTermosRelevantes)
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
		
		List<KeyWord> listaKeyWords = new ArrayList<KeyWord>();
		
		for (Map.Entry<String, Integer> entry : mapaFrequenciaTermos.entrySet())
		{
			KeyWord palavraChave = new KeyWord(entry.getKey(), entry.getValue());

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
		
		return mensagemSemStopWords;
	}
	
	protected Double calcularTfIdf(String termo, String documento, List<String> colecao)
	{
		Integer tf = calcularTf(termo, documento);
		Double idf = calcularIdf(termo, colecao);
		
		Double tfIdf = tf * idf;
		
		return tfIdf;
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

	private List<String> obterListaMensagensPosts(String nomeUsuarioLogado)
	{
		List<org.catfeed.Post> listaPostsUsuarioLogado = postDAO.recuperarPostsPorUsuario(nomeUsuarioLogado);
		List<String> listaMensagensPosts = new ArrayList<String>();
		
		 for(org.catfeed.Post post : listaPostsUsuarioLogado)
		 {
			 listaMensagensPosts.add(post.getMensagem());
		 }
		 
		 return listaMensagensPosts;
	}
		
	private String obterNomeUsuarioLogado(String accessToken)
	{
		String stringAccessToken = transformarJSONEmString(accessToken, "accessToken");
		FacebookClient facebookClient = new DefaultFacebookClient(stringAccessToken);
		 
		User usuarioLogado = facebookClient.fetchObject("me", User.class);
		String nomeUsuarioLogado = usuarioLogado.getName();
		
		return nomeUsuarioLogado;
	}
	
	private void persistirPostsSemCategoria(Connection<Post> newsFeed, User usuarioLogado)
	{
		String nomeUsuarioLogado = formatarNomeUsuarioLogado(usuarioLogado);
		
		for(Post post : newsFeed.getData())
		{
			if(post.getMessage() != null)
			{
				postDAO.salvar(post, nomeUsuarioLogado, ID_SEM_CATEGORIA);
			}
		}
	}
	
	private String formatarNomeUsuarioLogado(User usuarioLogado)
	{
		return usuarioLogado.getFirstName() + ' ' + usuarioLogado.getLastName();
	}
}
