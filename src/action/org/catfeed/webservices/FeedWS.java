package org.catfeed.webservices;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.catfeed.KeyWord;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.catfeed.StopWord;
import org.catfeed.dao.PostDAO;
import org.codehaus.jackson.annotate.JsonProperty;

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
	private static final int ID_SEM_CATEGORIA = 1;
	
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
		List<KeyWord> listaKeyWords = prepararListaKeyWords(listaMensagensSemStopWords);
		 
		return new Gson().toJson(listaKeyWords);
	}
	
	private List<String> obterListaMensagensSemStopWords(List<String> listaMensagensPosts)
	{
		 List<String> listaMensagensSemStopWords = new ArrayList<String>();

		 for(String mensagem : listaMensagensPosts)
		 {
				 List<String> palavrasChaveMensagem = extrairPalavrasChave(mensagem);
				 listaMensagensSemStopWords.addAll(palavrasChaveMensagem);
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

	protected String transformarJSONEmString(String json, String parametro)
	{
		 JsonElement je = new JsonParser().parse(json);
		 String string = je.getAsJsonObject().get(parametro).getAsString();
		 
		 return string;
	}
	
	protected List<KeyWord> prepararListaKeyWords(List<String> listaMensagensSemStopWords)
	{
		Map<String, Integer> mapaFrequenciaPalavrasChave = new HashMap<String, Integer>();
		
		for(String palavraChave : listaMensagensSemStopWords)
		{
			Integer frequencia = mapaFrequenciaPalavrasChave.get(palavraChave);
			mapaFrequenciaPalavrasChave.put(palavraChave, (frequencia == null) ? 1 : frequencia + 1);
		}
		
		List<KeyWord> listaPalavrasChave = new ArrayList<KeyWord>();
		
		for (Map.Entry<String, Integer> entry : mapaFrequenciaPalavrasChave.entrySet())
		{
			KeyWord palavraChave = new KeyWord(entry.getKey(), entry.getValue());

			listaPalavrasChave.add(palavraChave);
		}
		
		Collections.sort(listaPalavrasChave);
		
		return listaPalavrasChave;
	}

	protected List<String> extrairPalavrasChave(String mensagem)
	{
		List<String> palavrasChave = new ArrayList<String>();
		
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
			    palavrasChave.add(token);
			}
			stopFilter.close();
		} 
	    catch (IOException e)
	    {
			e.printStackTrace();
		}
		
		return palavrasChave;
	}
	
	private String formatarNomeUsuarioLogado(User usuarioLogado)
	{
		return usuarioLogado.getFirstName() + ' ' + usuarioLogado.getLastName();
	}
}
