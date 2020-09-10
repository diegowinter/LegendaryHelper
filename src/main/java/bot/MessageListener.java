package bot;

import javax.swing.SwingUtilities;

import bot.user_interactions.minigames.tictactoe.TicTacToe;
import bot.user_interactions.poll.Poll;
import bot.user_interactions.settings.Disable;
import bot.user_interactions.settings.Enable;
import bot.user_interactions.settings.Overview;
import bot.user_interactions.spotify.Spotify;
import bot.user_interactions.text_interactions.MessageInteractions;
import bot.user_interactions.text_interactions.commands.Add;
import bot.user_interactions.text_interactions.commands.Delete;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	
	Add add = new Add();
	Delete delete = new Delete();
	TicTacToe tictactoe = new TicTacToe();
	MessageInteractions messageInteractions = new MessageInteractions();
	Disable disable = new Disable();
	Enable enable = new Enable();
	Overview overview = new Overview();
	Poll poll = new Poll();
	Spotify spotify = new Spotify();
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		switch (event.getMessage().getContentRaw().split(" ")[0].toLowerCase()) {
			/*Text interactions commands*/
			case "!add":
				add.onNewMessage(event);
				break;
			case "!delete":
				delete.onNewMessage(event);
				break;
			case "!disable":
				disable.onNewMessage(event);
				break;
			case "!enable":
				enable.onNewMessage(event);
				break;
			case "!overview":
				overview.onNewMessage(event);
				break;
			case "!poll":
				poll.onNewMessage(event);
				break;
			/*Tic-tac-toe minigame commands*/
			case "!tictactoe":
			case "!1":
			case "!2":
			case "!3":
			case "!4":
			case "!5":
			case "!6":
			case "!7":
			case "!8":
			case "!9":
				tictactoe.onNewMessage(event);
				break;
			case "!spotify":
				new Thread(new Runnable() {
					@Override
					public void run() {
						spotify.onNewMessage(event);
					}
				}).start();
			/*Text message without commands*/
			default:
				messageInteractions.onNewMessage(event);
				break;
			
		}
	}

}
