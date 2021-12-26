package Commands;

import java.awt.Color;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Embed extends ListenerAdapter{
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ");
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "test"))
		{
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Embed Title");
			embed.setDescription("This is the embed description");
			embed.addField("Embed Field 1", "This is the content of field 1", false);
			embed.addField("Embed Field 2", "This is the content of field 2", false);
			embed.setFooter("Bot Created by Octavian", event.getGuild().getOwner().getUser().getAvatarUrl());
			
			embed.setColor(Color.GREEN);
			
			event.getChannel().sendMessageEmbeds(embed.build()).queue();
			
			
			embed.clear();
		}
		
		if (event.getMessage().getContentRaw().equalsIgnoreCase("doamne da-mi rabdare"))
		{
			event.getChannel().sendMessage("Ca daca-mi dai putere sparg tot aicea").queue();
		}
	}
}
