package bot;

import javax.security.auth.login.LoginException;

import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import message_listeners.MessageInteractions;
import message_listeners.commands.Add;
import message_listeners.commands.Delete;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	
	public static void main(String[] args) throws LoginException {
		/*	
		 * 	Invite link:
		 *  https://discordapp.com/oauth2/authorize?client_id=695072978978865153&scope=bot&permissions=158784
		 */
		new JDABuilder()
			.setToken(System.getenv("BOT_TOKEN"))
			/*.addEventListeners(new Main())*/
			.addEventListeners(new Add())
			.addEventListeners(new Delete())
			.addEventListeners(new MessageInteractions())
			.setActivity(Activity.listening("você!"))
			.build();
	}

}
