package bot.user_interactions.spotify;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.hc.core5.http.ParseException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import com.wrapper.spotify.exceptions.detailed.ForbiddenException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Recommendations;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.UploadCustomPlaylistCoverImageRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import bot.embeds.ErrorAlert;
import bot.embeds.OkAlert;
import db.dao.DAOSpotifyUser;
import model.SpotifyUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Spotify {
	
	DAOSpotifyUser daoSpotify = new DAOSpotifyUser();
	
	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
		    .setClientId(System.getenv("SPOTIFY_CLIENT_ID"))
		    .setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET"))
		    .setRedirectUri(SpotifyHttpManager.makeUri("https://legendaryhelper.xyz/spotify/confirm"))
		    .build();
	
	public void onNewMessage(GuildMessageReceivedEvent event) {
		
		if(event.getAuthor().isBot()) return;
		
		String link = "Para conectar sua conta do Spotify, entre no seguinte link:\n"
				+ "https://legendaryhelper.xyz/spotify/connect?user_id=" + event.getAuthor().getId()
				+ "\nFique tranquilo, eu uso o sistema de autenticação do próprio Spotify "
				+ "para fazer a conexão. Você pode revogar acesso a qualquer momento.\n"
				+ "Para ajuda, digite `!spotify help`";
		
		String message = event.getMessage().getContentRaw();
		
		if(event.getMessage().getContentRaw().split(" ")[0].equalsIgnoreCase("!spotify")) {
			if(message.equalsIgnoreCase("!spotify") || message.equalsIgnoreCase("!spotify connect")) {
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				SpotifyUser spotifyUser = null;
				try {
					spotifyUser = daoSpotify.search(event.getAuthor().getId());
				} catch (SQLException e3) {
					event.getChannel().sendMessage("Something went wrong.").queue();
					return;
				}
				
				if((spotifyUser == null) || (spotifyUser.getCode() == null)) {
					event.getChannel().sendMessage(link).queue();
					return;
				}
				
				if (spotifyUser.getToken() == null) {
					String[] tokens = requestToken(spotifyUser.getCode(), event);
					if(tokens == null) {
						event.getChannel().sendMessage("Sessão expirada, conecte novamente.").queue();
						event.getChannel().sendMessage(link).queue();
						return;
					} else {
						spotifyApi.setAccessToken(tokens[0]);
						spotifyApi.setRefreshToken(tokens[1]);
					}
				} else {
					spotifyApi.setAccessToken(spotifyUser.getToken());
					spotifyApi.setRefreshToken(spotifyUser.getRefreshToken());
				}

				switch(message.split(" ")[1]) {
					case "next": skipToNextTrack(spotifyApi, event, link);
						break;
					case "prev": skipToPreviousTrack(spotifyApi, event, link);
						break;
					case "play": play(spotifyApi, event, link);
						break;
					case "pause": pause(spotifyApi, event, link);
						break;
					case "np":
					case "nowplaying": nowPlayingInfo(spotifyApi, true, event, link);
						break;
					case "disconnect": disconnect(event.getAuthor().getId(), event);
						break;
					case "genpl": getRecommendations(spotifyApi, event, link);
						break;
					case "help": showHelp(event);
				}
			}	
		}
	}
	
	private String refreshToken(String refreshToken, GuildMessageReceivedEvent event) {
		// Try to get new Access Token by Refresh Token
		AuthorizationCodeRefreshRequest authCodeRefresh = spotifyApi.authorizationCodeRefresh().refresh_token(refreshToken).build();
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials = authCodeRefresh.execute();
			daoSpotify.updateToken(event.getAuthor().getId(), authorizationCodeCredentials.getAccessToken(), spotifyApi.getRefreshToken());
			return authorizationCodeCredentials.getAccessToken();
		} catch (BadRequestException e) {
			return null;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String[] requestToken(String code, GuildMessageReceivedEvent event) {
		try {
			AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
			daoSpotify.updateToken(event.getAuthor().getId(), authorizationCodeCredentials.getAccessToken(), authorizationCodeCredentials.getRefreshToken());
			return new String[] {authorizationCodeCredentials.getAccessToken(), authorizationCodeCredentials.getRefreshToken()};
		} catch (BadRequestException e) {
			return null;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		return null;
	}
	
	private void skipToNextTrack(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = spotifyApi.skipUsersPlaybackToNextTrack().build();

		try {
			skipUsersPlaybackToNextTrackRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				spotifyApi.setAccessToken(token);
				skipToNextTrack(spotifyApi, event, link);
				return;
			}
		} catch (ForbiddenException e) {
			event.getChannel().sendMessage(new ErrorAlert("Problema ao executar o comando",
					"O Spotify retornou a seguinte mensagem: `" + e.getMessage() + "`.").build()).queue();
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		nowPlayingInfo(spotifyApi, false, event, link);
	}
	
	private void skipToPreviousTrack(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = spotifyApi.skipUsersPlaybackToPreviousTrack().build();
		
		try {
			skipUsersPlaybackToPreviousTrackRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				spotifyApi.setAccessToken(token);
				skipToPreviousTrack(spotifyApi, event, link);
				return;
			}
		} catch (ForbiddenException e) {
			event.getChannel().sendMessage(new ErrorAlert("Problema ao executar o comando",
					"O Spotify retornou a seguinte mensagem: `" + e.getMessage() + "`.").build()).queue();
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		nowPlayingInfo(spotifyApi, false, event, link);
	}
	
	private void play(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi.startResumeUsersPlayback().build();
		
		try {
			startResumeUsersPlaybackRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				spotifyApi.setAccessToken(token);
				play(spotifyApi, event, link);
				return;
			}
		} catch (ForbiddenException e) {
			event.getChannel().sendMessage(new ErrorAlert("Problema ao executar o comando",
					"O Spotify retornou a seguinte mensagem: `" + e.getMessage() + "`.").build()).queue();
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		event.getChannel().sendMessage("Reprodução retomada").queue();
	}
	
	private void pause(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback().build();
		
		try {
			pauseUsersPlaybackRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				spotifyApi.setAccessToken(token);
				pause(spotifyApi, event, link);
				return;
			}
			
		} catch (ForbiddenException e) {
			event.getChannel().sendMessage(new ErrorAlert("Problema ao executar o comando",
					"O Spotify retornou a seguinte mensagem: `" + e.getMessage() + "`.").build()).queue();
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		event.getChannel().sendMessage("Reprodução pausada").queue();
	}
	
	private CurrentlyPlayingContext getUsersPlayback(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest = spotifyApi.getInformationAboutUsersCurrentPlayback().build();
		
		CurrentlyPlayingContext currentContext = null;
		try {
			currentContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return null;
			} else {
				spotifyApi.setAccessToken(token);
				getUsersPlayback(spotifyApi, event, link);
				return null;
			}
			
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		return currentContext;
	}
	
	private void nowPlayingInfo(SpotifyApi spotifyApi, boolean detailed, GuildMessageReceivedEvent event, String link) {
		CurrentlyPlayingContext currentContext = getUsersPlayback(spotifyApi, event, link);
		if(currentContext == null) {
			event.getChannel().sendMessage("Parece que você não está tocando nada agora...").queue();
			return;
		}
		Track track = (Track) currentContext.getItem();
		String trackInfo = track.getArtists()[0].getName() + "\n" + track.getAlbum().getName()
				+ " (" + track.getAlbum().getReleaseDate().split("-")[0] + ")";
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor("Reproduzindo agora (" + currentContext.getDevice().getName() + ")",
				"https://open.spotify.com/track/" + track.getId(),
				event.getAuthor().getAvatarUrl());
		embed.setColor(Color.GREEN);
		embed.setThumbnail(track.getAlbum().getImages()[0].getUrl());
		if(detailed) {			
			int totalDurationInSecs = track.getDurationMs() / 1000;
			int durationMinutes = totalDurationInSecs / 60;
			int durationSeconds = totalDurationInSecs % 60;
			String durationSecondsText = null;
			if(durationSeconds < 10) {
				durationSecondsText = "0" + durationSeconds;
			} else {
				durationSecondsText = String.valueOf(durationSeconds);
			}
			String durationTime = durationMinutes + ":" + durationSecondsText;
			
			int progressInSecs = currentContext.getProgress_ms() / 1000;
			int progressMinutes = progressInSecs / 60;
			int progressSeconds = progressInSecs % 60;
			String progressSecondsText = null;
			if(progressSeconds < 10) {
				progressSecondsText = "0" + progressSeconds;
			} else {
				progressSecondsText = String.valueOf(progressSeconds);
			}
			String progressTime = progressMinutes + ":" + progressSecondsText;
			
			int totalProportion = totalDurationInSecs/10;
			int a = progressInSecs / totalProportion;
			
			trackInfo += "\n`" + progressTime + " ";
			for(int i=0; i<a; i++) {
				trackInfo += "▬";
			}
			trackInfo += "🔘";
			for(int i=a+1; i<9; i++) {
				trackInfo += "▬";
			}
			trackInfo += " " + durationTime + "` `🔊 " + currentContext.getDevice().getVolume_percent() + "`";
		}
		
		embed.addField(track.getName(), trackInfo, false);
		event.getChannel().sendMessage(embed.build()).queue();
	}
	
	private void disconnect(String userId, GuildMessageReceivedEvent event) {
		try {
			daoSpotify.delete(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		event.getChannel().sendMessage(new OkAlert("Desconectado", "Sua conta do Spotify foi desconectada com sucesso. "
				+ "Se preferir, também acesse o site do Spotify e revogue acesso ao LegendaryHelper.").build()).queue();
	}
	
	private void getRecommendations(SpotifyApi spotifyApi, GuildMessageReceivedEvent event, String link) {
		CurrentlyPlayingContext currentContext = getUsersPlayback(spotifyApi, event, link);
		if(currentContext == null) {
			event.getChannel().sendMessage(new ErrorAlert("Não tem nada tocando...", "Pra gerar uma playlist de recomendações, você precisa "
					+ "estar ouvindo algo. Isso me dá inspiração para recomendar músicas para você.").build()).queue();
		}
		Track track = (Track) currentContext.getItem();
		
		String artistId = track.getArtists()[0].getId();
		GetArtistRequest getArtistRequest = spotifyApi.getArtist(artistId).build();
		Artist artist = null;
		try {
			artist = getArtistRequest.execute();
		} catch (UnauthorizedException e) {
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e2) {
			e2.printStackTrace();
		}

		GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations()
			.limit(30)
			.seed_artists(artist.getId())
			.seed_genres(artist.getGenres()[0])
			.seed_tracks(track.getId())
			.build();
		
		GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
		try {
			Recommendations recommendations = getRecommendationsRequest.execute();
			User user = getCurrentUsersProfileRequest.execute();
			CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(user.getId(), "Seu mix de recomendações").build();
			Playlist playlist = createPlaylistRequest.execute();
			String[] tracks = new String[30];
			int i = 0;
			for (TrackSimplified track1 : recommendations.getTracks()) {
				tracks[i] = track1.getUri();
				i++;
			}
			AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlist.getId(), tracks).build();
			UploadCustomPlaylistCoverImageRequest uploadCustomPlaylistCoverImageRequest = spotifyApi
				    .uploadCustomPlaylistCoverImage(playlist.getId())
				    .image_data(generatePlaylistCover())
				    .build();
			uploadCustomPlaylistCoverImageRequest.execute();
			addItemsToPlaylistRequest.execute();
		} catch (UnauthorizedException e) {
			String token = refreshToken(spotifyApi.getRefreshToken(), event);
			if(token == null) {
				event.getChannel().sendMessage("Sessão expirada ou desconectada, conecte novamente.").queue();
				event.getChannel().sendMessage(link).queue();
				return;
			} else {
				spotifyApi.setAccessToken(token);
				pause(spotifyApi, event, link);
				return;
			}
			
		} catch (ForbiddenException e) {
			event.getChannel().sendMessage(new ErrorAlert("Problema ao executar o comando",
					"O Spotify retornou a seguinte mensagem: `" + e.getMessage() + "`.").build()).queue();
			return;
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		
		event.getChannel().sendMessage("Playlist criada com recomendações baseadas em `" + track.getName() + "`. "
				+ "Confira sua lista de playlists no Spotify.").queue();
	}
	
	private String generatePlaylistCover() {
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(new Color(66, 120, 80));
		colors.add(new Color(53, 79, 97));
		colors.add(new Color(120, 40, 93));
		colors.add(new Color(145, 44, 4));
		colors.add(new Color(95, 151, 158));
		colors.add(new Color(49, 53, 54));
		
		Random random = new Random();
		int colorIndex = random.nextInt(6);
		
		BufferedImage bImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bImage.createGraphics();
		graphics.setPaint(colors.get(colorIndex));
		graphics.fillRect(0, 0, bImage.getWidth(), bImage.getHeight()); 
		
		String line1 = "Seu mix de";
		String line2 = "recomendações";
		String line3 = "LegendaryHelper";
		
		FontMetrics metrics = bImage.getGraphics().getFontMetrics(new Font("Lucida Sans", Font.BOLD, 65));
	    int width1 = metrics.stringWidth(line1);
	    int width2 = metrics.stringWidth(line2);
	    metrics = bImage.getGraphics().getFontMetrics(new Font("Lucida Sans", Font.PLAIN, 40));
	    int width3 = metrics.stringWidth(line3);
	    
		((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Lucida Sans", Font.BOLD, 65));
		graphics.drawString(line1, ((600 - width1)/2) , 270);
		graphics.setFont(new Font("Lucida Sans", Font.BOLD, 65));
		graphics.drawString(line2, ((600 - width2)/2), 340);
		graphics.setFont(new Font("Lucida Sans", Font.PLAIN, 40));
		graphics.drawString(line3, ((600 - width3)/2), 565);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImage, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return DatatypeConverter.printBase64Binary(output.toByteArray());
	}
	
	private void showHelp(GuildMessageReceivedEvent event) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("LegendaryHelper + Spotify (BETA)");
		embed.setColor(Color.BLUE);
		embed.addField("!spotify next", "Skip to next track", true);
		embed.addField("!spotify prev", "Skip to previous track", true);
		embed.addField("!spotify play", "Resume playback", true);
		embed.addField("!spotify pause", "Pause playback", true);
		embed.addField("!spotify np", "Show the current playback", true);
		embed.addField("!spotify genpl", "Generate a playlist based on currently playing track", true);
		embed.addField("!spotify disconnect", "Disconnects your Spotify account", true);
		
		event.getChannel().sendMessage(embed.build()).queue();
	}
	
}
