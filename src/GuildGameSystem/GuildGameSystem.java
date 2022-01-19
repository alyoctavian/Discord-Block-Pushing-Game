package GuildGameSystem;

import java.awt.Color;
import java.util.List;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildGameSystem extends ListenerAdapter{

TextChannel selectionChannel;
Message		selectGuildMessage;
Boolean retrievedMessages;
Boolean selecionChannelProcessed;
	
	public void SetupSelectionChannel()
	{
		// We need to find or setup the selection message
		retrievedMessages = false;
		
		selectionChannel = BotStartup.myBot.getTextChannelsByName("guild-selection", true).get(0);
		
		// Get the first, and only message in the channel
		selectionChannel.getHistory().retrievePast(1).queue(mesagesList ->
		{
			if (mesagesList.size() > 0)
			{
				selectGuildMessage = mesagesList.get(0);
			}
			
			retrievedMessages = true;
		});
		
		while (!retrievedMessages)
		{
			try
			{
			    Thread.sleep(500);
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}
		}
		
		// If there's no message, create one
		if (selectGuildMessage == null)
		{
			EmbedBuilder embed = new EmbedBuilder();
			
			embed.setColor(Color.GREEN);
			
			embed.setTitle("Hello there, mighty hero.\nDon't go alone, join a guild. "
					+ "You can help each other reach the top of the mountain.");
			
			if (selectionChannel.getGuild().getOwner() != null)
			{
				embed.setFooter("Game Created by Octavian", selectionChannel.getGuild().getOwner().getUser().getAvatarUrl());
				
				embed.setDescription("🐺 Guild of the Wolf\n"
						+ "🐻 Guild of the Bear\n"
						+ "😼 Guild of the Cat\n");
				
				selectionChannel.sendMessageEmbeds(embed.build()).queue(message -> {
					selectGuildMessage = message;
					
					message.addReaction("🐺").queue();
					message.addReaction("🐻").queue();
					message.addReaction("😼").queue();
				});
			}
			else 
			{
				selectionChannel.getGuild().retrieveMemberById("303140676458250240").queue(member -> 
				{
					embed.setFooter("Game Created by Octavian", member.getUser().getAvatarUrl());
					
					embed.setDescription("🐺 Guild of the Wolf\n"
							+ "🐻 Guild of the Bear\n"
							+ "😼 Guild of the Cat\n");
					
					selectionChannel.sendMessageEmbeds(embed.build()).queue(message -> {
						selectGuildMessage = message;
						
						message.addReaction("🐺").queue();
						message.addReaction("🐻").queue();
						message.addReaction("😼").queue();
					});
			    });
			}
		}
		
		// We now finished processing the message
		selecionChannelProcessed = true;
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		selecionChannelProcessed = false;
		
		// Setup the channel and retrieve the message
		SetupSelectionChannel();
		
		while (!selecionChannelProcessed)
		{
			try
			{
			    Thread.sleep(500);
			}
			catch(InterruptedException ex)
			{
			    Thread.currentThread().interrupt();
			}
		}
		
		if (!event.getMessageId().equalsIgnoreCase(selectGuildMessage.getId()) ||
				event.getMember().getUser().equals(event.getJDA().getSelfUser()))
		{
			return;
		}
		
		if (isMemberInGuild(event.getMember()))
		{
			return;
		}
		
		List<MessageReaction> reactionsList = selectGuildMessage.getReactions();
		
		int user_reactions = 0;
		
		for (int i = 0; i < reactionsList.size(); i++)
		{
			List<User> users = reactionsList.get(i).retrieveUsers().complete();
			
			for (int n = 0; n < users.size(); n++)
			{
				if (users.get(n).getId().equals(event.getUser().getId()))
				{
					user_reactions ++;
				}
			}
		}
		
		if (user_reactions > 1)
		{
			event.getReaction().removeReaction(event.getUser()).complete();
			
			return;
		}
		
		if (event.getReactionEmote().getName().equalsIgnoreCase("🐺"))
		{
			// Add role
			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("925827156951593010")).complete();
		}
		else if (event.getReactionEmote().getName().equalsIgnoreCase("🐻"))
		{
			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("931915568917938236")).complete();
		}
		else if (event.getReactionEmote().getName().equalsIgnoreCase("😼"))
		{
			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("931915643526205510")).complete();
		}
	}
	
	public Boolean isMemberInGuild(Member member)
	{
		List<Role> roles = member.getRoles();
		
		for (Role role : roles) 
		{
			if (role.getId().equalsIgnoreCase("925827156951593010")
				|| role.getId().equalsIgnoreCase("931915568917938236")
				|| role.getId().equalsIgnoreCase("931915643526205510"))
			{
				return true;
			}
		}
		
		return false;
	}
}
