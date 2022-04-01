package CreateChannel;

import java.util.EnumSet;
import java.util.List;

import GridBlocks.GameController;
import GridBlocks.GridBlocksGame;
import Main.BotStartup;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CreateChannel extends ListenerAdapter{
	
	// TODO: Create a channel when the user joins and send the first exercise and the help information
	// TODO: Move the creation of the channel to the game class
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ");
	}

}
