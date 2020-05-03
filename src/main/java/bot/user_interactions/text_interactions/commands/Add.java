package bot.user_interactions.text_interactions.commands;

import java.sql.SQLException;
import java.util.ArrayList;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import bot.embeds.TipAlert;
import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import exceptions.DuplicatedKeywordException;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Add {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		String idServerOwner = event.getGuild().getOwner().getUser().getId();
		String idMessageAuthor = event.getMessage().getAuthor().getId();
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!add")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!add")) {
				event.getChannel().sendMessage(new TipAlert("Adicionando novas palavras",
						"`!add %palavra_chave %uma resposta [...] %outra resposta`\n"
						+ "Você deve ser o dono do servidor. Use &u nas respostas quando quiser "
						+ "também marcar o autor da mensagem que escrever a palavra-chave.").build()).queue();
				
				return;
			} else {
				if(!idServerOwner.equals(idMessageAuthor)) {
					event.getChannel().sendMessage(new ErrorAlert("Não autorizado",
							"Você deve ser dono do servidor para usar este comando.").build()).queue();
					return;
				}
				
				ArrayList<String> entries = new ArrayList<String>();
				String commandContent = event.getMessage().getContentRaw().split("!add ")[1];
				
				if(commandContent.split("%").length > 2) {
					for(int i=1; i<commandContent.split("%").length; i++) {
						entries.add(commandContent.split("%")[i].trim());
					}
					
					if(entries.get(0).trim().split(" ").length > 1) {
						event.getChannel().sendMessage(new ErrorAlert("Muitas palavras-chave",
								"Uma palavra-chave deve ser composta de apenas uma palavra.").build()).queue();
						return;
					}
					
					if(entries.get(0).length() > 20) {
						event.getChannel().sendMessage(new ErrorAlert("Palavra-chave muito grande",
								"Uma palavra-chave deve ter até 20 caracteres.").build()).queue();
						return;
					}
					
					for(int i=1; i<entries.size(); i++) {
						if(entries.get(i).trim().length() > 500) {
							event.getChannel().sendMessage(new ErrorAlert("Resposta muito grande",
									"Uma resposta deve ter até 500 caracteres.").build()).queue();
							return;
						}
					}
					
					try {
						event.getMessage().addReaction("U+1F552").queue();
						int keywordId = daoKeyword.add(entries.get(0).toLowerCase(), event.getGuild().getId());
						String keyword = entries.get(0).toLowerCase();
						entries.remove(0);
						daoResponse.addResponseSet(keywordId, entries);
						String message = "Você adicionou: "
								+ "`"+ keyword +"`\nCom a(s) resposta(s): ";
						for (String entry : entries) {
							message = message + ("`" + entry + "` ");
						}
						event.getChannel().sendMessage(new OkAlert("Conhecimento adicionado",
								message).build()).queue();
					} catch (DuplicatedKeywordException e) {
						event.getChannel().sendMessage(new ErrorAlert("Palavra-chave duplicada",
								"A palavra-chave `"+ entries.get(0) + "` já existe na base de dados.")
								.build()).queue();
					} catch (SQLException e) {
						event.getChannel().sendMessage(new ErrorAlert("Erro interno",
								"Aconteceu um erro interno (SQLException).").build()).queue();
						e.printStackTrace();
					}
				} else {
					event.getChannel().sendMessage(new ErrorAlert("Faltam elementos",
							"Use o comando `!add` sozinho para ver mais detalhes.").build()).queue();
				}
				
				event.getMessage().removeReaction("U+1F552").queue();
			}
		}
	}

}
