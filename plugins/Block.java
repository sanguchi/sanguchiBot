package plugins;

import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Block extends Plugin
{
	public Block()
	{
		needOwner = true;
		needSanguchi = true;
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		long a = Long.parseLong(args.get(1));
		sanguchi.blocked_users.add(a);
		bot.sendMessage(to, "Zurdito Bloqueado! ;^)");
	}
	public String help()
	{
		return "(SU) - /block <id>\nBloquea a un usuario por parte del bot y evita que este pueda"
				+ "enviarle comandos al bot.";
	}
}
