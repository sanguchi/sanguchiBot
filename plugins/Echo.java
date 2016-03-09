package plugins;

import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Echo extends Plugin
{
	public Echo()
	{
		// TODO Auto-generated constructor stub
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		text = text.substring(5);
		bot.sendMessage(to, text);
	}
	public String help()
	{
		return "/echo <texto>\nRepite lo que le digas.";
	}
}
