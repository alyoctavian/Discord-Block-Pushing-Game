package Events;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMessageReactionAdd extends ListenerAdapter{
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		if (event.getReactionEmote().getName().equals("❌") &&
			!event.getMember().getUser().equals(event.getJDA().getSelfUser()))
		{
			if (event.getMember().getUser().equals
					(event.getChannel().retrieveMessageById(event.getMessageId())
							.complete().getAuthor()))
			{
				// If it's the author
				event.getChannel().retrieveMessageById(event.getMessageId()).complete().delete().queue();
			}
			else
			{
				// If it's not the author
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
		else if (!event.getMember().getUser().equals(event.getJDA().getSelfUser()))
		{
			// Delete any extra reactions test -- Test works
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}
}
