package plugins;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InputFile;

import sanguchi.Plugin;

import java.io.File;
public class Log extends Plugin
{
	public long current;
	public Log()
	{
		needOwner = true;
		needSanguchi = true;
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(args.size() == 2 && args.get(1).equals("raw"))
		{
			StringBuilder build = new StringBuilder();
			for(Message m : sanguchi.mensajes)
				build.append(m.toString());
			bot.sendMessage(to, build.toString());
			return;
		}
		current = me.from().id();
		File currentDir = new File(".");
		File parentDir = currentDir.getParentFile();
		File log_file = new File(parentDir, "log.txt");
		bot.sendMessage(to, "Lista de Zurditos enviada al chat privado, querido dictador :^)");
		bot.sendDocument(current, new InputFile("text/plain", log_file), null, null);
	}
	public String help()
	{
		return "(SU) - /log\n Envia un archivo de texto con el log de mensajes del bot";
	}
}
