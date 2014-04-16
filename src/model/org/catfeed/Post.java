package org.catfeed;

import java.sql.Date;

public class Post
{
	private int id;

	private Date data;
	
	private String mensagem;
	
	private int idCategoria;
	
	private String usuario;
	
	private String hashMensagem;
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setData(Date data)
	{
		this.data = data;
	}
	
	public void setMensagem(String mensagem)
	{
		this.mensagem = mensagem;
	}
	
	public void setIdCategoria(int idCategoria)
	{
		this.idCategoria = idCategoria;
	}
	
	public void setUsuario(String usuario)
	{
		this.usuario = usuario;
	}
	
	public void setHashMensagem(String hashMensagem)
	{
		this.hashMensagem = hashMensagem;
	}
	
	public int getId()
	{
		return id;
	}

	public Date getData()
	{
		return data;
	}

	public String getMensagem()
	{
		return mensagem;
	}

	public int getIdCategoria()
	{
		return idCategoria;
	}

	public String getUsuario()
	{
		return usuario;
	}

	public String getHashMensagem()
	{
		return hashMensagem;
	}
}
