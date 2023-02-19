# JavaTube
_JavaTube_ is a YouTube video download library based on [pytube](https://github.com/pytube/pytube) library.

_JavaTube_ is a library written in java. It has no third-party dependencies and aims to be highly reliable.

## Features
* Support for downloading the full playlist
* Support for progressive and adaptive streams
* Interaction with channels (videos, YouTube Shorts and lives)
* onProgress callback register
* Keyword search support
* Ability to get video details (Title, Description, Publish Date, Length, Thumbnail Url, Views, Author and Keywords)
* Subtitle generator for .srt format
* Support downloading yt_otf streams

# Contribution
Currently this project is maintained by only one person. Feel free to create issues with questions, bug reports or improvement ideas.

## Using JavaTube

To download videos from YouTube you need to import the YouTube class and pass a url argument like this to get access to the streams

```java
public static void main(String[] args) throws Exception {
    Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo");
    yt.streams().getHighestResolution().download("./");
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

The `results()` method will return an ArrayList with Youtube objects that can be inspected and downloaded.

```java
public static void main(String[] args) throws Exception {
    for(Youtube yt : new Search("YouTube Rewind").results()){
        System.out.println(yt.getTitle());
    }
}
```

### Interacting with channels

* `getVideos()`: method returns an ArrayList containing the channel's videos.


* `getShorts()`: method returns an ArrayList containing the channel's YouTube Shorts.


* `getLives()`: method returns an ArrayList containing the channel's lives.

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
}
```

Write to console in .srt format.

```java
public static void main(String[] args) throws Exception {
        System.out.println(new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo&t=1s").getCaptions().getByCode("en").xmlCaptionToSrt());
    }
}
```

Download it in .srt format (if the .srt format is not informed, the xml will be downloaded).

```java
public static void main(String[] args) throws Exception {
        new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo&t=1s").getCaptions().getByCode("en").download("caption.srt", "./")
    }
}
```

## Download Methods

To download you can use the methods:

* `getHighestResolution()`
* `getLowestResolution() `
* `getOnlyAudio() `

## Filters Parameters:
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
