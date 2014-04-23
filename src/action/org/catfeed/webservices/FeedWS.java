package org.catfeed.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.catfeed.CategorizadorActionBean;
import org.catfeed.Keyword;
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
	
	private CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
    public String exibirFeed(@JsonProperty("accessToken") String accessToken)
	{
		 String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		 List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		 
		 String JSONListaMensagensPosts = new Gson().toJson(listaMensagensPosts);
		 
		 return JSONListaMensagensPosts;
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
	public String obterMapaFrequencias(@JsonProperty("accessToken") String accessToken)
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		List<Keyword> listaKeywords = categorizadorActionBean.obterListaKeywords(listaMensagensPosts);
		
		return new Gson().toJson(listaKeywords);
	}
	
	protected String formatarNomeUsuarioLogado(User usuarioLogado)
	{
		return usuarioLogado.getFirstName() + ' ' + usuarioLogado.getLastName();
	}
	
	protected String transformarJSONEmString(String json, String parametro)
	{
		 JsonElement je = new JsonParser().parse(json);
		 String string = je.getAsJsonObject().get(parametro).getAsString();
		 
		 return string;
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
}
