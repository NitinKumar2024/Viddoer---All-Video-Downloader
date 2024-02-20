import instaloader

def download_posts(username):
    L = instaloader.Instaloader()
    try:
        profile = instaloader.Profile.from_username(L.context, username)
        posts = profile.get_posts()

        downloaded_posts = []
        for post in posts:
            downloaded_posts.append(post.url)

        return downloaded_posts

    except instaloader.exceptions.ProfileNotExistsException:
        return "wrong"
    except instaloader.exceptions.PrivateProfileNotFollowedException:
        return "wrong"
    except Exception as e:
        return "wrong"


def download_caption(username):
    L = instaloader.Instaloader()
    try:
        profile = instaloader.Profile.from_username(L.context, username)
        posts = profile.get_posts()

        downloaded_captions = []
        for post in posts:
            if post.caption:
                downloaded_captions.append(post.caption)

        return downloaded_captions

    except instaloader.exceptions.ProfileNotExistsException:
        return "Error: Profile not found."
    except instaloader.exceptions.PrivateProfileNotFollowedException:
        return "Error: This is a private profile, and you are not following it."
    except Exception as e:
        return f"Error: {str(e)}"

def download_video(username):
    L = instaloader.Instaloader()
    try:
        profile = instaloader.Profile.from_username(L.context, username)
        posts = profile.get_posts()

        downloaded_posts = []
        for post in posts:
            if post.is_video:  # Check if the post is a video
                downloaded_posts.append(post.video_url)

        return downloaded_posts

    except instaloader.exceptions.ProfileNotExistsException:
        return "wrong"
    except instaloader.exceptions.PrivateProfileNotFollowedException:
        return "wrong"
    except Exception as e:
        return "wrong"


def status(username):

    try:
        user = "hackerpython2023"
        password = "844102"

        import instaloader

        L = instaloader.Instaloader()

        downloaded_posts = []

        L.login(user, password)
        user_id = L.check_profile_id(username).userid
        for story in L.get_stories(userids=[user_id]):
            for item in story.get_items():
                print(f"Hosting link for the story: {item.video_url}")
                downloaded_posts.append(item.video_url)
                # L.download_storyitem(item, 'nitin_kumar_2023')
                return downloaded_posts
    except Exception as e:
        print(str(e))
        return "wrong"