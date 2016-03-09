package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Html extends Plugin
{
	public Html()
	{}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		String text = me.text().substring(5);
		ParseMode p = ParseMode.HTML;
		bot.sendMessage(to,text, p,null,null,null);
	}
	public String help()
	{
		return "/html <texto>\nFormatea el texto en:"
				+ "\n<b>bold</b>"
				+ "\n<strong>bold</strong>"
				+ "\n<im>italic</im>"
				+ "\n<em>italic</em>"
				+ "\n<a href=\"URL\">inline URL</a>"
				+ "\n<code>inline fixed-width code</code>"
				+ "\n<pre>pre-formatted fixed-width code block</pre>";
	}
}
