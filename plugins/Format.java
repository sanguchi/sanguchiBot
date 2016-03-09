package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Format extends Plugin
{
	public Format()
	{}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		boolean isHTML = (text.contains("<") && text.contains(">"));
		String text = me.text().substring(7);
		StringBuilder sb = new StringBuilder();
		String[] words;
		words = text.substring(1).split(" ");
		for(String s : words){
			if(isHTML){ //evita que algun boludito se quiera hacer el vivo.
				if(s.startsWith("*"))
					s = s.replaceAll("*", "<b>");
				if(s.endsWith("*"))
					s = s.replaceAll("*", "</b>");
				if(s.startsWith("_"))
					s = s.replaceAll("_", "<i>");
				if(s.endsWith("_"))
					s = s.replaceAll("_", "</i>");
				if(s.startsWith("`"))
					s = s.replaceAll("`", "<code>");
				if(s.endsWith("`"))
					s = s.replaceAll("`", "</code>");
				if(s.startsWith("```"))
					s = s.replaceAll("```", "<pre>");
				if(s.endsWith("`"))
					s = s.replaceAll("```", "</pre>");
				if(s.startsWith("[") && s.endsWith(")"))
				{
					String url = s.substring(1, s.indexOf("]"));
					String desc = s.substring(s.indexOf("(")+1, s.length() - 1);
					String res = "<a href=\"" + url + "\">" + desc + "</a>";
					print("url = " + url + " - desc = " + desc);
					print("result = " + res);
					s = res;
				}
			}
			sb.append(s + " ");
		}
		String reply = sb.toString();
		ParseMode p = ParseMode.Markdown;
		
		if(isHTML){
			
			print("HTML DETECTADO");
			p = ParseMode.HTML;
		}
		bot.sendMessage(to,reply, p,null,null,null);
	}
	public String help()
	{
		return "/format <texto>\nFormatea el texto en:"
				+ "\n>Greentext"
				+ "\n*bold text*"
				+ "\n_italic text_"
				+ "\n[text](URL)"
				+ "\n`inline fixed-width code`"
				+ "\n```pre-formatted fixed-width code block```"
				+ "\n<b>bold</b>"
				+ "\n<strong>bold</strong>"
				+ "\n<im>italic</im>"
				+ "\n<em>italic</em>"
				+ "\n<a href=\"URL\">inline URL</a>"
				+ "\n<code>inline fixed-width code</code>"
				+ "\n<pre>pre-formatted fixed-width code block</pre>";
	}
}
