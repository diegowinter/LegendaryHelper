package bot.user_interactions.settings;

import java.sql.SQLException;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import bot.embeds.TipAlert;
import db.dao.DAOServer;
import exceptions.NonexistentServerRegisterException;
import model.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Enable {
	
	DAOServer daoServer = new DAOServer();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		String idServerOwner = event.getGuild().getOwner().getUser().getId();
		String idMessageAuthor = event.getMessage().getAuthor().getId();
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!enable")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!enable")) {
				event.getChannel().sendMessage(new TipAlert("Habilitando funcionalidades",
						"`!enable funcionalidade`\n"
						+ "Funcionalidades que podem ser habilitadas: `interaction`, `minigames`.").build()).queue();
				
				return;
			} else {
				if(!idServerOwner.equals(idMessageAuthor)) {
					event.getChannel().sendMessage(new ErrorAlert("Não autorizado",
							"Você deve ser dono do servidor para usar este comando.").build()).queue();
					
					return;
				}
				
				String commandContent = event.getMessage().getContentRaw().split("!enable ")[1];
				
				Server server = null;
				try {
					server = daoServer.search(event.getGuild().getId());
				} catch (SQLException | NonexistentServerRegisterException e) {
					event.getChannel().sendMessage(new ErrorAlert("NonexistentServerRegisterException | SQLException",
							"Remover e adicionar o LegendaryHelper do servidor pode ajudar a solucionar o problema. "
							+ "Estamos trabalhando nisso.").build()).queue();
					e.printStackTrace();
					
					return;
				}
				
				switch (commandContent) {
					case "interaction":
						if(server.isEnableKeywordResponses()) {
							event.getChannel().sendMessage(new ErrorAlert("Ação sem efeito",
									"A propriedade que você tentou habilitar já se encontra habilitada.").build()).queue();
							
							return;
						}
						try {
							daoServer.update(event.getGuild().getId(), "enable_kwd_resp", true);
							event.getChannel().sendMessage(new OkAlert("Propriedade alterada",
									"A propriedade `interaction` foi habilitada com sucesso.").build()).queue();
						} catch (SQLException e) {
							event.getChannel().sendMessage(new ErrorAlert("SQLException",
									"Estamos trabalhando nisso.").build()).queue();
							e.printStackTrace();
							
							return;
						}
						break;
					case "minigames":
						if(server.isEnableMinigames()) {
							event.getChannel().sendMessage(new ErrorAlert("Ação sem efeito",
									"A propriedade que você tentou habilitar já se encontra habilitada.").build()).queue();
							
							return;
						}
						try {
							daoServer.update(event.getGuild().getId(), "enable_minigames", true);
							event.getChannel().sendMessage(new OkAlert("Propriedade alterada",
									"A propriedade `minigames` foi habilitada com sucesso.").build()).queue();
						} catch (SQLException e) {
							event.getChannel().sendMessage(new ErrorAlert("SQLException",
									"Estamos trabalhando nisso.").build()).queue();
							e.printStackTrace();
							
							return;
						}
						break;
					default:
						event.getChannel().sendMessage(new ErrorAlert("Nada a habilitar",
								"Forneça algum parâmetro valido. Use apenas `!enable` para ajuda.").build()).queue();
						break;
				}
			}
		}
	}

}
