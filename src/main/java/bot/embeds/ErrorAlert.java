package bot.embeds;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class ErrorAlert extends EmbedBuilder {
	
	public ErrorAlert(String summary, String message) {
		this.setTitle("Algo deu errado");
		this.addField(summary, message, false);
		this.setColor(Color.RED);
	}

}
