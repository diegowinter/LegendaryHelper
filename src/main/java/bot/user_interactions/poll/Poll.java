package bot.user_interactions.poll;

import java.util.ArrayList;

import bot.embeds.ErrorAlert;
import bot.embeds.TipAlert;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Poll {
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!poll")) {
			if(event.getMessage().getContentRaw().equalsIgnoreCase("!poll")) {
				event.getChannel().sendMessage(new TipAlert("Criando votações no servidor",
						"`!poll %um título %alternativa 1 [...] %alternativa 4`\n"
						+ "Você deve incluir de 2 a 4 alternativas para a votação. Os votos "
						+ "são feito através de reações adicionadas pelo LegendaryHelper").build()).queue();
				
				return;
			} else {
				ArrayList<String> entries = new ArrayList<String>();
				String commandContent = event.getMessage().getContentRaw().split("!poll")[1];
				
				if(commandContent.split("%").length > 3) {
					for(int i=1; i<commandContent.split("%").length; i++) {
						entries.add(commandContent.split("%")[i].trim());
					}
					
					if(entries.size() > 5) {
						event.getChannel().sendMessage(new ErrorAlert("Muitas alternativas",
								"Use o comando `!poll` sozinho para ver mais detalhes.").build()).queue();
						
						return;
					}
					
					if(entries.get(0).length() > 50) {
						entries.set(0, entries.get(0).substring(0, 50) + "...");
					}
					
					for(int i=1; i<entries.size(); i++) {
						if(entries.get(i).trim().length() > 25) {
							entries.set(i, entries.get(i).substring(0, 25) + "...");
						}
					}
					
					String pollText = entries.get(0);
					for(int i=1; i<entries.size(); i++) {
						pollText = pollText + "\n" + i + ") " + entries.get(i);
					}
					
					EmbedBuilder embed = new EmbedBuilder();
					embed.addField("Vote usando as reações abaixo", pollText, false);
					embed.setAuthor(event.getAuthor().getName() + " criou uma votação",
							"https://legendaryhelper.diegowinter.dev",
							event.getAuthor().getAvatarUrl());
					
					event.getChannel().sendMessage(embed.build()).queue(message -> {
						switch(entries.size()-1) {
							case 2: 
								message.addReaction("1️⃣").queue();
								message.addReaction("2️⃣").queue();
							break;
							
							case 3:
								message.addReaction("1️⃣").queue();
								message.addReaction("2️⃣").queue();
								message.addReaction("3️⃣").queue();
							break;
							
							case 4:
								message.addReaction("1️⃣").queue();
								message.addReaction("2️⃣").queue();
								message.addReaction("3️⃣").queue();
								message.addReaction("4️⃣").queue();
							break;
						}
					});
				} else {
					event.getChannel().sendMessage(new ErrorAlert("Faltam elementos",
							"Use o comando `!poll` sozinho para ver mais detalhes.").build()).queue();
				}
			}
		}
	}

}
