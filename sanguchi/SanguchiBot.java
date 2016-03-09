package sanguchi;

import infinity.Infinity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

public class SanguchiBot
{
	public static URL root = SanguchiBot.class.getClassLoader().getResource("");
	public static String plugins_dir = root.getPath() + "/plugins/";

	File config_file = new File("sanguchi.txt");
	File log_file = new File("log.txt");
	public TelegramBot bot;
	int last_update, limit, sleep_time, save_delay, delay, last_message_id;
	public String su_pass;
	private String token;
	public boolean debug, save_log;
	Config config;
	public Infinity infinity;
	public long owner;
	public String username;
	public List<Long> blocked_users = new ArrayList<Long>();
	public List<Long> sudo_users = new ArrayList<Long>();
	public List<Message> mensajes = new ArrayList<Message>();
	public List<Plugin> plugins = new ArrayList<Plugin>();
	boolean lock = true;

	public static void main(String[] args)
	{
		PrintStream ps = null;
		File file = new File("crash.log");
		try
		{
			ps = new PrintStream(file);
			new SanguchiBot();
		} catch (Exception ex)
		{
			ex.printStackTrace(ps);
			ex.printStackTrace();
		}
		ps.close();
	}

	public SanguchiBot()
	{
		config = loadConfig();
		token = config.token;
		last_update = config.last_update + 1;
		limit = config.limit;
		sleep_time = config.sleep_time;
		su_pass = config.su_pass;
		debug = config.debug;
		save_log = config.save_log;
		save_delay = config.save_delay;
		delay = save_delay;
		username = config.username;
		last_message_id = config.last_message_id;
		if (token.equals("") || token.equals("token_here") || token == null
				|| token.length() < 1)
		{
			System.out
					.println("No se encontro el Token.\nPor favor revisa el archivo de"
							+ " configuracion. (sanguchi.txt)");
			save();
			System.exit(0);
		}
		owner = config.owner;
		blocked_users = config.blocked_users;
		sudo_users = config.sudo_users;
		infinity = new Infinity();
		infinity.debug = debug;
		bot = TelegramBotAdapter.build(token);
		loadPlugins();
		for (Plugin p : plugins)
			print(p.getName());
		// for(String s : listPlugins())
		// print(s);
		start();
	}

	public void start()
	{
		System.out.println("Logger de mensajes "
				+ (save_log ? "activado." : "desactivado"));
		GetMeResponse g = bot.getMe();
		if (!g.isOk())
		{
			System.out
					.println("Hubo un error al tratar de conectarse a Telegram, Abortando...");
			System.exit(0);
		}
		print((g.isOk() ? "isOk da true" : "isOk da false"));
		print("g.toString() = " + g.toString());
		print("username = " + g.user().username());
		username = g.user().username();
		loadLog();
		System.out
				.println("Owner ID = " + owner + "\nSanguchiBot Activado :^)");

		while (true)
		{
			update();
			try
			{
				Thread.sleep(sleep_time);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			delay--;
			if (delay <= 0)
			{
				System.out.println("Mensajes Procesados: " + last_message_id);
				save();
				if (save_log)
					saveLog();
				delay = save_delay;
			}

		}
	}

	public void update()
	{
		int intentos = 0;
		GetUpdatesResponse updatesResponse = null;
		try
		{
			updatesResponse = bot.getUpdates(last_update, limit, 0);
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage()
					+ " - intentos = " + intentos);
			intentos++;
			if (intentos > 10)
				e.printStackTrace();
			else
				return;
		}
		intentos = 0;
		List<Update> update = updatesResponse.updates();
		for (Update u : update)
		{
			Message me = u.message();
			//if(me.text() != null && u.updateId() > last_update)
			if (u.updateId() > last_update)
			{
				last_update = u.updateId();
				if (me.messageId() > last_message_id)
				{
					last_message_id = me.messageId();
					procesar(me);
				}
				// offset++;
			}
			// System.out.println(u);
		}

		print("l:" + limit + " - l_u:" + last_update + " - l_m_i = "
				+ last_message_id);
	}

