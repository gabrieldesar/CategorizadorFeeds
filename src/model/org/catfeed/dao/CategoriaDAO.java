package org.catfeed.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.catfeed.Categoria;

public class CategoriaDAO
{
	public List<Categoria> recuperarTodasCategorias()
	{
		List<Categoria> listaCategorias = new ArrayList<Categoria>();
        Connection c = null;
    	String sql = "SELECT * FROM Categoria";
        try
        {
            c = ConnectionHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                listaCategorias.add(processarResultado(rs));
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
        
        return listaCategorias;
	}

    private Categoria processarResultado(ResultSet rs) throws SQLException
    {
        Categoria categoria = new Categoria();
        
        categoria.setId(rs.getInt("Id"));
        categoria.setNome(rs.getString("Nome"));
        
        return categoria;
    }
}
