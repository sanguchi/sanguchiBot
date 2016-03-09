package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Green extends Plugin
{
	public Green()
	{
	}

	public void onMessage(Message me)
	{
		super.onMessage(me);
		if (text.equals("/" + getName()))
		{
			bot.sendMessage(to, help());
			return;
		}
		String text = me.text().substring(6);
		String reply = "`>" + text.substring(1) + "`";
		// System.out.println("reply = " + reply);
		bot.sendMessage(to, reply, ParseMode.Markdown, null, null, null);
	}

	public String help()
	{
		return "/green <texto>\nGreentextea como un campeon, y si, le pone '>' por vos :^).";
	}
}
