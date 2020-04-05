package bot.user_interactions.minigames.tictactoe;

import java.util.ArrayList;
import java.util.Random;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import model.tictactoe.Session;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicTacToe {

	private ArrayList<Session> sessions = new ArrayList<Session>();

	public void onNewMessage(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;

		String possibleCommand = event.getMessage().getContentRaw().split(" ")[0];

		if (possibleCommand.equalsIgnoreCase("!tictactoe")) {
			
			// If message have the "stop" argument
			if (event.getMessage().getContentRaw().split(" ").length > 1) {
				if (event.getMessage().getContentRaw().split(" ")[1].equals("stop")) {
					Session session = searchForSession(event.getChannel().getId());
					if (session != null) {
						// Validating command
						if (event.getAuthor().getId().equals(session.getPlayerXId())
								|| event.getAuthor().getId().equals(session.getPlayerOId())
								|| event.getAuthor().getId().equals(event.getGuild().getOwnerId())) {
							removeSession(event.getChannel().getId());
							event.getChannel().sendMessage(new OkAlert("Sessão removida",
									"A sessão ativa desse jogo neste canal foi removida com sucesso.").build()).queue();
						} else {
							event.getChannel().sendMessage(new ErrorAlert("Não autorizado",
									"Apenas `" + session.getPlayerXName() + "`, `"
									+ session.getPlayerOName() + "` ou o dono do servidor pode "
									+ "encerrar a sessão atual deste jogo.").build()).queue();
						}
					} else {
						event.getChannel().sendMessage(new ErrorAlert("Nenhuma sessão deste jogo para ser fechada",
								"Sinta-se livre para iniciar uma nova!\nUse: `!tictactoe @aUser`").build()).queue();
					}
					
					return;
				}
			}
			// If there is no user tagged
			if (event.getMessage().getMentionedMembers().size() == 0) {
				event.getChannel().sendMessage(new ErrorAlert("Tentando jogar sozinho?",
						"Você deve mencionar (usando @) um usuário na mensagem.").build()).queue();

				return;
			}

			// If there is more than one user tagged
			if (event.getMessage().getMentionedMembers().size() > 1) {
				event.getChannel().sendMessage(
						new ErrorAlert("Muitos jogadores", "Este jogo permite apenas 2 jogadores (você é um deles).")
								.build())
						.queue();

				return;
			}

			// If the tagged user is the message author itself
			if (event.getMessage().getMentionedMembers().get(0).getId() .equals(event.getMessage().getAuthor().getId())) {
				event.getChannel().sendMessage(new ErrorAlert("Tentando jogar sozinho?",
						"Você não pode mencionar a si mesmo para iniciar um jogo.").build()).queue();

				return;
			}
			
			// If the tagged user is a bot
			if((event.getMessage().getMentionedMembers().get(0).getUser().isBot())) {
				event.getChannel().sendMessage(new ErrorAlert("Por enquanto... não",
						"Você não pode mencionar um bot para jogar.").build()).queue();

				return;
			}
			
			// Checking if there is a session, if not, create a new one
			Session session = searchForSession(event.getChannel().getId());
			if(session != null) {
				event.getChannel() .sendMessage(new ErrorAlert("Há um jogo em andamento",
						"Não será permitido um novo jogo neste canal até que `" + session.getPlayerXName()
							+ "` ou `" + session.getPlayerOName()
							+ "` use o comando `!tictactoe stop`. O dono do servidor também "
							+ "pode usar o comando para encerrar o jogo.").build()).queue();
			} else {
				// Creating session
				Random random = new Random();
				if (random.nextInt(2) == 0) {
					// Message's author is the X
					session = new Session(
							event.getChannel().getId(),
							event.getMessage().getAuthor().getId(),
							event.getMessage().getMentionedMembers().get(0).getId(),
							event.getMessage().getAuthor().getName(),
							event.getMessage().getMentionedMembers().get(0).getEffectiveName());
				} else {
					// Message's author is the O
					session = new Session(
							event.getChannel().getId(),
							event.getMessage().getMentionedMembers().get(0).getId(),
							event.getMessage().getAuthor().getId(),
							event.getMessage().getMentionedMembers().get(0).getEffectiveName(),
							event.getMessage().getAuthor().getName());
				}
				// Add session
				sessions.add(session);
				// Show the initial game's message
				event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
			}
			
			return;
			
		} else if ((possibleCommand.equals("!1")) || (possibleCommand.equals("!2")) || (possibleCommand.equals("!3"))
				|| (possibleCommand.equals("!4")) || (possibleCommand.equals("!5")) || (possibleCommand.equals("!6"))
				|| (possibleCommand.equals("!7")) || (possibleCommand.equals("!8")) || (possibleCommand.equals("!9"))) {

			// Checking if there is a session for this channel
			Session session = searchForSession(event.getChannel().getId());
			if (session != null) {
				switch (event.getMessage().getContentRaw().split(" ")[0]) {
				case "!1":
					performAction(event, session, 0, 0, 0);
					break;

				case "!2":
					performAction(event, session, 0, 1, 1);
					break;

				case "!3":
					performAction(event, session, 0, 2, 2);
					break;

				case "!4":
					performAction(event, session, 1, 0, 3);
					break;

				case "!5":
					performAction(event, session, 1, 1, 4);
					break;

				case "!6":
					performAction(event, session, 1, 2, 5);
					break;

				case "!7":
					performAction(event, session, 2, 0, 6);
					break;

				case "!8":
					performAction(event, session, 2, 1, 7);
					break;

				case "!9":
					performAction(event, session, 2, 2, 8);
					break;

				default:
					return;
				}

				return;
			} else {
				event.getChannel().sendMessage(new ErrorAlert("Nenhum jogo em andamento neste canal",
						"Comece um novo jogo com `!tictactoe @aUser`.").build()).queue();
			}
		}
	}

	public void performAction(GuildMessageReceivedEvent event, Session session, int fieldX, int fieldY,
			int visualField) {
		String authorId = event.getMessage().getAuthor().getId();
		boolean isXTurn = session.isXTurn();

		if ((authorId.equals(session.getPlayerXId())) && (isXTurn == true)) {
			boolean result = session.getBoard().putElement(fieldX, fieldY, 0);
			if (result) {
				if (session.getBoard().getAvailableFields() == 0) {
					session.getVisualBoard().updateBoard(visualField, 0, session.getPlayerXName(), true, true);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
					removeSession(event.getChannel().getId());
				} else if (session.getBoard().verifySolution()) {
					session.getVisualBoard().updateBoard(visualField, 0, session.getPlayerXName(), true, false);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
					removeSession(event.getChannel().getId());
				} else {
					session.getVisualBoard().updateBoard(visualField, 0, session.getPlayerXName(), false, false);
					session.setXTurn(false);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
				}
			} else {
				event.getChannel().sendMessage("Essa posição não está disponível, tenta de novo!").queue();
			}
		} else if ((authorId.equals(session.getPlayerOId())) && (isXTurn == false)) {
			boolean result = session.getBoard().putElement(fieldX, fieldY, 1);
			if (result) {
				if (session.getBoard().getAvailableFields() == 0) {
					session.getVisualBoard().updateBoard(visualField, 1, session.getPlayerOName(), true, true);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
					removeSession(event.getChannel().getId());
				} else if (session.getBoard().verifySolution()) {
					session.getVisualBoard().updateBoard(visualField, 1, session.getPlayerOName(), true, false);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
					removeSession(event.getChannel().getId());
				} else {
					session.getVisualBoard().updateBoard(visualField, 1, session.getPlayerOName(), false, false);
					session.setXTurn(true);
					event.getChannel().sendMessage(session.getVisualBoard().build()).queue();
				}
			} else {
				event.getChannel().sendMessage("Essa posição não está disponível, tenta de novo!").queue();
			}
		} else {
			// To prevent non players to receive this answer when try to perform a field
			// select command.
			if (event.getMessage().getAuthor().getId().equals(session.getPlayerXId())
					|| event.getMessage().getAuthor().getId().equals(session.getPlayerOId())) {
				if (isXTurn) {
					event.getChannel().sendMessage("Não é sua vez, é a vez do :x:!").queue();
				} else {
					event.getChannel().sendMessage("Não é sua vez, é a vez do :o:!").queue();
				}
			}
		}

		return;
	}

	private Session searchForSession(String currentChannelId) {
		for (Session session : sessions) {
			if (session.getChannelId().equals(currentChannelId)) {
				return session;
			}
		}
		
		return null;
	}

	private void removeSession(String currentChannelId) {
		for (int i = 0; i < sessions.size(); i++) {
			if (sessions.get(i).getChannelId().equals(currentChannelId)) {
				sessions.remove(i);
				return;
			}
		}
	}

}
