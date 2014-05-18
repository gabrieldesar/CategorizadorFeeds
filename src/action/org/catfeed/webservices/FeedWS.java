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
	private static final String CATEGORIA_OUTROS = "outros";

	private static final String CATEGORIA_TRANSITO = "transito";

	private static final String CATEGORIA_POLITICA = "politica";

	private static final String CATEGORIA_ESPORTES = "esportes";
	
	private static final String CATEGORIA_CLIMA = "clima";
	
	private static final String CATEGORIA_PARABENS = "parabens";

	private PostDAO postDAO = new PostDAO();
	
	private CategorizadorActionBean categorizadorActionBean = new CategorizadorActionBean();
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
    public String exibirFeed(@JsonProperty("accessToken") String accessToken)
	{
		 String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		 List<String> listaMensagensPosts = categorizadorActionBean.obterListaMensagensPosts(nomeUsuarioLogado);
		 
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
		 
		 Connection<Post> newsFeed = facebookClient.fetchConnection("me/home", Post.class, Parameter.with("limit", "100"));
		 User usuarioLogado = facebookClient.fetchObject("me", User.class);
		 
		 persistirPosts(newsFeed, usuarioLogado);
    }
	
	
	@POST
	@Path("listaKeywords")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaKeywords(@JsonProperty("accessToken") String accessToken)
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		List<String> listaMensagensPosts = categorizadorActionBean.obterListaMensagensPosts(nomeUsuarioLogado);
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
		List<String> listaMensagensPosts = categorizadorActionBean.obterListaMensagensPosts(nomeUsuarioLogado);
		ArrayList<ArrayList<Object>> arrayCategoriasNumeroPosts = categorizadorActionBean.obterArrayCategoriasNumeroPosts(listaMensagensPosts);
		
		return new Gson().toJson(arrayCategoriasNumeroPosts);
	}
	
	@POST
	@Path("esportes")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsEsportes(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsEsportes = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_ESPORTES);
		
		return new Gson().toJson(listaPostsEsportes);
	}
	@POST
	@Path("parabens")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsParabens(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsParabens = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_PARABENS);
		
		return new Gson().toJson(listaPostsParabens);
	}
	
	@POST
	@Path("clima")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsClima(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsClima = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_CLIMA);
		
		return new Gson().toJson(listaPostsClima);
	}
	
	@POST
	@Path("politica")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsPolitica(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsPolitica = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_POLITICA);
		
		return new Gson().toJson(listaPostsPolitica);
	}
	
	@POST
	@Path("transito")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsTransito(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsTransito = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_TRANSITO);
		
		return new Gson().toJson(listaPostsTransito);
	}
	
	@POST
	@Path("outros")
	@Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON})
	public String obterListaPostsOutros(@JsonProperty("accessToken") String accessToken) throws IOException
	{
		String nomeUsuarioLogado = obterNomeUsuarioLogado(accessToken);
		
		List<org.catfeed.Post> listaPostsOutros = categorizadorActionBean.obterListaPostsPorCategoria(nomeUsuarioLogado, CATEGORIA_OUTROS);
		
		return new Gson().toJson(listaPostsOutros);
	}
	
	
	private String obterNomeUsuarioLogado(String accessToken)
	{
		String stringAccessToken = FeedUtils.transformarJSONEmString(accessToken, "accessToken");
		FacebookClient facebookClient = new DefaultFacebookClient(stringAccessToken);
		 
		User usuarioLogado = facebookClient.fetchObject("me", User.class);
		String nomeUsuarioLogado = usuarioLogado.getName();
		
		return nomeUsuarioLogado;
	}
	
	private void persistirPosts(Connection<Post> newsFeed, User usuarioLogado)
	{
		String nomeUsuarioLogado = FeedUtils.formatarNomeUsuarioLogado(usuarioLogado);
		
		for(Post post : newsFeed.getData())
		{
			if(post.getMessage() != null)
			{
				postDAO.salvar(post, nomeUsuarioLogado);
			}
		}
	}
}
