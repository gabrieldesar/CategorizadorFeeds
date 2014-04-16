package org.catfeed.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostDAO
{
	public void salvar(com.restfb.types.Post post, String nomeUsuario, int categoria)
	{
		Connection c = null;
        PreparedStatement ps = null;
        
        try
        {
            c = ConnectionHelper.getConnection();
            ps = c.prepareStatement("INSERT INTO Post VALUES (default, ?, ?, ?, ?, ?)");
            ps.setDate(1, formatarHoraAtual());
            ps.setString(2, post.getMessage());
            ps.setInt(3, categoria);
            ps.setString(4, nomeUsuario);
            ps.setString(5, gerarHashMD5(post.getMessage()));
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
	
	public List<org.catfeed.Post> recuperarPostsPorUsuario(String usuario)
	{
		List<org.catfeed.Post> listaPosts = new ArrayList<org.catfeed.Post>();
        Connection c = null;
    	String sql = "SELECT * FROM Post as p " +
			         "WHERE UPPER(Usuario) LIKE ? " +	
			         "ORDER BY Usuario";
        try
        {
            c = ConnectionHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, "%" + usuario.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                listaPosts.add(processarResultado(rs));
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
		} 
        finally
        {
			ConnectionHelper.close(c);
		}
        
        return listaPosts;
	}
	
    private org.catfeed.Post processarResultado(ResultSet rs) throws SQLException
    {
        org.catfeed.Post post = new org.catfeed.Post();
        
        post.setId(rs.getInt("Id"));
        post.setData(rs.getDate("Data"));
        post.setHashMensagem(rs.getString("HashMensagem"));
        post.setIdCategoria(rs.getInt("IdCategoria"));
        post.setMensagem(rs.getString("Mensagem"));
        post.setUsuario(rs.getString("Usuario"));
        
        return post;
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
