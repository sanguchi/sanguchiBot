package plugins;

import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Help extends Plugin
{
	public Help()
	{
		needSanguchi = true;
	}

	public void onMessage(Message me)
	{
		super.onMessage(me);
		if (this.args.size() == 2)
		{
			for (Plugin p : sanguchi.plugins)
			{
				if (args.get(1).equals(p.getName()))
				{
					bot.sendMessage(to, p.help());
					return;
				}
			}
			bot.sendMessage(to, "Ese Plugin no existe.");
		}
		if (text.equals("/" + getName()))
		{
			StringBuilder build = new StringBuilder();
			build.append("Plugins detectados : " + sanguchi.plugins.size()
					+ "\n");
			for (Plugin p : sanguchi.plugins)
			{
				String h = p.help().substring(0, p.help().indexOf("\n"));
				build.append(sanguchi.plugins.indexOf(p) + ") " + h + "\n");
			}
			String h = build.toString().trim();
			print("Help msj:\n" + h);
			bot.sendMessage(to, h);
		}
	}
}
