package bot.user_interactions.text_interactions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import db.dao.DAOKeyword;
import db.dao.DAOResponse;
import model.text_responses.Keyword;
import model.text_responses.Response;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageInteractions {
	
	DAOKeyword daoKeyword = new DAOKeyword();
	DAOResponse daoResponse = new DAOResponse();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		if(event.getMessage().getContentRaw().substring(0, 1).equals("!")) return;
		
		ArrayList<String> wordSet = new ArrayList<String>();

		for(int i=0; i<event.getMessage().getContentRaw().split(" ").length; i++) {
			wordSet.add(event.getMessage().getContentRaw().split(" ")[i]);
		}
		
		try {
			ArrayList<Keyword> keywordSet = daoKeyword.searchKeywordSet(wordSet);
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
