package Main;

import javax.security.auth.login.LoginException;

import Commands.Clear;
import Commands.Embed;
import CreateChannel.CreateChannel;
import Events.GuildMemberJoin;
import Events.GuildMemberLeave;
import Events.GuildMessageReactionAdd;
import Events.GuildMessageReceived;
import GridBlocks.GridBlocksGame;
import GuildGameSystem.GuildGameSystem;
import PointsSystem.PointsSystem;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotStartup {
	public static String prefix = "!";
	
	public static PointsSystem myPointsSystem;
	
	public static GuildGameSystem myGuildGameSystem;
	
	public static JDA myBot;
	
	public static TextChannel selectionChannel;
	
	public static void main(String[] args) throws LoginException, InterruptedException
	{
		JDABuilder jda = JDABuilder.createDefault("OTExNjU5MTU0Nzc3NzE4Nzk0.YZkmwA.JNo4SDUgEGBbc4L8NlSQWmMtjIA").enableIntents(GatewayIntent.GUILD_MEMBERS);
		jda.setActivity(Activity.playing("!Game"));
		jda.setStatus(OnlineStatus.ONLINE);
		
		jda.addEventListeners(new Embed());
		jda.addEventListeners(new Clear());
		jda.addEventListeners(new GuildMemberJoin());
		jda.addEventListeners(new GuildMemberLeave());
		jda.addEventListeners(new GuildMessageReceived());
		
		myPointsSystem = new PointsSystem();
		
		myGuildGameSystem = new GuildGameSystem();
		
		jda.addEventListeners(myPointsSystem);
		jda.addEventListeners(new CreateChannel());
		
		jda.addEventListeners(new GridBlocksGame());
		
		jda.addEventListeners(myGuildGameSystem);
		
		myBot = jda.build();
		
		myBot.awaitReady();
		
		myGuildGameSystem.SetupSelectionChannel();
	}
}
