package org.catfeed.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import com.restfb.types.Post;

public class PostDAO
{
	public void salvar(Post post, String nomeUsuario)
	{
		Connection c = null;
        PreparedStatement ps = null;
        
        try
        {
            c = ConnectionHelper.getConnection();
            ps = c.prepareStatement("INSERT INTO POSTS VALUES (default, ?, ?, ?, ?)");
            ps.setDate(1, formatarHoraAtual());
            ps.setString(2, post.getMessage());
            ps.setString(3, nomeUsuario);
            ps.setString(4, gerarHashMD5(post.getMessage()));
            ps.executeUpdate();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
		}
        finally
        {
			ConnectionHelper.close(c);
		}
	}
	
	private Date formatarHoraAtual()
	{
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();
		java.sql.Date sqlDate = new Date(utilDate.getTime());
		
		return sqlDate;
	}
	
	protected String gerarHashMD5(String mensagem)
	{
		MessageDigest md;

		try
		{
			md = MessageDigest.getInstance("MD5");
			md.update(mensagem.getBytes());
			
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			
			for (byte b : digest)
			{
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		} 
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return "Erro";
	}
}
