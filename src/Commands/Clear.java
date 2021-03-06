package Commands;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Clear extends ListenerAdapter{
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase("!clear")) 
        {
            if (args.length < 2) 
            {
                EmbedBuilder usage = new EmbedBuilder();
                usage.setColor(0xff3923);
                usage.setTitle("🔴 Specify amount to delete");
                usage.setDescription("Usage: !clear [# of messages]");
                event.getChannel().sendMessageEmbeds(usage.build()).queue();
            } 
            else 
            {
                try
                {
                	List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1])).complete();
                    event.getChannel().deleteMessages(messages).queue();
                    event.getChannel().sendMessage("Messages have been deleted!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                }
                catch (IllegalArgumentException errorException)
                {
                	if (errorException.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval"))
                	{
                		// Too many messages
                		EmbedBuilder error = new EmbedBuilder();
                		
                		error.setTitle("🔴 Invalid number of messages selected!");
                		error.setDescription("Between 1 - 100 messages can be deleted.");
                		error.setFooter("Bot Created by Octavian", event.getGuild().getOwner().getUser().getAvatarUrl());
            			
                		error.setColor(Color.RED);
            			
            			event.getChannel().sendMessageEmbeds(error.build()).queue();
            			
            			
            			error.clear();
                	}
                	else
                	{
                		// Messages too old
                		EmbedBuilder error = new EmbedBuilder();
                		
                		error.setTitle("🔴 Selected messages too old.");
                		error.setDescription("Messages older than two weeks cannot be deleted.");
                		error.setFooter("Bot Created by Octavian", event.getGuild().getOwner().getUser().getAvatarUrl());
            			
                		error.setColor(Color.RED);
            			
            			event.getChannel().sendMessageEmbeds(error.build()).queue();
            			
            			
            			error.clear();
                	}
                }
            }
        }
	}
}
