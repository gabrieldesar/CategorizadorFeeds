package org.catfeed.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.restfb.types.User;

public class FeedUtils
{
	public static String formatarNomeUsuarioLogado(User usuarioLogado)
	{
		return usuarioLogado.getFirstName() + ' ' + usuarioLogado.getLastName();
	}
	
	public static String transformarJSONEmString(String json, String parametro)
	{
		 JsonElement je = new JsonParser().parse(json);
		 String string = je.getAsJsonObject().get(parametro).getAsString();
		 
		 return string;
	}
}
