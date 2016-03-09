package plugins;

import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Reverse extends Plugin
{
	public Reverse()
	{}
	//arreglar que no capta los reply.
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName()) && me.replyToMessage() == null){
		bot.sendMessage(to, help());
		return;}
		if(me.replyToMessage() != null)
		{
			print("reply = "+me.replyToMessage().text());
			text = reverseString(me.replyToMessage().text());
			System.out.println("text= "+me.text()+" - reply= "+text);
			bot.sendMessage(to,text);
			return;
		}
		text = text.substring(8).trim();	
		//System.out.println("mensaje = "+reply);
		bot.sendMessage(to,reverseString(text));
	}
	private String reverseString(String r)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = r.length()-1; i >= 0;i--)
			sb.append(r.charAt(i));
		return sb.toString();
	}
	
	public String help()
	{
		return "/reverse <texto>\nReordena un texto de atras para "
			+ "adelante.\nEj: /reverse hola.";
	}
}
