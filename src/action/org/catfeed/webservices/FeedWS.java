package org.catfeed.webservices;

import java.io.IOException;
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
import org.catfeed.utils.FeedUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.Gson;
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
		 String stringAccessToken = FeedUtils.transformarJSONEmString(accessToken, "accessToken");
		 FacebookClient facebookClient = new DefaultFacebookClient(stringAccessToken);
		 
		 Connection<Post> newsFeed = facebookClient.fetchConnection("me/home", Post.class, Parameter.with("since", "today"), Parameter.with("limit", "100"));
		 User usuarioLogado = facebookClient.fetchObject("me", User.class);
		 
		 persistirPostsSemCategoria(newsFeed, usuarioLogado);
    }
	
	@POST
	@Path("listaKeywords")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaKeywords(@JsonProperty("accessToken") String accessToken)
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		List<Keyword> listaKeywords = categorizadorActionBean.obterListaKeywords(listaMensagensPosts);
		
		return new Gson().toJson(listaKeywords);
	}
	
	@POST
	@Path("arrayCategoriasNumeroPosts")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterArrayCategoriasNumeroPosts(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		List<String> listaMensagensPosts = obterListaMensagensPosts(nomeUsuarioLogado);
		ArrayList<ArrayList<Object>> arrayCategoriasNumeroPosts = categorizadorActionBean.obterArrayCategoriasNumeroPosts(listaMensagensPosts);
		
		return new Gson().toJson(arrayCategoriasNumeroPosts);
	}
	
	private String obterNomeUsuarioLogado(String accessToken)
	{
		String stringAccessToken = FeedUtils.transformarJSONEmString(accessToken, "accessToken");
		FacebookClient facebookClient = new DefaultFacebookClient(stringAccessToken);
		 
		User usuarioLogado = facebookClient.fetchObject("me", User.class);
		String nomeUsuarioLogado = usuarioLogado.getName();
		
		return nomeUsuarioLogado;
	}
	
	private void persistirPostsSemCategoria(Connection<Post> newsFeed, User usuarioLogado)
	{
		String nomeUsuarioLogado = FeedUtils.formatarNomeUsuarioLogado(usuarioLogado);
		
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
