# JavaTube
JavaTube is a YouTube video download utility that is based on python's pytube library

## Using JavaTube

To download videos from YouTube you need to import the YouTube class and pass a url argument like this to get access to the streams

```java
 public static void main(String[] args) throws Exception {
        new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo").streams().getHighestResolution().download("./");
    }
```
or

```java
 public static void main(String[] args) throws Exception {
        Youtube yt = new Youtube("https://www.youtube.com/watch?v=2lAe1cqCOXo");
        yt.streams().getHighestResolution().download("./");
    }
```

## Download Methods

To download you can use the methods:
* getHighestResolution()
* getLowestResolution() 
* getOnlyAudio() 
