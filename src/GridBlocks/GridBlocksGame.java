package GridBlocks;

import java.awt.Color;
import java.util.ArrayList;
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
	GiantBlock giantBlock;
	
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
	List<MinusBlock>	  minus_blocks_list = new ArrayList<MinusBlock>();
	
	// Expected answers
	AnswerQuotient ans_quotient;
	AnswerRemainder ans_remainder;
	int divident_val;
	
	int divisor_val;
	
	// Store the game channel
	TextChannel channel_game;
	
	// Current game difficulty for the game
	public enum difficulty
	{
		DIF_EASY,
		DIF_MEDIUM,
		DIF_HARD
	}
	
	// Direction enum
	enum direction {
		LEFT,
		RIGHT,
		DOWN,
		UP
	}
	
	difficulty[] diff_levels = { difficulty.DIF_EASY, 
									difficulty.DIF_EASY, 
									difficulty.DIF_MEDIUM, 
									difficulty.DIF_MEDIUM, 
									difficulty.DIF_HARD, 
									difficulty.DIF_HARD};
	
	int current_difficulty;
	
	public GridBlocksGame() 
	{
		GridString = "";
		Grid = null;
		
		Hero = new GridViking();
		
		giantBlock = new GiantBlock();
		
		current_difficulty = 5;
		
		channel_game = null;
	}
	
	public void CreateGrid()
	{		
		switch (diff_levels[current_difficulty]) {
		case DIF_EASY:
			GridSize = 6;
			break;
		case DIF_MEDIUM:
			GridSize = 8;
			break;
		case DIF_HARD:
			GridSize = 10;
			break;
		default:
			GridSize = 6;
			System.out.println("DIFFICULTY ERROR!");
			break;
		}
		
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
		
		// Clear minus list
		minus_blocks_list.clear();
		
		switch (diff_levels[current_difficulty]) {
		case DIF_EASY:
			CreateSimpleQuestion();
			break;
		case DIF_MEDIUM:
			CreateMediumQuestion();
			break;
		case DIF_HARD:
			CreateHardQuestion();
			break;
		default:
			CreateSimpleQuestion();
			System.out.println("DIFFICULTY ERROR!");
			break;
		}
		
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
		
		giantBlock.pos.x = i;
		giantBlock.pos.y = j;
		
		if (Grid[giantBlock.pos.y][giantBlock.pos.x].equals(block))
		{
			Grid[giantBlock.pos.y][giantBlock.pos.x] = giantBlock.icon_string;
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
	
	public void AddNumber(int number_to_add)
	{
		if (number_to_add < 0)
		{
			add_minus_sign();
			
			number_to_add *= -1;
		}
		
		do 
		{	
			int val = number_to_add%10;
			number_to_add /= 10;
			
			Random numRandom = new Random();
			
			int x = numRandom.nextInt(GridSize);
			int y = numRandom.nextInt(GridSize);
			
			if (isNearEdge(x, y) || !can_add_block(x, y))
			{
				AddNumber(val);
				continue;
			}
			
			if (Grid[y][x].equals(block))
			{
				GridNumberBlock block_num = new GridNumberBlock(new GridPosition(x, y), val);
				
				Grid[y][x] = block_num.num_icon;
				
				numbers_list.add(block_num);
				
				continue;
			}
			else 
			{
				AddNumber(val);
				continue;
			}
			
		} while (number_to_add != 0);
	}
	
	public void CreateGridString()
	{
		Hero.is_alive = true;
		
		add_answer_icons();
		
		check_monster();
		
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
		
		if (!event.getMember().hasAccess(event.getChannel()))
		{
			return;
		}
		// TODO: Check this
		/*if (!event.getChannel().getTopic().equalsIgnoreCase(event.getAuthor().getId()))
		{
			return;
		}*/
		
		if (channel_game == null)
		{
			channel_game = event.getChannel();
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "select"))
		{
			send_select_message(event.getChannel());
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "grid"))
		{
			CreateGrid();
			
			SendGridMessage(event.getChannel());
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "help"))
		{
			send_help_message(event.getChannel());
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
		
		if (!event.getMember().hasAccess(event.getChannel()))
		{
			return;
		}
		// TODO: Check this
		/*if (!event.getChannel().getTopic().equalsIgnoreCase(event.getUser().getId()))
		{
			return;
		}*/
		
		if (channel_game == null)
		{
			channel_game = event.getChannel();
		}
		
		// Selecting the Hero Icon
		if (SelectHeroMsg != null && event.getMessageId().equalsIgnoreCase(SelectHeroMsg.getId()))
		{
			select_hero(event);
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
			else if (event.getReactionEmote().getName().equalsIgnoreCase(submit_icon))
			{
				submit_answer(event);
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
				
				// Store channel
				if(channel_game == null)
				{
					channel_game = game_channel;
				}
				
				// Add permissions to the previous channel
				game_channel.getManager().putMemberPermissionOverride(event.getMember().getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL), null).queue();
				
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
					categoryChannels.get(i).getManager().setTopic(event.getUser().getId()).queue();
					
					// Send the select character message
					send_select_message(categoryChannels.get(i));
					
					// Store channel
					if (channel_game == null)
					{
						channel_game = categoryChannels.get(i);
					}
					
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
			if (Hero.is_alive)
			{
				message.addReaction(ArrowLeft).queue();
				message.addReaction(ArrowRight).queue();
				message.addReaction(ArrowDown).queue();
				message.addReaction(ArrowUp).queue();
			}
			message.addReaction(RefreshArrows).queue();
			
			// Add the submit emote
			if (get_quotient_value() != null &&
				get_remainder_value() != null)
			{
				message.addReaction(submit_icon).queue();
			}
			
			});
	}

	public void send_select_message(TextChannel channel)
	{
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("Select your character");
		
		if (channel.getGuild().getOwner() != null)
		{
			embed.setFooter("Game Created by Octavian", channel.getGuild().getOwner().getUser().getAvatarUrl());
		}
		else 
		{
			System.out.println("Can't create Embed footer \n");
		}
		
		embed.setColor(Color.GREEN);
		
		channel.sendMessageEmbeds(embed.build()).queue(message -> {
			SelectHeroMsg = message;
			
			message.addReaction(ReactViking1).queue();
			message.addReaction(ReactViking2).queue();
			message.addReaction(ReactViking3).queue();
			message.addReaction(ReactViking4).queue();
			});
		
		channel.sendMessage(Viking1 + " " + Viking2 + " " + Viking3 + " " + Viking4).queue();
	}
	
	public void select_hero(GuildMessageReactionAddEvent event)
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
		
		// Send the message
		event.getChannel().sendMessage("You selected " + Hero.HeroIcon + "!").queue();
		
		if (Grid == null)
		{
			CreateGrid();
			SendGridMessage(event.getChannel());
		}
		else 
		{
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			CreateGridString();
			SendGridMessage(event.getChannel());
		}
	}
	
	public void MoveHero(direction dir, TextChannel channel)
	{
		if (!Hero.is_alive)
		{
			return;
		}
		
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
		
		// Check if there is a minus sign
		MinusBlock minus_block = get_minus_at_position(Hero.pos.y, Hero.pos.x + 1);
		
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
		// Move the minus block
		else if (Hero.pos.x < GridSize -2 &&
				can_move_dir(Hero.pos.y, Hero.pos.x + 2) &&
				minus_block != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.x += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block to the right
			minus_block.pos.x += 1;
			Grid[minus_block.pos.y][minus_block.pos.x] = minus_block.icon_string;
		}
	}
	
	public void MoveLeft()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock leftNum = get_num_at_position(Hero.pos.y, Hero.pos.x - 1);
		
		// Check if there is a minus sign
		MinusBlock minus_block = get_minus_at_position(Hero.pos.y, Hero.pos.x - 1);
		
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
		else if (Hero.pos.x > 1 &&
				can_move_dir(Hero.pos.y, Hero.pos.x - 2) &&
				minus_block != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.x -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block
			minus_block.pos.x -= 1;
			Grid[minus_block.pos.y][minus_block.pos.x] = minus_block.icon_string;
		}
	}
	
	public void MoveDown()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock downNum = get_num_at_position(Hero.pos.y + 1, Hero.pos.x);
		
		// Check if there is a minus sign
		MinusBlock minus_block = get_minus_at_position(Hero.pos.y + 1, Hero.pos.x);
		
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
		else if (Hero.pos.y < GridSize - 2 &&
				can_move_dir(Hero.pos.y + 2, Hero.pos.x) &&
				minus_block != null) 
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.y += 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the block
			minus_block.pos.y += 1;
			Grid[minus_block.pos.y][minus_block.pos.x] = minus_block.icon_string;
		}
	}
	
	public void MoveUp()
	{
		// Check if there is a number in the moving direction
		GridNumberBlock upNum = get_num_at_position(Hero.pos.y - 1, Hero.pos.x);
		
		// Check if there is a minus sign
		MinusBlock minus_block = get_minus_at_position(Hero.pos.y - 1, Hero.pos.x);
		
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
		else if (Hero.pos.y > 1 &&
				can_move_dir(Hero.pos.y - 2, Hero.pos.x) &&
				minus_block != null)
		{
			// Move the hero
			Grid[Hero.pos.y][Hero.pos.x] = block;
			
			Hero.pos.y -= 1;
			
			Grid[Hero.pos.y][Hero.pos.x] = Hero.HeroIcon;
			
			// Move the number
			minus_block.pos.y -= 1;
			Grid[minus_block.pos.y][minus_block.pos.x] = minus_block.icon_string;
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
		else if (Grid[y][x] == giantBlock.icon_string)
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
	
	public MinusBlock get_minus_at_position(int y, int x)
	{
		for (int i = 0; i < minus_blocks_list.size(); i++)
		{
			if (minus_blocks_list.get(i).pos.x == x &&
					minus_blocks_list.get(i).pos.y == y)
			{
				return minus_blocks_list.get(i);
			}
		}
		
		return null;
	}
	
	public void CreateSimpleQuestion()
	{
		ans_quotient = new AnswerQuotient(1);
		
		ans_remainder = new AnswerRemainder(1);
		
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
	
	public void CreateMediumQuestion()
	{
		ans_quotient = new AnswerQuotient(2);
		
		ans_remainder = new AnswerRemainder(2);
		
		Random rand = new Random();
		
		divident_val = rand.ints(0, 100).findFirst().getAsInt();
		
		divisor_val = rand.ints(2, 20).findFirst().getAsInt();
		
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
	
	public void CreateHardQuestion()
	{
		ans_quotient = new AnswerQuotient(3);
		
		ans_remainder = new AnswerRemainder(3);
		
		Random rand = new Random();
		
		divident_val = rand.ints(0, 100).findFirst().getAsInt();
		
		while(divident_val == 0)
		{
			divident_val = rand.ints(0, 100).findFirst().getAsInt();
		}
		
		divident_val *= -1;
		
		divisor_val = rand.ints(2, 20).findFirst().getAsInt();
		
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
		
		int num_to_add = 3;
		
		switch (diff_levels[current_difficulty]) 
		{
		case DIF_EASY:
			num_to_add = 5 - numbers_list.size();
			break;
		case DIF_MEDIUM:
			num_to_add = 9 - numbers_list.size();
			break;
		case DIF_HARD:
			num_to_add = 14 - numbers_list.size();
			break;
		default:
			num_to_add = 3;
			System.out.println("DIFFICULTY ERROR!");
			break;
		}
		
		for (int i = 0; i < num_to_add; i++)
		{
			int randomVal = numRandom.nextInt(Math.min(Math.abs(ans_quotient.expected_val) + 4, 10));
			int loops_try = 0;
			
			// Add three more random  numbers
			while (is_value_in_list(randomVal))
			{
				randomVal = numRandom.nextInt(Math.min(Math.abs(ans_quotient.expected_val) + 4, 10));
				
				loops_try += 1;
				
				if (loops_try > 4)
				{
					randomVal = numRandom.nextInt(10);
					break;
				}
			}
			
			AddNumber(randomVal);
		}
		
		if (diff_levels[current_difficulty] == difficulty.DIF_HARD)
		{
			// Add decoy minus signs
			while (minus_blocks_list.size() < 3)
			{
				add_minus_sign();
			}
		}
	}
	
	public void add_quotient()
	{
		Random numRandom = new Random();
		
		int x = numRandom.nextInt(GridSize - ans_quotient.num_blocks + 1);
		int y = numRandom.nextInt(GridSize);
		
		for (int i = 0; i < ans_quotient.num_blocks; i++)
		{
			if (Grid[y][x + i].equals(block))
			{
				ans_quotient.pos[i].x = x + i;
				ans_quotient.pos[i].y = y;
				
				Grid[y][x + i] = ans_quotient.icon_string;
			}
			else 
			{
				for (int j = 0; j < ans_quotient.num_blocks; j++)
				{
					int l_y = ans_quotient.pos[j].y;
					int l_x = ans_quotient.pos[j].x;
					
					if (Grid[l_y][l_x].equalsIgnoreCase(ans_quotient.icon_string))
					{
						Grid[l_y][l_x] = block;
					}
				}
				add_quotient();
				break;
			}
		}
	}
	
	public void add_remainder()
	{
		Random numRandom = new Random();
		
		int x = numRandom.nextInt(GridSize - ans_quotient.num_blocks + 1);
		int y = numRandom.nextInt(GridSize);
		
		for (int i = 0; i < ans_remainder.num_blocks; i++)
		{
			if (Grid[y][x + i].equals(block))
			{
				ans_remainder.pos[i].x = x + i;
				ans_remainder.pos[i].y = y;
				
				Grid[y][x + i] = ans_remainder.icon_string;
			}
			else 
			{
				for (int j = 0; j < ans_remainder.num_blocks; j++)
				{
					int l_y = ans_remainder.pos[j].y;
					int l_x = ans_remainder.pos[j].x;
					
					if (Grid[l_y][l_x].equalsIgnoreCase(ans_remainder.icon_string))
					{
						Grid[l_y][l_x] = block;
					}
				}
				add_remainder();
				break;
			}
		}
	}
	
	public void add_minus_sign()
	{
			
			Random numRandom = new Random();
			
			int x = numRandom.nextInt(GridSize);
			int y = numRandom.nextInt(GridSize);
			
			if (isNearEdge(x, y) || !can_add_block(x, y))
			{
				add_minus_sign();
				return;
			}
			
			if (Grid[y][x].equals(block))
			{
				MinusBlock minus_block = new MinusBlock(x, y);
				
				Grid[y][x] = minus_block.icon_string;
				
				minus_blocks_list.add(minus_block);
				
				return;
			}
			else 
			{
				add_minus_sign();
				return;
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
	
	// If another icon - number or player - moves out, add the Q or R icons back
	public void add_answer_icons()
	{
		for (int i = 0; i < ans_quotient.num_blocks; i++)
		{
			if (Grid[ans_quotient.pos[i].y][ans_quotient.pos[i].x] == block)
			{
				Grid[ans_quotient.pos[i].y][ans_quotient.pos[i].x] = ans_quotient.icon_string;
			}
		}
		
		for (int i = 0; i < ans_remainder.num_blocks; i++)
		{
			if (Grid[ans_remainder.pos[i].y][ans_remainder.pos[i].x] == block)
			{
				Grid[ans_remainder.pos[i].y][ans_remainder.pos[i].x] = ans_remainder.icon_string;
			}
		}
	}
	
	public void submit_answer(GuildMessageReactionAddEvent event)
	{
		// Check if the answer is correct
		if (get_quotient_value() != null &&
			get_remainder_value() != null)
		{
			int q_val = get_quotient_value();
			int r_val = get_remainder_value();
			
			String string_msg = "";
			
			if (q_val == ans_quotient.expected_val &&
				r_val == ans_remainder.expected_val)
			{
				string_msg = "Well Done";
			}
			else 
			{
				string_msg = "Wrong Answer! Try again!";
			}
			
			event.getChannel().sendMessage(string_msg).queue();
		}
		else 
		{
			System.out.println("Invalid submission \n");
		}
	}
	
	public void send_help_message(TextChannel channel)
	{
		String help_string = 
		"🔘 Complete the given equations by moving the number blocks into the " + 
		":regional_indicator_q:" + " (quotient) and " + ":regional_indicator_r:" + " (remainder) blocks. \n";
		
		help_string += "🔘 To move your hero, select the arrows emotes below the maze " + 
		ArrowLeft + ArrowRight +ArrowDown + ArrowUp + "\n";
		
		help_string += "🔘 If you get stuck, use the " + RefreshArrows + " button \n";
		
		help_string += "🔘 The Giant " + giantBlock.icon_string + " eats heroes and number blocks, avoid it at all costs! \n";
		
		help_string += "🔘 You can cross over " + ":regional_indicator_q:" + " and " + 
		":regional_indicator_r:" + " blocks.\n🔘 Moving towards number blocks will push them. \n";
		
		help_string += "🔘 When the numbers are in their place, use the " + submit_icon + " to submit your answer. \n";
		
		help_string += "🔘 !help -- brings up the help panel \n" +
					   "🔘 !select -- lets you select the hero icon \n";
		
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("Help Panel");
		
		embed.setDescription(help_string);
		
		if (channel.getGuild().getOwner() != null)
		{
			embed.setFooter("Game Created by Octavian", channel.getGuild().getOwner().getUser().getAvatarUrl());
		}
		else 
		{
			System.out.println("Can't create Embed footer \n");
		}
		
		embed.setColor(Color.GREEN);
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}
	
	// Checks if a number can be moved over a block
	// Not the same as can_move_dir
	public Boolean can_add_block(int x, int y)
	{
		int blockers_nearby = 0;
		
		if (!can_move_over(x, y - 1))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x + 1, y - 1))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x + 1, y))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x + 1, y + 1))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x, y + 1))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x - 1, y + 1))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x - 1, y))
		{
			blockers_nearby ++;
		}
		
		if (!can_move_over(x - 1, y - 1))
		{
			blockers_nearby ++;
		}
		
		if (blockers_nearby > 2)
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	
	public Boolean can_move_over(int x, int y)
	{
		if (Grid[y][x] == block ||
			Grid[y][x] == ans_quotient.icon_string ||
			Grid[y][x] == ans_remainder.icon_string)
		{
			return true;
		}
		
		return false;
	}
	
	public void check_monster()
	{
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setColor(Color.RED);
		
		boolean send_embed = false;
		
		// Check if the hero is eaten by the giant
		if (Hero.pos.equals(giantBlock.pos))
		{		
			embed.setDescription("The hero was eaten by the fierce Giant! Try again!\n");
			
			send_embed = true;
			
			Hero.is_alive = false;
		}
		
		// Check if numbers are eaten by the giant
		for (int i = 0; i < numbers_list.size(); i++)
		{
			if (numbers_list.get(i).pos.equals(giantBlock.pos))
			{
				embed.setDescription("The number "  + numbers_list.get(i).num_value +  " was eaten by the fierce Giant! \n");
				
				send_embed = true;
				
				numbers_list.remove(i);
				
				break;
			}
		}
		
		Grid[giantBlock.pos.y][giantBlock.pos.x] = giantBlock.icon_string;
		
		if (send_embed)
		{
			channel_game.sendMessageEmbeds(embed.build()).queue();
		}
	}
	
	// Combine all Q blocks to get the value
	public Integer get_quotient_value()
	{
		Integer new_int = null;
		
		for (int i = 0; i < ans_quotient.num_blocks; i++)
		{
			GridNumberBlock l_num = get_num_at_position(ans_quotient.pos[i].y, ans_quotient.pos[i].x);
			
			if (l_num != null)
			{
				if (new_int == null)
				{
					new_int = 0;
				}
				
				new_int = new_int * 10 + l_num.num_value;
			}
		}
		
		return new_int;
	}
	
	public Integer get_remainder_value()
	{

		Integer new_int = null;
		
		for (int i = 0; i < ans_remainder.num_blocks; i++)
		{
			GridNumberBlock l_num = get_num_at_position(ans_remainder.pos[i].y, ans_remainder.pos[i].x);
			
			if (l_num != null)
			{
				if (new_int == null)
				{
					new_int = 0;
				}
				
				new_int = new_int * 10 + l_num.num_value;
			}
		}
		
		return new_int;
	
	}
}
