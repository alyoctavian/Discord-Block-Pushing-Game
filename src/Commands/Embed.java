package Commands;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import Main.BotStartup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Embed extends ListenerAdapter{
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)
	{
		String[] args = event.getMessage().getContentRaw().split(" ");
		
		if (!BotStartup.isOwner(event.getMessage().getMember().getId()))
		{
			return;
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "test"))
		{
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Embed Title");
			embed.setDescription("This is the embed description");
			embed.addField("Embed Field 1", "This is the content of field 1", false);
			embed.addField("Embed Field 2", "This is the content of field 2", false);
			embed.setFooter("Bot Created by Octavian", event.getGuild().getOwner().getUser().getAvatarUrl());
			
			embed.setColor(Color.GREEN);
			
			event.getChannel().sendMessageEmbeds(embed.build()).queue();
			
			
			embed.clear();
			
			event.getChannel().sendMessage("Test mention channel <#925815467178852362>").queue();
		}
		
		if (args[0].equalsIgnoreCase(BotStartup.prefix + "story") && BotStartup.isOwner(event.getMessage().getMember().getId()))
		{
			SendWelcomeMessage(event.getChannel());
		}
	}
	
	public void SendWelcomeMessage(TextChannel channel)
	{
		// Upload and send an image to current channel
		try {
		    URL url = new URL("https://i.icanvas.com/BMN6542?d=2&sh=h&p=1&bg=g&t=1623505707");
				BufferedImage img = ImageIO.read(url);
				File file = new File("temp.jpg"); // change the '.jpg' to whatever extension the image has
				ImageIO.write(img, "jpg", file); // again, change 'jpg' to the correct extension
				channel.sendFile(file).queue();
		}
		catch (Exception e) {
			channel.sendMessage("Error fetching image.").queue();
		}
		
		String messageText = "🟤 Hello dear hero, glad to see you again. I'm Ratatosk, the messenger squirrel.\n" + "\n";
		
		messageText += "🟤 I have invited you here to participate in the race to the top of Jotunheim's misty mountains!\n" + "\n";
		
		messageText += "🟤 To reach the top, you need to clear the paths blocked by the magical numbers.\n" + "\n";
		
		messageText += "🟤 **Be careful!** The giants are wandering around!\n" + "\n";
		
		messageText += "🟤 To get started, visit <#925815467178852362>.\n" + "\n";
		
		messageText += "🟤 If you want to join a guild, visit <#931918407501242439>.\n" + "\n";
		
		messageText += "🟤 And finally, to start the race, go to the **Ratatosk Race** channel.\n" + "\n";
		
		messageText += "🟤 You can see how you rank among the other heroes in <#925826296590766100>\n" + "\n";
		
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle("Ratatosk's Race");
		
		embed.setColor(0x964B00);
		
		embed.setDescription(messageText);
		
		channel.sendMessageEmbeds(embed.build()).queue();
	}
}
