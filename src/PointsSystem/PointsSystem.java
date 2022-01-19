package PointsSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import Main.BotStartup;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.concurrent.Task;

public class PointsSystem extends ListenerAdapter{
	
	HashMap<String, Integer> PlayerPoints = new HashMap<>();
	HashMap<String, Integer> PlayerTimer = new HashMap<>();
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		// Checks if the message is not from the bot
		
		if (event.getMember().getUser().equals(event.getJDA().getSelfUser())) 
		{
			return;
		}
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "points"))
		{
			event.getChannel().sendMessage("You have " + GetPlayerPoints(event.getMember().getId()) + " points").queue();
		}
		// Comment this out. We don't need to add points on each message
		/*else if (CanGetPoints(event.getMember().getId()))
		{
			RandPoints(event.getMember().getId());
			
			SetPlayerTimer(event.getMember().getId(), 100);
			
			StartTimer(event.getMember().getId());
		}*/
	}
	
	private int GetPlayerPoints(String memberID)
	{
		if (!PlayerPoints.containsKey(memberID))
		{
			SetPlayerPoints(memberID, 0);
		}
		
		return PlayerPoints.get(memberID);
	}
	
	public void SetPlayerPoints(String memberID, int points)
	{
		PlayerPoints.put(memberID, points);
	}
	
	private int GetPlayerTimer(String memberID)
	{
		return PlayerTimer.get(memberID);
	}
	
	private void SetPlayerTimer(String memberID, int new_time)
	{
		PlayerTimer.put(memberID, new_time);
	}
	
	public void AddPlayerPoints(String memberID, int points)
	{
		if (!PlayerPoints.containsKey(memberID))
		{
			SetPlayerPoints(memberID, 0);
		}
		
		SetPlayerPoints(memberID, GetPlayerPoints(memberID) + points);
	}
	
	public void RandPoints(String memberID)
	{
		Random numRandom = new Random();
		
		if (!PlayerPoints.containsKey(memberID))
		{
			SetPlayerPoints(memberID, 0);
		}
		
		int points = numRandom.nextInt(26);
		
		SetPlayerPoints(memberID, GetPlayerPoints(memberID) + points);
	}
	
	public void CreatePlayerLeaderboard()
	{
		Guild myGuild = BotStartup.myBot.getGuildById("911650356084240384");

		
		List<Member> myMembers = myGuild.getMembers();
		
		for (Member member : myMembers) 
		{
			if (member.getId().equalsIgnoreCase("911659154777718794")
					|| member.getId().equalsIgnoreCase("134073775925886976"))
			{
				continue;
			}
			
			if (!PlayerPoints.containsKey(member.getId()))
			{
				SetPlayerPoints(member.getId(), 0);
			}
			
			//System.out.print(member.getEffectiveName() + " " + GetPlayerPoints(member.getId()) + '\n');
		}
		
		// We are displaying the leaderboard for up to 5 members
		int displayedMembers = Math.max(5, myMembers.size());
		
		List<Integer> highestScores = new ArrayList<>();
		
		Set<String> playersSet = PlayerPoints.keySet();
		
		for (String memberKey : playersSet) 
		{
			if (memberKey.equalsIgnoreCase("911659154777718794")
					|| memberKey.equalsIgnoreCase("134073775925886976"))
			{
				continue;
			}
			
			highestScores.add(GetPlayerPoints(memberKey));
		}
	}
	
	private boolean CanGetPoints(String memberID)
	{
		if (!PlayerTimer.containsKey(memberID))
		{
			SetPlayerTimer(memberID, 0);
			
			return true;
		}
		else if (PlayerTimer.containsKey(memberID))
		{
			if (GetPlayerTimer(memberID) <= 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void StartTimer(String memberID)
	{
		Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() 
			{
				SetPlayerTimer(memberID, GetPlayerTimer(memberID) - 1);
				
				if (GetPlayerTimer(memberID) <= 0)
				{
					timer.cancel();
				}
			}
		};
		
		timer.schedule(task, 1000, 1000);
	}
}
