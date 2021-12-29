package GridBlocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GridBlocksGame extends ListenerAdapter{

	// Basic Grid Emotes
	String block = "🔳";
	String giant = "👹";
	
	// Reaction Emote Strings
	String ReactViking1 = "viking1:922507879402061895";
	String ReactViking2 = "viking2:922514842638774352";
	String ReactViking3 = "viking3:922515468303101952";
	String ReactViking4 = "viking4:922515511517007882";
	
	// Hero Icons Strings
	String Viking1 = "<:viking1:922507879402061895>";
	String Viking2 = "<:viking2:922514842638774352>";
	String Viking3 = "<:viking3:922515468303101952>";
	String Viking4 = "<:viking4:922515511517007882>";
	
	// Arrow Emotes Strings
	String ArrowLeft= "⬅️";
	String ArrowRight = "➡️";
	String ArrowDown = "⬇️";
	String ArrowUp = "⬆️";
	String RefreshArrows = "🔄";
	
	// Lock Emote String
	String Lock = "🔐";
	
	// Submit Emote String
	String submit_icon = "✅";
	
	// Select character message
	Message SelectHeroMsg = null;
	
	// Latest Grid Message to react to
	Message GridMsg = null;
	
	// Player Class
	GridViking Hero;
	
	// Grid variables
	String[][] Grid;
	String GridString;
	int GridSize;
	
	// Numbers array
	List<GridNumberBlock> numbers_list = new ArrayList<GridNumberBlock>();
	
	// Expected answers
	AnswerQuotient ans_quotient;
	AnswerRemainder ans_remainder;
	int divident_val;
	
	int divisor_val;
	
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
		
		ans_quotient = new AnswerQuotient();
		
		ans_remainder = new AnswerRemainder();
	}
	
	public void CreateGrid()
	{		
		GridSize = 6;
		
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
		
		// Add the Giant monster
		AddGiantMonster();
		
		// Clear the numbers list
		numbers_list.clear();
		
		// Create the simple question
		CreateSimpleQuestion();
		
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
	
	public void AddNumber(int val)
	{
		Random numRandom = new Random();
		
		int x = numRandom.nextInt(GridSize);
		int y = numRandom.nextInt(GridSize);
		
		if (isNearEdge(x, y))
		{
			AddNumber(val);
			return;
		}
		
		if (Grid[y][x].equals(block))
		{
			GridNumberBlock num = new GridNumberBlock(new GridPosition(x, y), val);
			
			Grid[y][x] = num.num_icon;
			
			numbers_list.add(num);
		}
		else 
		{
			AddNumber(val);
		}
	}
	
	public void CreateGridString()
	{
		add_answer_icons();
		
		GridString = "";
		
		// Add the question string
		GridString = "Give the quotient **q** and remainder **r** according to the Division Theorem when the integer " +
					   divident_val + " is divided by the positive integer " + divisor_val + ".\n";
		
		for(int i = 0; i < GridSize; i++)
		{
			for(int j = 0; j < GridSize; j++)
			{
				GridString = GridString + Grid[i][j];
			}
			
			GridString += "\n";
		}
	}
	
	public Boolean isNearEdge(int x, int y)
	{
		if (x == GridSize -1 || y == GridSize -1 || x == 0 || y == 0)
		{
			return true;
		}
		
		return false;
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
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "simple"))
		{
			CreateSimpleQuestion();
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
		else if (GridMsg != null && event.getMessageId().equalsIgnoreCase(GridMsg.getId()))
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
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		// Create the room under the Games category
		// Looks for categories named Game and stores the first (and the only) one
		net.dv8tion.jda.api.entities.Category gamesCategory = event.getGuild().getCategoriesByName("Games", true).get(0);
		
		// Gets the text channels under the Games category
		List<TextChannel> categoryChannels = gamesCategory.getTextChannels();
		
		Boolean channelExists = false;
		
		for (int i = 0; i < categoryChannels.size(); i++)
		{
			TextChannel game_channel = categoryChannels.get(i);
			
			if (game_channel.getName().equalsIgnoreCase("game1") && 
					game_channel.getTopic().equalsIgnoreCase(event.getUser().getId()))
			{
				channelExists = true;
				
				// Used for Debug purposes
				event.getGuild().getDefaultChannel().sendMessage("Channel Exists").queue();
				
				// Add permissions to the previous channel
				game_channel.getManager().putMemberPermissionOverride(event.getMember().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null).complete();
				
				return;
			}
		}
		
		if (!channelExists)
		{
			Guild guild = event.getMember().getGuild();
			
			String name = "game1";
	
			gamesCategory.createTextChannel(name)
	         .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
	         .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
	         .complete(); // this actually sends the request to discord.
		    
			categoryChannels = gamesCategory.getTextChannels();
			
			for (int i = 0; i < categoryChannels.size(); i++)
			{
				if (categoryChannels.get(i).getName().equalsIgnoreCase("game1") && event.getMember().hasAccess(categoryChannels.get(i)))
				{
					categoryChannels.get(i).getManager().setTopic(event.getUser().getId()).complete();
					
					// Used for Debug purposes
					event.getGuild().getDefaultChannel().sendMessage("Topic Set").queue();
					
					break;
				}
			}
		    
		    // Used for Debug purposes
		    event.getGuild().getDefaultChannel().sendMessage("Channel Created").queue();
		}
	}
	
	public void SendGridMessage(TextChannel channel)
	{
		channel.sendMessage(GridString).queue(message -> {
			GridMsg = message;
			message.addReaction(ArrowLeft).queue();
			message.addReaction(ArrowRight).queue();
			message.addReaction(ArrowDown).queue();
			message.addReaction(ArrowUp).queue();
			message.addReaction(RefreshArrows).queue();
			
			// Add the submit emote
			if (get_num_at_position(ans_quotient.pos.y, ans_quotient.pos.x) != null &&
					get_num_at_position(ans_remainder.pos.y, ans_remainder.pos.x) != null)
			{
				message.addReaction(submit_icon).queue();
			}
			
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
		// Check if there is a number in the moving direction
		GridNumberBlock rightNum = get_num_at_position(Hero.pos.y, Hero.pos.x + 1);
		
		// Check if we can move
		if (Hero.pos.x < GridSize -1 &&
			can_move_dir(Hero.pos.y, Hero.pos.x + 1))
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.x += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
		else if (Hero.pos.x < GridSize -2 &&
				can_move_dir(Hero.pos.y, Hero.pos.x + 2) &&
				rightNum != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.x += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block to the right
			rightNum.pos.x += 1;
			Grid[rightNum.pos.y][rightNum.pos.x] = rightNum.num_icon;
		}
	}
	
	public void MoveLeft()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock leftNum = get_num_at_position(Hero.pos.y, Hero.pos.x - 1);
		
		if (Hero.pos.x > 0 &&
			can_move_dir(Hero.pos.y, Hero.pos.x - 1))
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.x -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
		else if (Hero.pos.x > 1 &&
				can_move_dir(Hero.pos.y, Hero.pos.x - 2) &&
				leftNum != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.x -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block
			leftNum.pos.x -= 1;
			Grid[leftNum.pos.y][leftNum.pos.x] = leftNum.num_icon;
		}
	}
	
	public void MoveDown()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock downNum = get_num_at_position(Hero.pos.y + 1, Hero.pos.x);
		
		if (Hero.pos.y < GridSize - 1 &&
			can_move_dir(Hero.pos.y + 1, Hero.pos.x))
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.y += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
		else if (Hero.pos.y < GridSize - 2 &&
				can_move_dir(Hero.pos.y + 2, Hero.pos.x) &&
				downNum != null) 
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.y += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block
			downNum.pos.y += 1;
			Grid[downNum.pos.y][downNum.pos.x] = downNum.num_icon;
		}
	}
	
	public void MoveUp()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock upNum = get_num_at_position(Hero.pos.y - 1, Hero.pos.x);
		
		if (Hero.pos.y > 0 &&
			can_move_dir(Hero.pos.y - 1, Hero.pos.x))
		
		{
			Grid[Hero.pos.y][Hero.pos.x] = block;
		
			Hero.pos.y -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
		}
		else if (Hero.pos.y > 1 &&
				can_move_dir(Hero.pos.y - 2, Hero.pos.x) &&
				upNum != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.y -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the number
			upNum.pos.y -= 1;
			Grid[upNum.pos.y][upNum.pos.x] = upNum.num_icon;
		}
		
	}
	
	public Boolean can_move_dir(int y, int x)
	{
		if (Grid[y][x] == block)
		{
			return true;
		}
		else if (Grid[y][x] == ans_quotient.icon_string)
		{
			return true;
		}
		else if (Grid[y][x] == ans_remainder.icon_string)
		{
			return true;
		}
		
		return false;
	}
	
	public GridNumberBlock get_num_at_position(int y, int x)
	{
		for (int i = 0; i < numbers_list.size(); i++)
		{
			if (numbers_list.get(i).pos.x == x &&
					numbers_list.get(i).pos.y == y)
			{
				return numbers_list.get(i);
			}
		}
		
		return null;
	}
	
	public void CreateSimpleQuestion()
	{
		Random rand = new Random();
		
		divident_val = rand.ints(0, 10).findFirst().getAsInt();
		
		divisor_val = rand.ints(2, 10).findFirst().getAsInt();
		
		System.out.println("Random divdent " + divident_val + '\n' +
							"Random Divisor " + divisor_val + '\n');
		
		ans_quotient.expected_val = Math.floorDiv(divident_val, divisor_val);
		ans_remainder.expected_val = Math.floorMod(divident_val, divisor_val);
		
		System.out.println("Quotient " + ans_quotient.expected_val + '\n' +
				"Remainder " + ans_remainder.expected_val + '\n');
		
		add_simple_numbers();
		
		add_quotient();
		
		add_remainder();
	}
	
	public void add_simple_numbers()
	{
		// Add the expected answers
		AddNumber(ans_quotient.expected_val);
		
		AddNumber(ans_remainder.expected_val);
		
		// Add two more random numbers
		
		Random numRandom = new Random();
		
		int randomVal = numRandom.nextInt(Math.min(ans_quotient.expected_val + 4, 10));
		int loops_try = 0;
		
		// Add three more random  numbers
		while (is_value_in_list(randomVal))
		{
			randomVal = numRandom.nextInt(Math.min(ans_quotient.expected_val + 4, 10));
			
			loops_try += 1;
			
			if (loops_try > 4)
			{
				randomVal = numRandom.nextInt(10);
			}
		}
		
		AddNumber(randomVal);
		
		loops_try = 0;
		
		while (is_value_in_list(randomVal))
		{
			randomVal = numRandom.nextInt(Math.min(ans_remainder.expected_val + 4, 10));
			
			loops_try += 1;
			
			if (loops_try > 4)
			{
				randomVal = numRandom.nextInt(10);
			}
		}
		
		AddNumber(randomVal);
		
		loops_try = 0;
		
		while (is_value_in_list(randomVal))
		{
			randomVal = numRandom.nextInt(Math.min(ans_quotient.expected_val + 4, 10));
			
			loops_try += 1;
			
			if (loops_try > 4)
			{
				randomVal = numRandom.nextInt(10);
			}
		}
		
		AddNumber(randomVal);
	}
	
	public void add_quotient()
	{
		Random numRandom = new Random();
		
		int x = numRandom.nextInt(GridSize);
		int y = numRandom.nextInt(GridSize);
		
		if (Grid[y][x].equals(block))
		{
			ans_quotient.pos.x = x;
			ans_quotient.pos.y = y;
			
			Grid[y][x] = ans_quotient.icon_string;
		}
		else 
		{
			add_quotient();
		}
	}
	
	public void add_remainder()
	{
		Random numRandom = new Random();
		
		int x = numRandom.nextInt(GridSize);
		int y = numRandom.nextInt(GridSize);
		
		if (Grid[y][x].equals(block))
		{
			ans_remainder.pos.x = x;
			ans_remainder.pos.y = y;
			
			Grid[y][x] = ans_remainder.icon_string;
		}
		else 
		{
			add_remainder();
		}
	}
	
	public Boolean is_value_in_list(int val)
	{
		for (int i = 0; i < numbers_list.size(); i++)
		{
			if (val == numbers_list.get(i).num_value)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void add_answer_icons()
	{
		if (Grid[ans_quotient.pos.y][ans_quotient.pos.x] == block)
		{
			Grid[ans_quotient.pos.y][ans_quotient.pos.x] = ans_quotient.icon_string;
		}
		
		if (Grid[ans_remainder.pos.y][ans_remainder.pos.x] == block)
		{
			Grid[ans_remainder.pos.y][ans_remainder.pos.x] = ans_remainder.icon_string;
		}
	}
}
