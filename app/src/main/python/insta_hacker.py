
import instaloader
import requests
from bs4 import BeautifulSoup

# Create an Instaloader object
L = instaloader.Instaloader()


def get_username(username):
    try:
        url = f"https://www.instagram.com/{username}/"
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        username = soup.find("meta", property="al:ios:url")["content"].split("/")[-2]
        return username

    except Exception as e:
        return str(e)

def get_full_name(username):
    try:
        url = f"https://www.instagram.com/{username}/"
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        full_name = soup.title.text.strip().split("•")[0].strip()
        return full_name
    except Exception as e:
        return str(e)

def get_bio(username):
    try:

        profile = instaloader.Profile.from_username(L.context, username)
        return profile.biography
    except Exception as e:
        return "Refresh your internet and try again"

def get_followers(username):
    try:
        profile = instaloader.Profile.from_username(L.context, username)
        return profile.followers
    except Exception as e:
        return "no"

def get_following(username):
    try:
        url = f"https://www.instagram.com/{username}/"
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        following = int(soup.find("meta", property="og:description")["content"].split(" Following")[0].split(",")[-1])
        return following
    except Exception as e:
        return str(e)

def get_posts(username):
    try:
        url = f"https://www.instagram.com/{username}/"
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        posts = int(soup.find("meta", property="og:description")["content"].split(" Posts")[0].split(",")[-1])
        return posts
    except Exception as e:
        return str(e)

def get_profile_pic_url(username):
    try:
        url = f"https://www.instagram.com/{username}/"
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        profile_pic_url = soup.find("meta", property="og:image")["content"]
        return profile_pic_url
    except Exception as e:
        return str(e)




