package CreateChannel;

import java.util.EnumSet;
import java.util.List;

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
		
		net.dv8tion.jda.api.entities.Category gamesCategory = event.getGuild().getCategoriesByName("Games", true).get(0);
		
		List<TextChannel> categoryChannels = gamesCategory.getTextChannels();
		
		Boolean channelExists = false;
		
		for (int i = 0; i < categoryChannels.size(); i++)
		{
			if (categoryChannels.get(i).getName() == "Game 1 " + event.getMember().getEffectiveName())
			{
				channelExists = true;
				
				// Used for Debug purposes
				//event.getChannel().sendMessage("Channel Exists").queue();
				
				return;
			}
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "create") && !channelExists)
		{
			Guild guild = event.getMember().getGuild();
			
			String name = "Game1";
	
			gamesCategory.createTextChannel(name)
	         .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
	         .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
	         .complete(); // this actually sends the request to discord.
		    
			categoryChannels = gamesCategory.getTextChannels();
			
			for (int i = 0; i < categoryChannels.size(); i++)
			{
				if (categoryChannels.get(i).getName().equalsIgnoreCase("game1") && event.getMember().hasAccess(categoryChannels.get(i)))
				{
					categoryChannels.get(i).getManager().setTopic("Topic Test").complete();
					
					// Used for Debug purposes
					//event.getChannel().sendMessage("Topic Set").queue();
					
					break;
				}
			}
			
			// TODO: Check if channel exists by looking for name and permissions/access.
		    
		    // Used for Debug purposes
		    //event.getChannel().sendMessage("Channel Created").queue();
		}
	}
}
