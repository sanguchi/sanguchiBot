package plugins;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;

import sanguchi.Plugin;
import sanguchi.SanguchiBot;


public class Plugins extends Plugin
{
	public String frozen_plugin;
	public String plugins_dir = SanguchiBot.root.getPath() + "/plugins/";
	public String downloads_dir = SanguchiBot.root.getPath() + "/downloads/";
	StringBuilder build;
	List<Plugin> disabled = new ArrayList<Plugin>();
	public Plugins()
	{
		needOwner = true;
		needSanguchi = true;
		init();
	}
	
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName())){
		bot.sendMessage(to, help());
		return;}
		if(args.size() > 1)
		{
			String a = args.get(1);
			if(a.equals("reload"))
			{
				reload();
				return;
			}
			if(a.equals("disable"))
			{
				if(args.size() == 3)
					disable(args.get(2));
				else
					bot.sendMessage(to, "Argumentos invalidos.");
				return;
			}
			if(a.equals("enable"))
			{
				if(args.size() == 3)
					enable(args.get(2));
				else
					bot.sendMessage(to, "Argumentos invalidos.");
				return;
			}
			if(a.equals("upload"))
			{
				bot.sendMessage(to, "code:upload\n"
						+ "Por favor responde a este mensaje con el archivo.class.");
				waitForReply = true;
			}
			if(a.equals("update"))
			{
				boolean aaa = true;
				if(aaa)
				{
					bot.sendMessage(to, "Comando parcialmente implementado, por favor no usar.");
				}
				if(args.size() == 3)
				{
					String plug = args.get(2);
					for(Plugin p : sanguchi.plugins)
						if(p.getName().equals(plug))
						{
							bot.sendMessage(to, "code:update\nPlugin `"+ plug + "` listo para"
									+ " la actualizacion.\n"
									+ "Por favor contesta a este mensaje con el plugin en formato "
									+ "*.class*",ParseMode.Markdown, null, null, null);
							waitForReply = true;
							frozen_plugin = plug;
							return;
						}
					bot.sendMessage(to, "Ese plugin no existe o no esta en la lista de plugins"
							+ "habilitados.");
				}
				else
					bot.sendMessage(to, "Argumentos invalidos.");
			}
		}
	}
	
	public boolean onReply(Message me)
	{
		if(!me.replyToMessage().text().startsWith("code:"))
			return false;
		String reply = me.replyToMessage().text();
		String code = reply.substring(5, reply.indexOf("\n"));
		print("code = " + code);
		if(code.equals("upload"))
		{
			upload(me);
			waitForReply = false;
			return true;
		}
		if(code.equals("update"))
		{
			update(me);
			waitForReply = false;
			return true;
		}
		return false;
	}
	
	public String help()
	{
		return "/plugins <argumento>\nAdministra los plugins del bot.\n"
				+ "argumentos:\n"
				+ "reload - escanea por nuevos plugins.\n"
				+ "disable <plugin> - desactiva plugins.\n"
				+ "enable <plugin> - activa plugins.\n"
				+ "upload - sirve para subir plugins.\n"
				+ "update - <plugin> actualiza plugins.";
	}
	public void reload()
	{
		int prev = sanguchi.plugins.size();
		List<String> after = new ArrayList<String>();
		for(Plugin p : sanguchi.plugins)
			after.add(p.getName());
		sanguchi.plugins = new ArrayList<Plugin>();
		sanguchi.loadPlugins();
		if(sanguchi.plugins.size() == prev)
		{
			bot.sendMessage(to, "No se encontro nada nuevo.");
			return;
		}
		StringBuilder build = new StringBuilder();
		int nw = sanguchi.plugins.size() - prev;
		build.append("Se encontraron " + nw + " plugins:\n");
		List<Plugin> np = sanguchi.plugins;
		for(Plugin p : np)
			for(String s : after)
			{
				if(s.equals(p.getName()))
					np.remove(p);
			}
		for(Plugin p: np)
			build.append("- " +p.getName()+ "\n");
		bot.sendMessage(to, build.toString());
	}
	
	public void disable(String plug)
	{
		for(Plugin p : sanguchi.plugins)
			if(p.getName().equals(plug)){
				disabled.add(p);
				sanguchi.plugins.remove(p);
				bot.sendMessage(to, "Plugin " + plug + " removido de la lista de plugins.");
				return;
			}
		bot.sendMessage(to, "Plugin no encontrado.");
	}
	
	public void enable(String plug)
	{
		for(Plugin d : disabled)
			if(d.getName().equals(plug))
			{
				sanguchi.plugins.add(d);
				disabled.remove(d);
				bot.sendMessage(to, "Plugin " + plug + " agregado de la lista de plugins.");
				return;
			}
	}
	
	public void upload(Message me)
	{
		if(me.document() == null){
			bot.sendMessage(to, "Te dije que me respondieras con un archivo.class\n"
					+ "Vas a tener que poner el comando de nuevo.");
			return;
		}
		if(me.document().fileName().endsWith(".class"))
		{
			String name = me.document().fileName();
			String id = me.document().fileId();
			String n = name.substring(0, name.indexOf(".class")).toLowerCase();
			for(Plugin p : sanguchi.plugins)
				if(p.getName().equals(n))
				{
					bot.sendMessage(to, "Este plugin ya existe, por favor usa replace.");
					return;
				}
			build = new StringBuilder();
			build.append(">BUILD LOG:\n");
			java.io.File file = downloadFile(me);
			build.append("-> Downloading File: [OK]\n");
			
			
			//String fullPath = bot.getFullFilePath(id);
			build.append("--> Filename: " + name + "\n-->ID: "+ id 
					+"\n-->Size: " + me.document().fileSize() + "\n-->Type: "
					+ me.document().mimeType()+"\n");
			//print("path = " + fullPath);
			//build.append("Path = " + fullPath + "\n"); //link de descarga del archivo.
			print("filename = " + name);
			try{
				java.io.File plugin_file = new java.io.File(plugins_dir + name);
				plugin_file.createNewFile();
				build.append("->Creating dummy plugin. [OK]\n");
				Files.copy(file.toPath(), plugin_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				build.append("->Writing dummy plugin. [OK]\n");
				print("Archivo guardado.");
				addPlugin(plugin_file);
				bot.sendMessage(to, build.toString());
				build = new StringBuilder(); // limpiamos el contenido de build.
			}
			catch(Exception e)
			{
				e.printStackTrace();
				onError(e);}	
		}
		else
			bot.sendMessage(to, "No, solo archivos .class");
	}
	public void addPlugin(java.io.File f)
	{
		
		Plugin plug = null;
		print("File name = " + f);
		String c = "plugins." + f.getName().substring(0,f.getName().indexOf(".class"));
		//String c = f.getPath();
		print("String c = " + c);
		// System.out.println(c);
		boolean isSubclass = c.contains("$");
		if (!isSubclass)
		{
			try{
				plug = (Plugin) ClassLoader.getSystemClassLoader().loadClass(c).newInstance();
				build.append("->Casting object to Plugin.class. [OK]\n");
				plug.setBot(bot);
				build.append("-->" + plug.getName() + ".needSanguchi = "+ plug.needSanguchi+ "\n");
				build.append("-->" + plug.getName()	+ ".needInfinity = "+ plug.needInfinity+ "\n");
				build.append("-->" + plug.getName() + ".needOwner = " + plug.needOwner + "\n");
				if (plug.needSanguchi)
				{
					plug.setSanguchi(this.sanguchi);
				}
				if (plug.needInfinity)
				{
					plug.setInfinity(infinity);
				}
				sanguchi.plugins.add(plug);
				build.append("->Enabling /" +  plug.getName() + " ... [OK]\n");
			}
			catch (Exception e1)
			{
				build.append("->Casting object to Plugin.class. [FAIL]\n");
				build.append("-->Class " + f.getName()	+ " is not a plugin.\n");
				build.append("->Deleting plugin... " + (f.delete() ? "[OK]" : "[FAIL]")+"\n");
				e1.printStackTrace();
				//bot.sendMessage(to, build.toString());
			}
		}
	
	}
	public void update(Message me)
	{
		if(me.document() == null){
			bot.sendMessage(to, "Te dije que me respondieras con un archivo.class\n"
					+ "Vas a tener que poner el comando de nuevo.");
			return;
		}
		String name = me.document().fileName();
		if(name.endsWith(".class")){
			String n = name.substring(0, name.indexOf(".class"));
			if(!n.equals(frozen_plugin))
			{
				bot.sendMessage(to, "El archivo debe de tener el mismo nombre del plugin.\n"
						+ "Cancelando update...");
				return;
			}
			java.io.File file = downloadFile(me);}
		else
			bot.sendMessage(to, "No, solo archivos .class");
			
	}
	
	public java.io.File downloadFile(Message me)
	{
		if(me.document() != null)
		{
			String file_name = me.document().fileName();
			String file_id = me.document().fileId();
			String file_link = bot.getFullFilePath(file_id);
			print("link = " + file_link);
			//build.append("Path = " + fullPath + "\n");
			print("filename = " + file_name);
			//build.append("Filename = " + name + "\n");
			try{
				java.io.File file = new java.io.File(downloads_dir + file_name);
				file.createNewFile();
				URL link = new URL(file_link);
				InputStream in = link.openStream();
				Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				print("Archivo guardado.");
				return file;}
			
			catch(Exception e){
				e.printStackTrace();
				onError(e);}	
		}
		return null;
	}
	private void init()
	{
		// TODO Auto-generated method stub
		java.io.File dir = new java.io.File(downloads_dir);
		if(dir.exists() && dir.isDirectory()){
			java.io.File[] contents = dir.listFiles();
			for(java.io.File f : contents)
				if(f.delete())
					print("archivo " + f.getName() + " borrado exitosamente.");
				else
					print("error al borrar el archivo " + f.getName());
		}
		if(!dir.exists())
		{
			print("directorio de descargas no existe, creando... " +
					(dir.mkdir() ? "[OK]" : "[FAIL]"));
		}
		
	}
}
