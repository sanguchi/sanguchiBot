package plugins;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Message;

import sanguchi.Plugin;

public class Waifu extends Plugin
{
	public HashMap<String, ArrayList<String>> waifus = new HashMap<String, ArrayList<String>>();
	public java.io.File waifus_file = new java.io.File("waifus.json");
	public Waifu()
	{
		load();
	}
	public void onMessage(Message me)
	{
		super.onMessage(me);
		if(text.equals("/"+getName()))
		{
			
			ArrayList<String> w = getWaifus(me.from().id().toString());
			if(w == null)
			{
				bot.sendMessage(to, "No, vos no tenes waifus\nPone /help waifu "
						+ "para saber como funciona esto.");
			}
			else
			{
				StringBuilder build = new StringBuilder();
				build.append("User :" + me.from().firstName() + " - Waifus:\n");
				for(String s : w)
					build.append("> " + s + "\n");
				bot.sendMessage(to, build.toString());
			}
		}
		if(args.size() == 2)
		{
			bot.sendMessage(to, "Argumentos insuficientes, para mas info:\n/help waifu.");
		}
		if(args.size() >= 3)
		{
			if(args.get(1).equals("claim"))
			{
				String arg = text.substring(text.indexOf("claim") + 5);
				bot.sendMessage(to, arg +
				(addWaifu(me.from().id().toString(), arg.trim()) ? " Es tu waifu ahora :^)" : " No esta disponible."));
			}
			if(args.get(1).equals("divorce"))
			{
				String arg = text.substring(text.indexOf("divorce") + 7);
				if(deleteWaifu(me.from().id().toString(), arg))
					bot.sendMessage(to, arg + " Ahora esta disponible :^)");
				else
					bot.sendMessage(to, arg + " No es tu waifu, tomatela.");
					
			}
		}
		
	}
	public String help()
	{
		return "/waifu\n-Devuelve una lista con tus waifus.\n/waifu claim <nombre>\n"
				+ "-Te permite reclamar tu waifu.\n/waifu divorce <nombre>\n"
				+ "-Te separas de tu waifu y la dejas disponible :^)";
	}
	public ArrayList<String> getWaifus(String user)
	{
		for(String l : waifus.keySet())
			if(l.equals(user))
				return waifus.get(l);
		return null;
		
	}
	public void onError(Exception e)
	{
		e.printStackTrace();
	}
	public boolean addWaifu(String user, String waifu)
	{
		for(ArrayList<String> array : waifus.values())
			for(String s : array)
				if(s.toLowerCase().equals(waifu.toLowerCase()))
					return false;
		for(String l : waifus.keySet())
			if(l.equals(user))
			{
				waifus.get(user).add(waifu);
				save();
				return true;
			}
		ArrayList<String> a = new ArrayList<String>();
		a.add(waifu);
		waifus.put(user, a);
		save();
		return true;
	}
	public boolean deleteWaifu(String user, String waifu)
	{
		for(String l : waifus.keySet())
			if(l.equals(user))
				return waifus.get(l).remove(waifu);
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		if (this.waifus_file.exists())
		{
			try
			{
				Gson gson = new Gson();
				BufferedReader br;
				br = new BufferedReader(new FileReader(waifus_file));
				waifus = gson.fromJson(br, waifus.getClass());
				// System.out.println(con);
				br.close();
				print("Archivo de waifus cargado.");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			print("No se ha encontrado el archivo.");
		}
	}

	public void save()
	{
		if (!waifus_file.exists())
		{
			print("El archivo no existe, creando...");
			try
			{
				waifus_file.createNewFile();
				
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			FileWriter save = null;
			try
			{
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String c = gson.toJson(waifus);
				// System.out.println("json = \n" + c);
				save = new FileWriter(waifus_file);
				save.write(c);
				save.close();
				print("Archivo guardado.");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				print("No se pudo guardar el archivo.");
				e.printStackTrace();
			}
		}
	}
}