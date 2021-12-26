package GridBlocks;

import java.awt.Color;
import java.util.Random;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GridBlocksGame extends ListenerAdapter{

	String block = "🔳";
	String giant = "👹";
	
	// Reaction strings
	String ReactViking1 = "viking1:922507879402061895";
	String ReactViking2 = "viking2:922514842638774352";
	String ReactViking3 = "viking3:922515468303101952";
	String ReactViking4 = "viking4:922515511517007882";
	
	// Hero icons
	String Viking1 = "<:viking1:922507879402061895>";
	String Viking2 = "<:viking2:922514842638774352>";
	String Viking3 = "<:viking3:922515468303101952>";
	String Viking4 = "<:viking4:922515511517007882>";
	
	// Arrow emotes
	String ArrowLeft= "⬅️";
	String ArrowRight = "➡️";
	String ArrowDown = "⬇️";
	String ArrowUp = "⬆️";
	String RefreshArrows = "🔄";
	
	// Lock emote
	String Lock = "🔐";
	
	// Select character message
	Message SelectHeroMsg = null;
	
	GridViking Hero;
	
	// Grid variables
	String[][] Grid;
	String GridString;
	
	int GridSize;
	
	// Direction enum
	enum direction {
		LEFT,
		RIGHT,
		DOWN,
		UP
	}
	
	public GridBlocksGame() 
	{
		Hero = new GridViking();
	}
	
	public void CreateGrid()
	{
		GridSize = 5;
		
		Grid = new String[GridSize][GridSize];
		
		// Populate the grid with empty blocks
		for(int i = 0; i < GridSize; i++)
		{
			for(int j = 0; j < GridSize; j++)
			{
				Grid[i][j] = block;
			}
		}
		
		// Add the Viking hero
		AddVikingHero();
		
		// Add the Lock
		AddLock();
		
		// Add the Giant monster
		AddGiantMonster();
		
		// Debug purposes
		// Add a number
		AddNumber();
		
		CreateGridString();
	}
	
	public void AddVikingHero()
	{
		Hero.pos.x = 0;
		Hero.pos.y = 0;
		
		Grid[Hero.pos.x][Hero.pos.y] = Hero.HeroIcon;
	}
	
	public void AddGiantMonster()
	{
		Random numRandom = new Random();
		
		int i = numRandom.nextInt(GridSize);
		int j = numRandom.nextInt(GridSize);
		
		if (Grid[i][j].equals(block))
		{
			Grid[i][j] = giant;
		}
		else 
		{
			AddGiantMonster();
		}
	}
	
	public void AddLock()
	{
		Random numRandom = new Random();
		
		int i = numRandom.nextInt(GridSize);
		int j = numRandom.nextInt(GridSize);
		
		if (Grid[i][j].equals(block))
		{
			Grid[i][j] = Lock;
		}
		else 
		{
			AddLock();
		}
	}
	
	public void AddNumber()
	{
		Random numRandom = new Random();
		
		int i = numRandom.nextInt(GridSize);
		int j = numRandom.nextInt(GridSize);
		
		if (Grid[i][j].equals(block))
		{
			Random randomVal = new Random();
			
			int val = randomVal.nextInt(10);
			
			GridNumberBlock num = new GridNumberBlock(new GridPosition(i, j), val);
			
			Grid[i][j] = num.num_icon;
		}
		else 
		{
			AddNumber();
		}
	}
	
	public void CreateGridString()
	{
		GridString = "";
		
		for(int i = 0; i < GridSize; i++)
		{
			for(int j = 0; j < GridSize; j++)
			{
				GridString = GridString + Grid[i][j];
			}
			
			GridString += "\n";
		}
	}
		
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ");
		
		if (!event.getChannel().getName().equalsIgnoreCase("game1"))
		{
			return;
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "select"))
		{
			EmbedBuilder embed = new EmbedBuilder();
			
			embed.setTitle("Select your character");
			
			embed.setFooter("Game Created by Octavian", event.getGuild().getOwner().getUser().getAvatarUrl());
			
			embed.setColor(Color.GREEN);
			
			event.getChannel().sendMessageEmbeds(embed.build()).queue(message -> {
				SelectHeroMsg = message;
				
				message.addReaction(ReactViking1).queue();
				message.addReaction(ReactViking2).queue();
				message.addReaction(ReactViking3).queue();
				message.addReaction(ReactViking4).queue();
				});
			
			event.getChannel().sendMessage(Viking1 + " " + Viking2 + " " + Viking3 + " " + Viking4).complete();
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "grid"))
		{
			CreateGrid();
			
			SendGridMessage(event.getChannel());
		}
	}
		
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		if (!event.getChannel().getName().equalsIgnoreCase("game1") ||
			event.getMember().getUser().equals(event.getJDA().getSelfUser()))
		{
			return;
		}
		
		// Selecting the Hero Icon
		if (SelectHeroMsg != null && event.getMessageId().equalsIgnoreCase(SelectHeroMsg.getId()))
		{
			if (event.getReactionEmote().getName().equalsIgnoreCase("viking1"))
			{
				Hero.HeroIcon = Viking1;
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase("viking2"))
			{
				Hero.HeroIcon = Viking2;
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase("viking3"))
			{
				Hero.HeroIcon = Viking3;
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase("viking4"))
			{
				Hero.HeroIcon = Viking4;
			}
		}
		// TODO: Check if it is the GridMessage
		else 
		{
			if (event.getReactionEmote().getName().equalsIgnoreCase(ArrowRight))
			{
				MoveHero(direction.RIGHT, event.getChannel());
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase(ArrowLeft))
			{
				MoveHero(direction.LEFT, event.getChannel());
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase(ArrowDown))
			{
				MoveHero(direction.DOWN, event.getChannel());
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase(ArrowUp))
			{
				MoveHero(direction.UP, event.getChannel());
			}
			else if (event.getReactionEmote().getName().equalsIgnoreCase(RefreshArrows))
			{
				CreateGrid();
				
				SendGridMessage(event.getChannel());
			}
		}
		
		/*
		else if (!event.getMember().getUser().equals(event.getJDA().getSelfUser()))
		{
			// Delete any extra reactions test -- Test works
			event.getReaction().removeReaction(event.getUser()).queue();
		}*/
	}
	
	public void SendGridMessage(TextChannel channel)
	{
		channel.sendMessage(GridString).queue(message -> {
			message.addReaction(ArrowLeft).queue();
			message.addReaction(ArrowRight).queue();
			message.addReaction(ArrowDown).queue();
			message.addReaction(ArrowUp).queue();
			message.addReaction(RefreshArrows).queue();
			});
	}

	public void MoveHero(direction dir, TextChannel channel)
	{
		if (dir == direction.RIGHT)
		{
			MoveRight();
		}
		else if (dir == direction.LEFT) 
		{
			MoveLeft();
		}
		else if (dir == direction.DOWN)
		{
			MoveDown();
		}
		else if (dir == direction.UP)
		{
			MoveUp();
		}
		
		CreateGridString();
		
		SendGridMessage(channel);
	}
	
	public void MoveRight()
	{
		if (Hero.pos.x < GridSize -1 && 
			Grid[Hero.pos.y][Hero.pos.x + 1] == block)
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.x += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
	}
	
	public void MoveLeft()
	{
		if (Hero.pos.x > 0 && 
			Grid[Hero.pos.y][Hero.pos.x - 1] == block)
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.x -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
	}
	
	public void MoveDown()
	{
		if (Hero.pos.y < GridSize - 1 && 
			Grid[Hero.pos.y + 1][Hero.pos.x] == block)
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.y += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
	}
	
	public void MoveUp()
	{
		if (Hero.pos.y > 0 && 
			Grid[Hero.pos.y - 1][Hero.pos.x] == block)
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.y -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
	}
}
