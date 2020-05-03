package bot;

import java.sql.SQLException;

import javax.security.auth.login.LoginException;

import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import db.dao.DAOServer;
import exceptions.ExistingServerRegisterException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	DAOServer daoServer = new DAOServer();
	
	public static void main(String[] args) throws LoginException {
		/*	
		 *  https://discordapp.com/oauth2/authorize?client_id=695072978978865153&scope=bot&permissions=158784
		 */
		new JDABuilder()
			.setToken(System.getenv("BOT_TOKEN"))
			.addEventListeners(new MessageListener())
			.addEventListeners(new Main())
			.build();
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		System.out.println("INFO: Joined " +  event.getGuild().getId());
		try {
			daoServer.add(event.getGuild().getId());
		} catch (SQLException | ExistingServerRegisterException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		System.out.println("INFO: Bye, " + event.getGuild().getId());
		try {
			daoServer.delete(event.getGuild().getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
