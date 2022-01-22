package PointsSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PointsSystem extends ListenerAdapter{
	
	HashMap<String, Integer> PlayerPoints = new HashMap<>();
	HashMap<String, Integer> PlayerTimer = new HashMap<>();
	
	Guild myGuild;
	
	TextChannel leaderboardChannel;
	
	int timerSeconds;
	
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
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "leaderboard") && BotStartup.isOwner(event.getMember().getId()))
		{
			CreatePlayerLeaderboard();
		}

		if (args[0].equalsIgnoreCase(BotStartup.prefix + "add") && BotStartup.isOwner(event.getMember().getId()))
		{
			if (event.getGuild().getMemberById(args[1]) != null && args[2].length() > 0)
			{
				AddPlayerPoints(args[1], Integer.parseInt(args[2]));
				
				event.getChannel().sendMessage("Points Added").queue();
			}
		}
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
		myGuild = BotStartup.myBot.getGuildById("911650356084240384");

		leaderboardChannel = myGuild.getTextChannelById("925826296590766100");
		
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
		
		// We are displaying the leaderboard for up to 5 members
		int displayedMembers = Math.min(5, highestScores.size());
		
		// Sort the list in highest > low order
		Collections.sort(highestScores);
		
		Collections.reverse(highestScores);
		
		DisplayMemberLeaderboard(displayedMembers, highestScores);
	}
	
	public void DisplayMemberLeaderboard(int displayedMembers, List<Integer> highestScores)
	{
		int alreadyDisplayed = 0;
		
		myGuild.loadMembers();
		
		// Create embed
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("Asgard's Hall of Fame");
		embed.setDescription("Here, the mightiest heroes are displayed.");
		
		myGuild.getTextChannelById("925826296590766100").sendMessageEmbeds(embed.build()).queue();
		
		// Get the keys for the top values in the array
		for (int n = 0; n < displayedMembers; n++)
		{
			for (Entry<String, Integer> memberEntry : PlayerPoints.entrySet()) 
			{
				// TODO: Change this
				/*if (memberKey.equalsIgnoreCase("911659154777718794")
						|| memberKey.equalsIgnoreCase("134073775925886976"))
				{
					continue;
				}*/
				
				if (memberEntry.getValue().equals(highestScores.get(n)))
				{
					EmbedBuilder memberRanking = new EmbedBuilder();
					
					String place = "1st";
					int ColourCode = 0XFFD700;
					
					switch (alreadyDisplayed) {
					case 0:
						place = "1st";
						ColourCode = 0XFFD700;
						break;
					case 1:
						place = "2nd";
						ColourCode = 0x9a9a9a;
						break;
					case 2:
						place = "3rd";
						ColourCode = 0xcd7f32;
						break;
					case 3:
						place = "4th";
						ColourCode = 0xffffff;
						break;
					case 4:
						place = "5th";
						ColourCode = 0xffffff;
						break;
					default:
						break;
					}
					
					memberRanking.setTitle(place + " " + myGuild.getMemberById(memberEntry.getKey()).getEffectiveName());
					
					memberRanking.setColor(ColourCode);
					
					memberRanking.setFooter(memberEntry.getValue() + " Points", myGuild.getMemberById(memberEntry.getKey()).getUser().getAvatarUrl());
					
					System.out.print(myGuild.getMemberById(memberEntry.getKey()).getUser().getAvatarUrl());
					
					leaderboardChannel.sendMessageEmbeds(memberRanking.build()).queue();
					
					alreadyDisplayed++;
				}
				
				if (alreadyDisplayed >= displayedMembers)
				{
					return;
				}
			}
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
	
	// Display the timer every X time
	public void StartTimer()
	{
		CreatePlayerLeaderboard();
		
		timerSeconds = 180;
		
		Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() 
			{
				timerSeconds -= 1;
				
				System.out.println(timerSeconds + " " + "\n");
				
				if (timerSeconds <= 0)
				{
					// Clear all previous messages
					leaderboardChannel.getHistory().retrievePast(10).queue(mesagesList ->
					{
						if (mesagesList.size() > 0)
						{
							for (Message message : mesagesList)
							{
								message.delete().queue();
							}
						}	
						
						CreatePlayerLeaderboard();
					});
					
					timerSeconds = 180;
				}
			}
		};
		
		timer.schedule(task, 1000, 1000);
	}
}
