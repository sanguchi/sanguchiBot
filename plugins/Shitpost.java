package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;

public class Shitpost extends Plugin
{
	public long id;
	public boolean running;
	String shitword;
	public Shitpost()
	{
		leaveListen = true;
		running = false;
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		print("args.size() = " + args.size());
		if(args.size() == 2){
			if(args.get(1).equals("stop")){
				running = false;
				bot.sendMessage(to, "Esta bien, Shitposting abortado.");
				print("Cancelando shitposting");
				return;}
		}
		if(args.size() < 3)
		{
			bot.sendMessage(to, "Argumentos invalidos.");
			print("text = " + text);
			return;
		}
		
		try{id = Long.parseLong(args.get(1));}
		catch(NumberFormatException e)
		{
			print("error en long, args(1) = " + args.get(1));
			onError(e);
			return;
		}
		args.remove(0);
		args.remove(0);
		StringBuilder build = new StringBuilder();
		for(String z : args)
			build.append(z + " ");
		shitword = build.toString();
		bot.sendMessage(to, "Shitposteando en el grupo con id = " + id() + "\nShitword = " + shitword);
		leaveListen = true;
		running = true;
		Thread thread;
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				while(running){
					
					try{bot.sendMessage(id(), shitword,ParseMode.Markdown,true,null,null);
					Thread.sleep(2000);
					}
				catch(Exception e)
				{
					onError(e);
					running = false;
				}
				}
			}
		});
		thread.start();
	}
	public String getWord()
	{
		return shitword;
	}
	public boolean onLeave(Message me)
	{
		print("onLeave Event for Plugin " + getName());
		String g = me.chat().title();
		bot.sendMessage(to, "Listo, me sacaron del grupo " + g);
		running = false;
		leaveListen = false;
		return false;
	}
	public long id()
	{
		return id;
	}
	
}