	public void procesar(Message me)
	{
		for (long l : blocked_users)
			if (me.from().id() == l)
			{
				print("Ignorando usuario id = " + me.from().id()
						+ "por bloqueo.");
				return;
			}
		if (mensajes.size() < 100)
			mensajes.add(me);
		else
		{
			mensajes.remove(0);
			mensajes.add(me);
		}
		// Esto evita que vuelva a procesar de nuevo el ultimo mensaje que
		// recibio;
		if (lock)
		{
			lock = !lock;
			return;
		}

		print("Procesando mensaje " + me.messageId() + " = [" + me.text() + "]");
		// System.out.println("Chat ID " + me.chat().id() + " > " + me.text());
		String com = "";
		if(me.text() != null){
		String m = me.text().toLowerCase();
		int space = m.indexOf(" ");
		
		if (space != -1)
			com = m.substring(0, space);
		else
			com = m;
		}
		// System.out.println("com = " + com);
		if (me.replyToMessage() != null)
		{
			for (Plugin p : plugins)
			{
				if (p.waitForReply)
				{
					if (p.onReply(me))
						return;
				}
			}
		}
		for (Plugin p : plugins)
		{
			String name = p.getName();
			if (com.contains("/" + name))
			{
				print("Comando coincide con un plugin: " + name);
				if (p.needOwner && me.from().id() == this.owner)
				{
					try
					{
						p.onMessage(me);
					} catch (Exception e)
					{
						p.onError(e);
					}
					return;
				}
				if (p.needOwner && me.from().id() != this.owner)
				{
					String error = "No sos el dueño de este bot, tomatela :^)";
					bot.sendMessage(me.chat().id(), error);
					return;
				}
				try
				{
					p.onMessage(me);
				} catch (Exception e)
				{
					p.onError(e);
				}
				return;
			}
		}
		for (Plugin p : plugins)
		{
			if (p.listening)
			{
				if (p.onListen(me))
					return;
			}
		}
		if (me.leftChatParticipant() != null)
		{
			print("Alguien salio del chat " + me.chat().title());
			String uname = me.leftChatParticipant().username();
			if (uname != null&& uname.equals(username))
				System.out.println("Bot expulsado del chat id " + me.chat().id());
			
			for (Plugin p : plugins)
				if (p.leaveListen)
					if (p.onListen(me))
						return;
		}
		if (me.newChatParticipant() != null)
		{
			print("Nuevo user en el chat " + me.chat().title());
			if(me.newChatParticipant().username() == null)
				return;
			if (me.newChatParticipant().username().equals(username))
			{
				System.out.println("Bot agregado al chat id " + me.chat().id());
				for (Plugin p : plugins)
					if (p.onEnterListen)
					{
						if (p.onEnter(me))
							return;
					}
			}

		}

		/*
		 * if(m.startsWith("/echo")){ String result = m.text().substring(5);
		 * bot.sendMessage(m.chat().id(), result); }
		 */
	}

	private Config loadConfig()
	{
		// TODO Auto-generated method stub

		try
		{
			Config con;
			Gson gson = new Gson();
			BufferedReader br;
			br = new BufferedReader(new FileReader("sanguchi.txt"));
			con = gson.fromJson(br, Config.class);
			// System.out.println(con);
			br.close();
			return con;
		} catch (IOException e)
		{
			System.out.println("No se encontro archivo de configuracion.\n"
					+ "Usando configuracion por defecto.");
		}

		return new Config();
	}

	public void save()
	{
		if (config_file.exists())
		{
			FileWriter save = null;
			try
			{
				Config con;
				if (config == null)
					con = new Config();
				else
					con = config;
				con.owner = this.owner;
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String c = gson.toJson(con);
				// System.out.println("json = \n" + c);
				save = new FileWriter("sanguchi.txt");
				save.write(c);
				save.close();
				print("Configuracion guardada.");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			System.out
					.println("La configuracion del bot no existe, creando...");
			try
			{
				config_file.createNewFile();
				save();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("No se pudo guardar la configuracion");
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadLog()
	{
		if (log_file.exists())
		{
			try
			{
				Gson gson = new Gson();
				BufferedReader br;
				br = new BufferedReader(new FileReader(log_file));
				mensajes = gson.fromJson(br, mensajes.getClass());
				// System.out.println(con);
				br.close();

				print("log de mensajes cargado. " + mensajes.size()
						+ " mensajes en memoria.");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			System.out.println("No se ha encontrado log de mensajes.");
		}
	}

	public void saveLog()
	{
		if (log_file.exists())
		{
			FileWriter save = null;
			try
			{
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String c = gson.toJson(mensajes);
				// System.out.println("json = \n" + c);
				save = new FileWriter(log_file);
				save.write(c);
				save.close();
				print("log de mensajes guardado.");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			System.out
					.println("El archivo de log de mensajes no existe, creando...");
			try
			{
				log_file.createNewFile();
				saveLog();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				System.out.println("No se pudo guardar el archivo.");
				e.printStackTrace();
			}
		}
	}

	public String[] listPlugins()
	{
		String[] plugins_list = null;
		File test = new File(plugins_dir);
		if (test.exists())
		{
			plugins_list = new File(plugins_dir).list();
			// System.out.println(plugins_list.length);
			return plugins_list;
		} else
		{
			System.out
					.println("El directorio de plugins no existe, creando...");
			test.mkdir();
			plugins_list = new File(plugins_dir).list();
			return plugins_list;
		}
	}

	public void loadPlugins()
	{
		String a[] = listPlugins();
		if (a == null || a.length == 0)
		{
			System.out.println("No se han detectado plugins.");
			return;
		}
		print("a[] = " + a);
		System.out.println("Plugins encontrados: " + a.length);
		for (int i = 0; i < a.length; i++)
		{
			Plugin plug = null;
			try
			{
				String c = "plugins."
						+ a[i].substring(0, a[i].indexOf(".class"));
				// System.out.println(c);
				boolean isSubclass = c.contains("$");
				if (!isSubclass)
				{
					plug = (Plugin) ClassLoader.getSystemClassLoader()
							.loadClass(c).newInstance();
					plug.setBot(bot);
					if (plug.needSanguchi)
					{
						print("Plugin " + plug.getName()
								+ " requiere SanguchiBot.");
						plug.setSanguchi(this);
					}
					plugins.add(plug);
				}
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e1)
			{
				System.out.println("Class " + plug.getClass().getName()
						+ " No es un plugin");
			}
		}
	}

	private void print(String s)
	{
		if (debug)
			System.out.println(s);
	}
}
