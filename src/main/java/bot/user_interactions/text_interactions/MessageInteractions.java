package bot.user_interactions.text_interactions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import bot.embeds.ErrorAlert;
import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import db.dao.DAOServer;
import exceptions.NonexistentServerRegisterException;
import model.text_responses.Keyword;
import model.text_responses.Response;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MessageInteractions {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	DAOServer daoServer = new DAOServer();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		if(event.getMessage().getContentRaw().substring(0, 1).equals("!")) return;
		
		try {
			if(!daoServer.search(event.getGuild().getId()).isEnableKeywordResponses()) return;
		} catch (SQLException | NonexistentServerRegisterException e1) {
			event.getChannel().sendMessage(new ErrorAlert("NonexistentServerRegisterException | SQLException",
					"Remover e adicionar o LegendaryHelper do servidor pode ajudar a solucionar o problema. "
					+ "Estamos trabalhando nisso.").build()).queue();
			e1.printStackTrace();
			
			return;
		}
		
		ArrayList<String> wordSet = new ArrayList<String>();

		for(int i=0; i<event.getMessage().getContentRaw().split(" ").length; i++) {
			wordSet.add(event.getMessage().getContentRaw().split(" ")[i]);
		}
		
		try {
			ArrayList<Keyword> keywordSet = daoKeyword.searchKeywordSet(wordSet, event.getGuild().getId());
			if(keywordSet.size() == 0) return;
			Random random = new Random();
			
			ArrayList<Response> responseSet = daoResponse
					.searchResponseSet(keywordSet
					.get(random.nextInt(keywordSet.size())).getId());
			
			event.getChannel().sendMessage(responseSet
					.get(random.nextInt(responseSet.size())).getResponse())
					.queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
