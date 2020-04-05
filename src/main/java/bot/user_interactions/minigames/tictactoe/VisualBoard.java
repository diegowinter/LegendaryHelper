package bot.user_interactions.minigames.tictactoe;

import java.awt.Color;
import java.util.ArrayList;

import net.dv8tion.jda.api.EmbedBuilder;

public class VisualBoard extends EmbedBuilder {
	
	private ArrayList<String> visualBoardFields = new ArrayList<String>();
	private String xPlayer;
	private String oPlayer;
	
	public VisualBoard(String xPlayer, String oPlayer) {
		this.xPlayer = xPlayer;
		this.oPlayer = oPlayer;
		
		visualBoardFields.add(":one:");
		visualBoardFields.add(":two:");
		visualBoardFields.add(":three:");
		visualBoardFields.add(":four:");
		visualBoardFields.add(":five:");
		visualBoardFields.add(":six:");
		visualBoardFields.add(":seven:");
		visualBoardFields.add(":eight:");
		visualBoardFields.add(":nine:");
		
		this.setColor(Color.YELLOW);
		this.setTitle("Um jogo da velha começou neste canal!");
		this.addField(":x: `" + xPlayer + "`\n:o: `" + oPlayer + "`", "-\n" +
				visualBoardFields.get(0) + visualBoardFields.get(1) + visualBoardFields.get(2) + "\n" +
				visualBoardFields.get(3) + visualBoardFields.get(4) + visualBoardFields.get(5) + "\n" +
				visualBoardFields.get(6) + visualBoardFields.get(7) + visualBoardFields.get(8) 
				+"\n-\nÉ a vez de: :x:\n-\n`![número]`: selecionar posição\n"
				+ "`!tictactoe stop`: finalizar jogo", false);
	}
	
	public void updateBoard(int field, int type, String currentPlayerName, boolean end, boolean draw) {
		this.clearFields();
		String currentTurn;
		
		if(type == 0) {
			visualBoardFields.set(field, ":x:");
			currentTurn = ":o:";
		} else {
			visualBoardFields.set(field, ":o:");
			currentTurn = ":x:";
		}
		
		if(end) {
			if(draw) {
				this.setTitle(":joy: Deu empate... :joy:");
				this.addField(":x: `" + xPlayer + "`\n:o: `" + oPlayer + "`",  "-\n" +
						visualBoardFields.get(0) + visualBoardFields.get(1) + visualBoardFields.get(2) + "\n" +
						visualBoardFields.get(3) + visualBoardFields.get(4) + visualBoardFields.get(5) + "\n" +
						visualBoardFields.get(6) + visualBoardFields.get(7) + visualBoardFields.get(8)
						+ "\n-\n`!tictactoe @aUser`: iniciar um novo jogo", false);
			} else {
				this.setTitle(":tada: Você ganhou, `" + currentPlayerName + "`!!! :tada:");
				this.addField(":x: `" + xPlayer + "`\n:o: `" + oPlayer + "`",  "-\n" +
						visualBoardFields.get(0) + visualBoardFields.get(1) + visualBoardFields.get(2) + "\n" +
						visualBoardFields.get(3) + visualBoardFields.get(4) + visualBoardFields.get(5) + "\n" +
						visualBoardFields.get(6) + visualBoardFields.get(7) + visualBoardFields.get(8)
						+ "\n-\n`!tictactoe @aUser`: iniciar um novo jogo", false);
			}
		} else {
			this.setTitle("Boa jogada, `" + currentPlayerName + "`!");
			this.addField(":x: `" + xPlayer + "`\n:o: `" + oPlayer + "`", "-\n" +
					visualBoardFields.get(0) + visualBoardFields.get(1) + visualBoardFields.get(2) + "\n" +
					visualBoardFields.get(3) + visualBoardFields.get(4) + visualBoardFields.get(5) + "\n" +
					visualBoardFields.get(6) + visualBoardFields.get(7) + visualBoardFields.get(8) 
					+"\n-\nÉ a vez de: "+currentTurn+"\n-\n`![número]`: selecionar posição\n"
					+ "`!tictactoe stop`: finalizar jogo", false);
		}
	}

}
