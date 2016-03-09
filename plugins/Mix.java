package plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Mix extends Plugin
{
	
	
	public Mix()
	{
		//avoidErrors = true;
	}
	
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if (text.equals("/" + getName())&& me.replyToMessage() == null)
		{
			bot.sendMessage(to, help());
			return;
		}
		print("args.size() = " + args.size());
		if(me.replyToMessage() != null && me.replyToMessage().text() != null)
		{
			Pattern pattern = Pattern.compile("/mix[0-9]");
			int times = 1;
			Matcher matcher = pattern.matcher(text);
			if(matcher.find())
			{
				String com = matcher.group();
				pattern = Pattern.compile("[0-9]");
				matcher = pattern.matcher(com);
				matcher.find();
				times = Integer.parseInt(matcher.group());
				if(times <= 0)
					times = 1;
			}
			StringBuilder build = new StringBuilder();
			if(times > 1)
				build.append("Aplicando Mix " + times + " veces.\n");
			for(int i = 0; i < times; i++)
				build.append(mixString(me.replyToMessage().text()) + "\n");
			print("Mixeando " + times + " veces.");
			bot.sendMessage(to,build.toString());
			return;
		}
		String a = me.text().substring(4);
		bot.sendMessage(to, mixString(a).trim());
	}
	private String mixString(String in)
	{	
		if(in.length() < 1 || in == null)
			return "Nada que desordenar.";
		List<String> words = new ArrayList<String>();
		String[] a = in.split(" ");
		for(String s : a)
			words.add(s);
		// System.out.println("words.length = " + words.length);
		StringBuilder build = new StringBuilder();
		// System.out.println("c.size() = " + c.size());
		while (words.size() > 0)
		{
			// try{Thread.sleep(500);}catch(Exception f){System.out.println("ERROR");}
			int rand = (int) (Math.random() * 10);
			// System.out.println(rand);
			while (rand >= words.size())
				rand = (int) (Math.random() * 10);
			
			build.append(words.get(rand) + " ");
			words.remove(rand);
		}
		return build.toString();
	}
	
	public String help()
	{
		return "/mix <texto>\nOrdena las palabras de forma aleatoria.";
	}
}
