package me.melijn.jda.utils;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import me.duncte123.weebJava.WeebApiBuilder;
import me.duncte123.weebJava.models.WeebApi;
import me.duncte123.weebJava.models.image.WeebImage;
import me.duncte123.weebJava.models.image.response.ImageTypesResponse;
import me.duncte123.weebJava.types.Endpoint;
import me.duncte123.weebJava.types.TokenType;
import me.melijn.jda.Melijn;
import net.dv8tion.jda.core.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {

    private final WeebApi weebApi;
    private final OkHttpClient client = new OkHttpClient();
    private SpotifyApi spotifyApi;
    private final Melijn melijn;

    private final Pattern spotifyTrackUrl = Pattern.compile("https://open.spotify.com/track/(\\S+)");
    private final Pattern spotifyTrackUri = Pattern.compile("spotify:track:(\\S+)");
    private final Pattern spotifyPlaylistUrl = Pattern.compile("https://open.spotify.com(?:/user/\\S+)?/playlist/(\\S+)");
    private final Pattern spotifyPlaylistUri = Pattern.compile("spotify:(?:user:\\S+:)?playlist:(\\S+)");
    private final Pattern spotifyAlbumUrl = Pattern.compile("https://open.spotify.com/album/(\\S+)");
    private final Pattern spotifyAlbumUri = Pattern.compile("spotify:album:(\\S+)");


    public WebUtils(Melijn melijn) {
        this.melijn = melijn;
        weebApi = new WeebApiBuilder(TokenType.WOLKETOKENS)
                .setEndpoint(Endpoint.PRODUCTION)
                .setBotInfo("Melijn", "1.0", melijn.getConfig().getValue("environment"))
                .setToken(melijn.getConfig().getValue("wolketoken"))
                .build();
        try {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(melijn.getConfig().getValue("spotifyClientId"))
                    .setClientSecret(melijn.getConfig().getValue("spotify"))
                    .build();
            ClientCredentialsRequest credentialsRequest = spotifyApi.clientCredentials().build();
            spotifyApi.setAccessToken(credentialsRequest.execute().getAccessToken());
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
    }

    public void updateSpotifyCredentials() {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(melijn.getConfig().getValue("spotifyClientId"))
                .setClientSecret(melijn.getConfig().getValue("spotify"))
                .build();
        ClientCredentialsRequest credentialsRequest = spotifyApi.clientCredentials().build();
        try {
            spotifyApi.setAccessToken(credentialsRequest.execute().getAccessToken());
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
    }


    public void getTags(Consumer<List<String>> callback) {
        weebApi.getTags().async(callback);
    }

    public void getTypes(Consumer<ImageTypesResponse> callback) {
        weebApi.getTypes().async(callback);
    }

    public String run(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            } else return "error";
        } catch (IOException ignored) {
            return "IOException";
        }
    }

    public String getCatUrl() {
        String catPage = run("http://aws.random.cat/meow");
        if (melijn.getHelpers().isJSONObjectValid(catPage) && EmbedBuilder.URL_PATTERN.matcher(new JSONObject(catPage).getString("file")).matches())
            return new JSONObject(catPage).getString("file");
        return null;
    }

    public void getImage(String type, Consumer<WeebImage> callback) {
        weebApi.getRandomImage(type).async(callback, Throwable::printStackTrace);
    }

    public void getTracksFromSpotifyUrl(String url, Consumer<Track> track, Consumer<PlaylistTrack[]> tracks, Consumer<TrackSimplified[]> tracksa, Consumer<Object> rip) {
        melijn.getTaskManager().async(() -> {
            try {

                //Tracks
                if (spotifyTrackUrl.matcher(url).matches()) {
                    track.accept(spotifyApi.getTrack(url.replaceFirst("https://open.spotify.com/track/", "").replaceFirst("\\?\\S+", "")).build().execute());
                } else if (spotifyTrackUri.matcher(url).matches()) {
                    track.accept(spotifyApi.getTrack(url.replaceFirst("spotify:track:", "").replaceFirst("\\?\\S+", "")).build().execute());

                    //Playlists
                } else if (spotifyPlaylistUrl.matcher(url).matches()) {
                    acceptTracksIfMatchesPattern(url, tracks, spotifyPlaylistUrl);
                } else if (spotifyPlaylistUri.matcher(url).matches()) {
                    acceptTracksIfMatchesPattern(url, tracks, spotifyPlaylistUri);

                    //Albums
                } else if (spotifyAlbumUrl.matcher(url).matches()) {
                    acceptIfMatchesPattern(url, tracksa, spotifyAlbumUrl);
                } else if (spotifyAlbumUri.matcher(url).matches()) {
                    acceptIfMatchesPattern(url, tracksa, spotifyAlbumUri);
                } else {
                    rip.accept(new Object());
                }
            } catch (IOException | SpotifyWebApiException ignored) {
                rip.accept(new Object());
            }
        });
    }

    private void acceptTracksIfMatchesPattern(String url, Consumer<PlaylistTrack[]> tracks, Pattern pattern) throws IOException, SpotifyWebApiException {
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            if (matcher.group(1) != null)
                tracks.accept(spotifyApi.getPlaylistsTracks(matcher.group(1).replaceFirst("\\?\\S+", "")).build().execute().getItems());
        }
    }

    private void acceptIfMatchesPattern(String url, Consumer<TrackSimplified[]> tracksa, Pattern pattern) throws IOException, SpotifyWebApiException {
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            if (matcher.group(1) != null)
                tracksa.accept(spotifyApi.getAlbumsTracks(matcher.group(1).replaceFirst("\\?\\S+", "")).build().execute().getItems());
        }
    }

    public void getImageByTag(String tag, Consumer<WeebImage> callback) {
        weebApi.getRandomImage(Collections.singletonList(tag)).async(callback);
    }

    @Nullable
    public String getAlpacaUrl() {
        String alpacaPage = run("https://apis.duncte123.me/alpaca");
        if (!melijn.getHelpers().isJSONObjectValid(alpacaPage)) return null;
        JSONObject alpacaJson = new JSONObject(alpacaPage);
        if (!alpacaJson.getBoolean("success")) return null;
        return alpacaJson.getJSONObject("data").getString("file");
    }

    @Nullable
    public String getBirdUrl() {
        String birdPage = run("https://some-random-api.ml/img/birb");
        if (!melijn.getHelpers().isJSONObjectValid(birdPage)) return null;
        if (new JSONObject(birdPage).has("link"))
            return new JSONObject(birdPage).getString("link");
        else return null;
    }
}
