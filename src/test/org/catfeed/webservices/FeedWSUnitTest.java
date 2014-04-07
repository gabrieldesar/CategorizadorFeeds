package org.catfeed.webservices;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FeedWSUnitTest
{
	@Test
	public void testTransformarJSONEmString_DeveRetornarStringCorreta()
	{
		FeedWS feedWS = new FeedWS();
		String entrada = "{\"accessToken\":\"CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD\"}";
		
		String saida = feedWS.transformarJSONEmString(entrada, "accessToken");
		String esperado = "CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD";
		
		assertEquals(esperado, saida);
	}
}
