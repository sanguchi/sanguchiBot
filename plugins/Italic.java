package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Italic extends Plugin
{
	public Italic(){}
	
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName())){
			bot.sendMessage(to, help());
			return;}
		text = text.substring(7);
		String reply = "_"+text+"_";
		bot.sendMessage(to,reply,ParseMode.Markdown,null,null,null);
	}
	public String help()
	{
		return "/italic <texto>\nRepite el texto pero en 'Italic'.";
	}
}
