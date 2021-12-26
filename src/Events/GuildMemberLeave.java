package Events;

import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberLeave extends ListenerAdapter{
	
	String[] messages = {
		    "[member] left, the party's over",
		  };
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		Random rand = new Random();
		int number = rand.nextInt(messages.length);
		
		EmbedBuilder join = new EmbedBuilder();
		
		join.setColor(0x3c003d);
		join.setDescription(messages[number].replace("[member]", event.getUser().getAsMention()));
		event.getGuild().getDefaultChannel().sendMessageEmbeds(join.build()).queue();
	}
}
