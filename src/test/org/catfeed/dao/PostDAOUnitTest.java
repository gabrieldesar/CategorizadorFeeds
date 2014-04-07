package org.catfeed.dao;

import org.junit.Assert;
import org.junit.Test;

public class PostDAOUnitTest
{
	@Test
	public void testGerarHashMD5_DeveRetornarHashCorreto()
	{
		PostDAO postDAO = new PostDAO();
		
		String entrada = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
		
		Assert.assertEquals("35899082e51edf667f14477ac000cbba", postDAO.gerarHashMD5(entrada));
	}
}
