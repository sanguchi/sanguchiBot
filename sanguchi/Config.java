package sanguchi;

import java.util.ArrayList;
import java.util.List;

public class Config
{
	int save_delay = 6;
	int last_update = 0;
	int limit = 5;
	int last_message_id = 0;
	String token = "token_here";
	int sleep_time = 10000;
	String su_pass = "YOUR PASS";
	String username = "";
	boolean debug = false;
	boolean save_log = false;
	long owner = 0;
	List<Long>blocked_users = new ArrayList<Long>();
}
