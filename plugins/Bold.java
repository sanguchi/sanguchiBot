package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Bold extends Plugin
{
	public Bold()
	{}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		text = text.substring(5);
		String reply = "*"+text+"*";
		bot.sendMessage(to,reply,ParseMode.Markdown,null,null,null);	
	}
	public String help()
	{
		return "/bold <texto>\nPasa el texto dado a negrita.";
	}	
}
