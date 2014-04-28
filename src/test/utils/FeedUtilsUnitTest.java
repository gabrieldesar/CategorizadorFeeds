package utils;

import static org.junit.Assert.assertEquals;

import org.catfeed.utils.FeedUtils;
import org.junit.Test;

public class FeedUtilsUnitTest
{
	@Test
	public void testTransformarJSONEmString_DeveRetornarStringCorreta()
	{
		String entrada = "{\"accessToken\":\"CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD\"}";
		
		String saida = FeedUtils.transformarJSONEmString(entrada, "accessToken");

		String esperado = "CAAC8uEGIYe4BAD9tgmerKlOOM4oO5VkkFhsz19oiSgCNAYCZATkQkvV9lDYSDB6DFMLDE0vQFre29dbaUJ0p7onypq2dS6V5S9BQym5l3ZCoROzq8eUtbfPSOJOxCMAXqOKvUtloDH0E4eRqxx8WugGGYNkZAUgUJN2bNNSYDZBgL9ZB5Lwke1b4IyVzTIMpwUxNUrQUBCAZDZD";
		assertEquals(esperado, saida);
	}
}
