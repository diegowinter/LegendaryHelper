package bot.embeds;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class OkAlert extends EmbedBuilder {
	
	public OkAlert(String summary, String message) {
		this.setTitle("Feito!");
		this.addField(summary, message, false);
		this.setColor(Color.GREEN);
	}
	
}
