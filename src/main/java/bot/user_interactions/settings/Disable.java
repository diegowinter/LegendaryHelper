package bot.user_interactions.settings;

import java.sql.SQLException;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import bot.embeds.TipAlert;
import db.dao.DAOServer;
import exceptions.NonexistentServerRegisterException;
import model.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Disable {
	
	DAOServer daoServer = new DAOServer();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		String idServerOwner = event.getGuild().getOwner().getUser().getId();
		String idMessageAuthor = event.getMessage().getAuthor().getId();
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!disable")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!disable")) {
				event.getChannel().sendMessage(new TipAlert("Desabilitando funcionalidades",
						"`!disable funcionalidade`\n"
						+ "Funcionalidades que podem ser desabilitadas: `interaction`, `minigames`.").build()).queue();
				
				return;
			} else {
				if(!idServerOwner.equals(idMessageAuthor)) {
					event.getChannel().sendMessage(new ErrorAlert("Não autorizado",
							"Você deve ser dono do servidor para usar este comando.").build()).queue();
					
					return;
				}
				
				String commandContent = event.getMessage().getContentRaw().split("!disable ")[1];
				
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
						if(!server.isEnableKeywordResponses()) {
							event.getChannel().sendMessage(new ErrorAlert("Ação sem efeito",
									"A propriedade que você tentou desabilitar já se encontra desabilitada.").build()).queue();
							
							return;
						}
						try {
							daoServer.update(event.getGuild().getId(), "enable_kwd_resp", false);
							event.getChannel().sendMessage(new OkAlert("Propriedade alterada",
									"A propriedade `interaction` foi desabilitada com sucesso.").build()).queue();
						} catch (SQLException e) {
							event.getChannel().sendMessage(new ErrorAlert("SQLException",
									"Estamos trabalhando nisso.").build()).queue();
							e.printStackTrace();
							
							return;
						}
						break;
					case "minigames":
						if(!server.isEnableMinigames()) {
							event.getChannel().sendMessage(new ErrorAlert("Ação sem efeito",
									"A propriedade que você tentou desabilitar já se encontra desabilitada.").build()).queue();
							
							return;
						}
						try {
							daoServer.update(event.getGuild().getId(), "enable_minigames", false);
							event.getChannel().sendMessage(new OkAlert("Propriedade alterada",
									"A propriedade `minigames` foi desabilitada com sucesso.").build()).queue();
						} catch (SQLException e) {
							event.getChannel().sendMessage(new ErrorAlert("SQLException",
									"Estamos trabalhando nisso.").build()).queue();
							e.printStackTrace();
							
							return;
						}
						break;
					default:
						event.getChannel().sendMessage(new ErrorAlert("Nada a desabilitar",
								"Forneça algum parâmetro valido. Use apenas `!disable` para ajuda.").build()).queue();
						break;
				}
			}
		}
	}

}
