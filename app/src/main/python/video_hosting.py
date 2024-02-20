from pytube import YouTube, Playlist
import instaloader


def downloader(video_link):
    try:
        # Create an instance of the Instaloader class
        loader = instaloader.Instaloader()

        # Get the post object for the video
        post = instaloader.Post.from_shortcode(loader.context, video_link.split("/")[-2])

        if post:
            hosting = post.video_url
            return hosting
        else:
            return "wrong"
    except Exception as e:
        return "wrong"

def get_video_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.get_highest_resolution()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"


def get_144p_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.filter(res='144p', file_extension='mp4').first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"

def get_240p_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.filter(res='240p', file_extension='mp4').first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"


def get_360p_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.filter(res='360p', file_extension='mp4').first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"

def get_720p_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.filter(res='720p', file_extension='mp4').first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"

def get_1080p_hosting_link(video_link):
    try:
        yt = YouTube(video_link)
        stream = yt.streams.filter(res='1080p', file_extension='mp4').first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"

def get_music_hosting_link(music_link):
    try:
        yt = YouTube(music_link)
        stream = yt.streams.filter(only_audio=True).first()
        if stream:
            hosting_link = stream.url
            return hosting_link
        else:
            return "wrong"
    except Exception as e:
        return "wrong"


def get_title_youtube(video_link):
    try:
        # Create a YouTube object with the video link
        yt = YouTube(video_link)

        # Get the available caption tracks
        caption_tracks = yt.title

        if caption_tracks:
            # Select the first available caption track (you can modify this logic as needed)

            return caption_tracks
        else:
            return "No captions available for this video."

    except Exception as e:
        # Handle exceptions here, if needed
        return "wrong"


url_list = []

def download_playlist_audio(url):
    if "playlist" in url.lower():
        playlist = Playlist(url)
        for video in playlist.videos:
            audio_stream = video.streams.filter(only_audio=True).first()

            url_list.append(audio_stream.title + audio_stream.url)
        return url_list
    else:
        yt = YouTube(url)
        audio_stream = yt.streams.filter(only_audio=True).first()
        url_list.append(audio_stream.url)
        return url_list
