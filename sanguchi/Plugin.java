package sanguchi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

public class Plugin
{
	public TelegramBot bot;
	
	/**
	 * id del chat de donde proviene el mensaje.
	 */
	public long to;
	
	/**
	 * id del usuario que envio el mensaje.
	 */
	public long id;
	/**
	 * texto completo del mensaje, incluyendo el comando.
	 */
	public String text;
	/**
	 * Contenido del mensaje, excluyendo el comando.
	 */
	public String rest;
	/**
	 * Lista de palabras del mensaje.
	 */
	public List<String> args = new ArrayList<String>();
	
	/**
	 * Especifica si el plugin necesita acceso a la clase SanguchiBot.
	 */
	public boolean needSanguchi = false;
	
	
	/**
	 * Se usa para cuando un plugin desea recibir un mensaje aunque
	 *  el plugin no haya sido llamado, poner esto en true hace que el 
	 *  bot llame a la funcion onListen() del plugin.
	 */
	public boolean listening = false;
	
	/**
	 * Se usa para cuando un plugin desea recibir notificaciones 'left_chat_participant'.
	 */
	public boolean leaveListen = false;
	
	/**
	 * Se usa para cuando un plugin desea recibir notificaciones 'new_chat_participant'.
	 */
	public boolean onEnterListen = false;
	
	//ya me olvide si los iba a usar o no.
	public boolean waitForReply = false;
	public boolean avoidErrors = false;
	
	/**
	 * Se usa para crear plugins que solo el administrador del bot puede usar.
	 */
	public boolean needOwner = false;
	
	/**
	 * Instancia de SanguchiBot del plugin.
	 */
	public SanguchiBot sanguchi;

	public Plugin()
	{
	}
	
	/**
	 * Funcion llamada cada vez que un mensaje contiene un comando.</br>
	 * <b>super.onMessage(me);</b> configura las siguientes variables, para comodidad:</br>
	 * <b>to</b> id del chat de donde proviene el mensaje.</br>
	 * <b>id</b> id de la persona que envio el mensaje.</br>
	 * <b>text</b> texto completo del mensaje.</br>
	 * <b>args</b> texto del mensaje convertido en una lista de palabras.</br>
	 * <b>rest</b> texto del mensaje excepto el comando.
	 * 
	 * @param me Mensaje a procesar.
	 */
	public void onMessage(Message me)
	{
		to = me.chat().id();
		id = me.from().id();
		text = me.text();
		args = Arrays.asList(text.split(" "));
		args = new ArrayList<String>(args);
		rest = me.text().substring(getName().length());
	}
	/**
	 * Funcion llamada cuando el plugin tiene configurado el boolean onListen en true.
	 * @param me mensaje que recibe el bot.
	 * @return true en caso de que el mensaje no deba ser propagado a los otros plugins.
	 * En caso de que el mensaje no sea el que el plugin esperaba, puede enviarselo a los otros
	 * plugins devolviendo false.
	 */
	public boolean onListen(Message me)
	{
		return false;
	}
	/**
	 * Funcion llamada cuando alguien le responde al bot.
	 * @param me mensaje a procesar.
	 * @return false en caso de que el mensaje se deba propagar a los siguientes plugins.
	 * true si el mensaje ha sido procesado correctamente por el plugin correspondiente.
	 */
	public boolean onReply(Message me)
	{
		return false;
	}
	
	
	public void setBot(TelegramBot b)
	{
		bot = b;
	}

	public boolean onLeave(Message me)
	{
		print("onLeave Event for Plugin " + getName());
		return false;
	}
	public boolean onEnter(Message me)
	{
		print("onEnter Event for Plugin " + getName());
		return false;
	}
	public void setSanguchi(SanguchiBot b)
	{
		sanguchi = b;
	}
	public String help()
	{
		return "/" + getName() + "\nPlugin sin descripcion.";
	}

	/**
	 * Funcion llamada en caso de que un plugin genere una excepcion, esto evita que cualquier
	 * plugin que tenga un error no provoque que el bot se crashee.
	 * @param e Excepcion generada por el plugin, lanzada por el bot.
	 */
	public void onError(Exception e)
	{
		String error = "Error en plugin ["+getName()+"]";
		System.out.println(error);
		if(avoidErrors){return;}
		String mensaje = e.getMessage();
		String trace = "("+ e.getStackTrace()[0].getMethodName() +e.getStackTrace()[0].getLineNumber()+")";
		print("Mensaje: " + mensaje + "\nTrace: " + trace);
		try{bot.sendMessage(to, error+"\n"+mensaje);}
		catch(Exception ee){ee.printStackTrace();}
	}
	public void print(String m)
	{
		System.out.println("["+getName()+"]: "+ m);
	}
	public String getName()
	{
		String n = this.getClass().getName().toLowerCase();
		String name = n.substring(n.indexOf(".")+1);
		return name;
	}
}
