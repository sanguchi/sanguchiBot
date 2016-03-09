package plugins;

import sanguchi.Plugin;

import com.pengrad.telegrambot.model.Message;

public class Su extends Plugin
{
	String prompt = "Por favor responde a este mensaje con la contraseña del bot.";
	public Su()
	{
		needSanguchi = true;
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName()))
			return;
		text = text.substring(3).trim();
		if(text.equals(sanguchi.su_pass))
		{
			sanguchi.owner = id;
			bot.sendMessage(id, "Acceso Garantizado :^)");
			return;
		}
	}
}
