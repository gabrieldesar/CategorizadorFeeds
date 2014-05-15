package org.catfeed;

import java.sql.Date;

public class Post
{
	private int id;

	private Date data;
	
	private String mensagem;
	
	private String usuario;
	
	private String autor;
	
	private String hashMensagem;

	private long countPriority;
	
	public String getHashMensagem()
	{
		return hashMensagem;
	}
	
	public long getCountPriority() {
		return countPriority;
	}

	public void setCountPriority(long countPriority) {
		this.countPriority = countPriority;
	}

	public void setHashMensagem(String hashMensagem)
	{
		this.hashMensagem = hashMensagem;
	}

	public String getAutor()
	{
		return autor;
	}

	public void setAutor(String autor)
	{
		this.autor = autor;
	}

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
	
	public void setUsuario(String usuario)
	{
		this.usuario = usuario;
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

	public String getUsuario()
	{
		return usuario;
	}
}
