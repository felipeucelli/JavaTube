# JavaTube 
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/felipeucelli/JavaTube/gradle.yml)

[![JDK](https://img.shields.io/badge/JDK-17%2B-blue.svg)](https://www.oracle.com/java/technologies/downloads/#java17) 
[![](https://jitpack.io/v/felipeucelli/javatube.svg)](https://jitpack.io/#felipeucelli/javatube) 

_JavaTube_ is a YouTube video download library based on [pytube](https://github.com/pytube/pytube) library.

_JavaTube_ is a library written in java and aims to be highly reliable.

## Features
* Support for downloading the full playlist
* Support for progressive and adaptive streams
* Interaction with channels (Videos, YouTube Shorts, lives and Playlists)
* onProgress callback register
* Keyword search support
* Search using filters
* Ability to get video details (Title, Description, Publish Date, Length, Thumbnail Url, Views, Author and Keywords)
* Subtitle generator for .srt format
* Support downloading yt_otf streams
* Possibility to choose the client (WEB, ANDROID, IOS)
* Native js interpreter
* PoToken support

## Contribution
Currently this project is maintained by only one person. Feel free to create issues with questions, bug reports or improvement ideas.

## WARNING
This code is for educational purposes and must not be used in any way for commercial purposes.

Downloading videos from YouTube without proper authorization may violate the [terms of the platform](https://www.youtube.com/static?template=terms).

Downloading copyrighted videos may infringe on the creators' intellectual property.

I reaffirm not to use this software to violate any laws.

## Using JavaTube

To download YouTube videos you need to import the YouTube class and pass a YouTube video url to access the streams.

The streams() method returns a StreamQuery object that lists the properly handled streams.

You must only get one stream to be able to download it. You can use the methods: 

* `getHighestResolution()`
* `getLowestResolution() `
* `getOnlyAudio() `

You can also manually select the stream using `.get("index")`.

The download() method must receive the path that the stream will be downloaded.

```java
public static void main(String[] args) throws Exception {
    Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo");
    yt.streams().getHighestResolution().download("./");
}
```

or

```java
public static void main(String[] args) throws Exception {
        new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo").streams().get(1).download("./");
    }
}
```

### Downloading videos with multiple audio tracks
Videos with multiple progressive audio tracks come with the original audio, which is why we must choose the adaptive types.

Because the dubbed audio tracks have the same tag, we have to filter by name.

This will only list tracks dubbed in the chosen language:

```java
public static void main(String[] args) throws Exception {
    for(Stream s : new Youtube("https://www.youtube.com/watch?v=g_VxOIlg7q8").streams().getExtraAudioTracksByName("English").getAll()){
        System.out.println(s.getItag() + " " + s.getAudioTrackName() + " " + s.getAbr() + " " + s.getUrl());
    }
}
```
You can check the dubbed tracks using:

```java
public static void main(String[] args) throws Exception {
    for(Stream s : new Youtube("https://www.youtube.com/watch?v=g_VxOIlg7q8").streams().getExtraAudioTracks().getAll()){
        System.out.println(s.getItag() + " " + s.getAudioTrackName() + " " + s.getAbr() + " " + s.getUrl());
    }
}
```

### Download using filters 

You must pass a HashMap String with the filter you want to use and its respective value

```java
public static void main(String[] args) throws Exception {
    Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo");

    HashMap<String, String> filters = new HashMap<>();
    filters.put("progressive", "true");
    filters.put("subType", "mp4");

    yt.streams().filter(filters).getFirst().download("./");
    
}
```

### Download with callback function

If no parameter is passed, a download percentage string will be printed to the terminal
```java
public static void progress(Long value){
    System.out.println(value);
}

public static void main(String[] args) throws Exception {
    Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo");
    yt.streams().getHighestResolution().download("./", Download::progress);
}
```

### Downloading a playlist

The `getVideos()` method will return an ArrayList with the links extracted from the playlist url (YouTube mix not supported)

```java
 public static void main(String[] args) throws Exception {
    for(String pl : new Playlist("https://www.youtube.com/playlist?list=PLS1QulWo1RIbfTjQvTdj8Y6yyq4R7g-Al").getVideos()){
        new Youtube(pl).streams().getHighestResolution().download("./");
    }
}
```

### Using the search feature

* `getResults()`: method will return an ArrayList with links to videos, shorts, playlists and channels.


* `getVideoResults()`: method returns an ArrayList of Youtube objects, containing videos.


* `getShortsResults()`: method returns an ArrayList of Youtube objects, containing YouTube Shorts.


* `getChannelsResults()`: method returns an ArrayList of Channel objects, containing the channels.


*  `getPlaylistsResults()`: method returns an ArrayList of Playlist objects, containing the playlists.


* `getCompletionSuggestions()`: method returns a list containing search suggestions.


*  `generateContinuation()`: method will not return anything, just add the continuation of the items to their respective lists.


If no match was found the method will return empty, other than `getCompletionSuggestions()` which returns null.
```java
public static void main(String[] args) throws Exception {
    for(String yt : new Search("Java").getResults()){
        System.out.println(yt);
    }
}
```

### Search using filters

YouTube allows you to filter a search just by passing a parameter encoded in protobuf. 
This parameter consists of dictionaries where each key and value represents a category and filter respectively.
With these parameters we can combine several filters to create a personalized search.

It wouldn't be very practical for the user or developer to have to manually retrieve the custom filter from YouTube whenever they want to do a search, 
so the `FilterBuilder` class will do all the work of providing all the available filters, combining them, coding them in protobuf and send to the `Search` class, 
all we need to do is import it and create a dictionary with the necessary filters:

```java
public static void main(String[] args) throws Exception {
    Map<FilterBuilder.Filter, Object> filter = new HashMap<>();
    
    filter.put(FilterBuilder.Filter.TYPE, FilterBuilder.Type.VIDEO);
    filter.put(FilterBuilder.Filter.UPLOAD_DATE, FilterBuilder.UploadDate.TODAY);
    filter.put(FilterBuilder.Filter.DURATION, FilterBuilder.Duration.UNDER_4_MIN);
    filter.put(FilterBuilder.Filter.FEATURES, List.of(FilterBuilder.Feature.CREATIVE_COMMONS, FilterBuilder.Feature._4K));
    filter.put(FilterBuilder.Filter.SORT_BY, FilterBuilder.SortBy.UPLOAD_DATE);

    Search s = new Search("music", filter);
    System.out.println(s.getResults());
}
```
This will return all videos published *today* in *4K* and *Creative Commons* *under 4 minutes* organized by *upload date*.

Note that the FEATURES category is the only one that supports combining several filters, you can send a list with all the necessary filters or just a single filter like the other categories.

### Interacting with channels

* `getVideos()`: method returns an ArrayList containing the channel's videos.


* `getShorts()`: method returns an ArrayList containing the channel's YouTube Shorts.


* `getLives()`: method returns an ArrayList containing the channel's lives.


* `getPlaylists()`: method returns an ArrayList containing the channel's playlists.


```java
public static void main(String[] args) throws Exception {
    for(String c : new Channel("https://www.youtube.com/channel/UCmRtPmgnQ04CMUpSUqPfhxQ").getVideos()){
        System.out.println(new Youtube(c).getTitle());
    }
}
```

### Using the subtitles feature

See available languages.

```java
public static void main(String[] args) throws Exception {
    for(Captions caption: new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo&t=1s").getCaptionTracks()){
        System.out.println(caption.getCode());
        }
    }
```

Write to console in .srt format.

```java
public static void main(String[] args) throws Exception {
        System.out.println(new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo&t=1s").getCaptions().getByCode("en").xmlCaptionToSrt());
    }
```

Download it in .srt format (if the .srt format is not informed, the xml will be downloaded).

```java
public static void main(String[] args) throws Exception {
        new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo&t=1s").getCaptions().getByCode("en").download("caption.srt", "./")
    }
```

## PoToken
The proof of origin (PO) token is a parameter that YouTube requires to be sent with video playback requests from some clients. Without it, format URL requests from affected customers may return HTTP error 403, error with bot detection, or result in your account or IP address being blocked.

This token is generated by BotGuard (Web) / DroidGuard (Android) to attest the requests are coming from a genuine client.

### Manually acquiring a PO Token from a browser for use when logged out
This process involves manually obtaining a PO token generated from YouTube in a web browser and then manually passing it to JavaTube via the usePoToken=True argument. Steps:

1. Open a browser and go to any video on YouTube Music or YouTube Embedded (e.g. https://www.youtube.com/embed/2lAe1cqCOXo). Make sure you are not logged in to any account!

2. Open the developer console (F12), then go to the "Network" tab and filter by v1/player

3. Click the video to play and a player request will appear in the network tab

4. In the request payload JSON, find the PO Token at serviceIntegrityDimensions.poToken and save that value

5. In the request payload JSON, find the visitorData at context.client.visitorData and save that value

6. In your code, pass the parameter usePoToken=True, to send the visitorData and PoToken:

```java
public static void main(String[] args) throws Exception {
    Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo", true);
    yt.streams().getHighestResolution().download("./");
}
```
The terminal will ask you to insert the tokens.

If you want to save the token in cache, just add one more argument `true`, this will create a _tokens.json_ file where the visitorData and poToken will be stored.

The _tokens.json_ will be created in the temporary folder of your operating system, you can delete it using: `Youtube.resetCache();`.

## Stream filters Parameters:
* `"res"` The video resolution (e.g.: "360p", "720p")
            

* `"fps"` The frames per second (e.g.: "24fps", "60fps")


* `"mineType"` Two-part identifier for file formats and format contents composed of a “type”, a “subtype” (e.g.: "video/mp4", "audio/mp4")


* `"type"` Type part of the mineType (e.g.: audio, video)


* `"subType"` Sub-type part of the mineType (e.g.: mp4, webm)


* `"abr"` Average bitrate  (e.g.: "128kbps", "192kbps")


* `"videoCodec"` Video compression format


* `"audioCodec"` Audio compression format


* `"onlyAudio"` Excludes streams with video tracks (e.g.: "true" or "false")


* `"onlyVideo"` Excludes streams with audio tracks (e.g.: "true" or "false")


* `"progressive"` Excludes adaptive streams (one file contains both audio and video tracks) (e.g.: "true" or "false")


* `"adaptive"` Excludes progressive streams (audio and video are on separate tracks) (e.g.: "true" or "false")
