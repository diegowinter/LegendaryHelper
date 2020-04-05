package bot.embeds;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;

public class TipAlert extends EmbedBuilder {
	
	public TipAlert(String summary, String message) {
		this.setTitle("Dica");
		this.addField(summary, message, false);
		this.setColor(Color.BLUE);
	}
	
}
