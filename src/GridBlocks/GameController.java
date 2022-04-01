package GridBlocks;

import java.util.HashMap;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GameController extends ListenerAdapter {
	
	HashMap<String, GridBlocksGame> PlayerGameObject = new HashMap<>();
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		GetGameEntry(event.getMember().getId()).GameMemberJoin(event);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		GetGameEntry(event.getMember().getId()).GameMessageReceived(event);
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		GetGameEntry(event.getMember().getId()).GameReactionAdd(event);
	}
	
	public GridBlocksGame GetGameEntry(String memberID)
	{
		if (!PlayerGameObject.containsKey(memberID))
		{
			CreateGameEntry(memberID);
		}
		
		return PlayerGameObject.get(memberID);
	}
	
	public void CreateGameEntry(String memberID)
	{
		if (!PlayerGameObject.containsKey(memberID))
		{
			PlayerGameObject.put(memberID, new GridBlocksGame());
		}
	}
}
