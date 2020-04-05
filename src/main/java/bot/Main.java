package bot;

import javax.security.auth.login.LoginException;

import bot.user_interactions.minigames.tictactoe.TicTacToe;
import bot.user_interactions.text_interactions.MessageInteractions;
import bot.user_interactions.text_interactions.commands.Add;
import bot.user_interactions.text_interactions.commands.Delete;
import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	
	public static void main(String[] args) throws LoginException {
		/*	
		 *  https://discordapp.com/oauth2/authorize?client_id=695072978978865153&scope=bot&permissions=158784
		 */
		new JDABuilder()
			.setToken(System.getenv("BOT_TOKEN"))
			.addEventListeners(new MessageListener())
			.setActivity(Activity.listening("você!"))
			.build();
	}

}
