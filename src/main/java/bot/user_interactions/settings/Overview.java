package bot.user_interactions.settings;

import java.awt.Color;
import java.sql.SQLException;

import bot.embeds.ErrorAlert;
import db.dao.DAOServer;
import exceptions.NonexistentServerRegisterException;
import model.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Overview {
	
	DAOServer daoServer = new DAOServer();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!overview")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!overview")) {
				EmbedBuilder embed = new EmbedBuilder();
				
				Server server = null;
				try {
					server = daoServer.search(event.getGuild().getId());
				} catch (SQLException | NonexistentServerRegisterException e) {
					e.printStackTrace();
					return;
				}
				embed.setColor(Color.GRAY);
				embed.setTitle("Resumo neste servidor");
				if(server.isEnableKeywordResponses()) {
					embed.addField("Interação", "Ativada", true);
				} else {
					embed.addField("Interação", "Desativada", true);
				}
				
				if(server.isEnableMinigames()) {
					embed.addField("Minigames", "Ativados", true);
				} else {
					embed.addField("Minigames", "Desativados", true);
				}
				
				event.getChannel().sendMessage(embed.build()).queue();
				
			} else {
				event.getChannel().sendMessage(new ErrorAlert("Uso incorreto do comando",
						"Use apenas `!overview` neste comando.").build()).queue();
			}
		}
	}

}
