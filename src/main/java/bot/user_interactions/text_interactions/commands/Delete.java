package bot.user_interactions.text_interactions.commands;

import java.sql.SQLException;
import java.util.ArrayList;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import bot.embeds.TipAlert;
import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import exceptions.NonexistentKeywordException;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Delete {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		String idServerOwner = event.getGuild().getOwner().getUser().getId();
		String idMessageAuthor = event.getMessage().getAuthor().getId();
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!delete")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!delete")) {
				event.getChannel().sendMessage(new TipAlert("Deletando conhecimento",
						"`!delete %palavra_chave [...] %palavra_chave`\n"
						+ "Todas as respostas relacionadas à esta(s) palavra(s)-chave serão deletadas, "
						+ "então sinta-se livre caso precise adicioná-las novamente.").build()).queue();
				
				return;
			} else {
				if(!idServerOwner.equals(idMessageAuthor)) {
					event.getChannel().sendMessage(new ErrorAlert("Não autorizado",
							"Você deve ser dono do servidor para usar este comando.").build()).queue();
					return;
				}
				
				ArrayList<String> entries = new ArrayList<String>();
				String commandContent = event.getMessage().getContentRaw().split("!delete ")[1];
				
				if(commandContent.split("%").length > 1) {
					for(int i=1; i<commandContent.split("%").length; i++) {
						entries.add(commandContent.split("%")[i].trim());
					}
					
					for (String entry : entries) {
						if(entry.length() > 20) {
							event.getChannel().sendMessage(new ErrorAlert("Palavra-chave muito grande",
									"Uma palavra-chave tem até 20 caracteres. Você digitou "
									+ "corretamente?").build()).queue();
							return;
						}
					}
					
					event.getMessage().addReaction("U+1F552").queue();
					
					ArrayList<String> success = new ArrayList<String>();
					ArrayList<String> fail = new ArrayList<String>();
					for (String entry : entries) {
						try {
							int id = daoKeyword.search(entry, event.getGuild().getId()).getId();
							daoResponse.delete(id);
							daoKeyword.delete(entry, event.getGuild().getId());
							success.add(entry);
						} catch (NonexistentKeywordException e) {
							fail.add(entry);
						} catch (SQLException e) {
							event.getChannel().sendMessage(new ErrorAlert("Erro interno",
									"Aconteceu um erro interno (SQLException).").build()).queue();
							e.printStackTrace();
						}
					}
					
					if(success.size() > 0) {
						String message;
						if(success.size() == 1) {
							message = "A seguinte palavra-chave foi deletada: ";
						} else {
							message = "A seguintes palavras-chave foram deletadas: ";
						}
						
						for (String keyword : success) {
							message = message + "`" + keyword + "` ";
						}
						
						event.getChannel().sendMessage(new OkAlert("Conhecimento deletado",
								message).build()).queue();
					}
					
					if(fail.size() > 0) {
						String summary;
						String message;
						if(fail.size() == 1) {
							summary = "Palavra-chave inexistente";
							message = "A seguinte palavra-chave não existia na base de dados: ";
						} else {
							summary = "Palavras-chave inexistentes";
							message = "A seguintes palavras-chave não existiam na base de dados: ";
						}
						
						for (String keyword : fail) {
							message = message + "`" + keyword + "` ";
						}
						
						event.getChannel().sendMessage(new ErrorAlert(summary, message).build()).queue();
					}
					
				} else {
					event.getChannel().sendMessage(new ErrorAlert("Faltam elementos",
							"Use o comando `!delete` sozinho para ver mais detalhes.").build()).queue();
				}
				
				event.getMessage().removeReaction("U+1F552").queue();
			}
		}
	}

}
