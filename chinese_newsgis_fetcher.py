import requests
import pandas as pd
import spacy
import mysql.connector
import datetime
from datetime import timezone
from bs4 import BeautifulSoup

nlp = spacy.load("zh_core_web_sm")

df = pd.read_csv("worldcities2.csv")

connection = mysql.connector.connect(host='localhost', database='newsgis', user='chung', password='1428')
cursor = connection.cursor()

def save_news(uniqueId, title, url, newsDate):
    cursor.execute("SELECT news_id FROM news WHERE unique_id = %s" , (uniqueId,))
    record = cursor.fetchone()
    if cursor.rowcount == 0 :
        data = (uniqueId, title, url, 'A', 'baidu.com', newsDate, 'baidu',)
        print(data)
        cursor.execute("INSERT INTO news (unique_id, title, url, status, domain, news_date, source, createdate, lastupdatedate) VALUES (%s,%s,%s,%s,%s,%s,%s,now(),now())", data)
        newsId = cursor.lastrowid
        
        save_news_history(newsId, 0, 0,)
        
        return newsId
    else:
        return None

def save_news_history(newsId, ups, comments):
    data = (newsId, ups, comments,)
    cursor.execute("INSERT INTO news_history (news_id, ups, num_comments, createdate) VALUES (%s,%s,%s,now())", data)

def save_city(newsId, pos, city, seq, noun_type):
    data = (city["city_ascii"], city["country"], city["lat"], city["lng"], seq, newsId, pos, noun_type)
    print(data)
    cursor.execute("INSERT INTO geo_location (city, country ,lat, lng, seq, news_id, text_position, noun_type) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)", data)
    return seq + 1

def save_hash(newsId, chunk):
    text = chunk.text
    text = text.replace('"', '')
    text = text.replace("'", "")
    text = text.replace(" ", "_")
    
    data = (text, chunk.root.dep_, newsId,)
    cursor.execute("INSERT INTO hash_tag (name, type, news_id) VALUES (%s, %s, %s)", data)

def getWeb(web):

    headers = {
     'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
    }
    rs = requests.Session()
    subRs = requests.Session()

    res = rs.get(web, headers=headers)

    soup = BeautifulSoup(res.text, 'html.parser')
    items = soup.select('.ulist li')
    for item in items:
        url = item.select('a')[0].attrs['href']
        uniqueId = url.replace('http://baijiahao.baidu.com/s?id=','')

        print(url , " " , item.select('a')[0].text)
        subRes = subRs.get(url, headers=headers)
        subSoap = BeautifulSoup(subRes.text, 'html.parser')

        subItems = subSoap.select('title')
        text = subItems[0].text
        
        if subSoap.select('head > meta[itemprop=dateUpdate]'):
            dateString = subSoap.select('head > meta[itemprop=dateUpdate]')[0]["content"]
            newsDate = datetime.datetime.strptime(dateString, '%Y-%m-%d %H:%M:%S')
        else :
            newsDate = datetime.datetime.now()
        

        newsId = save_news(uniqueId, text, url, newsDate)

        if newsId is not None:
            processedCity = []
            seq = 1

            doc = nlp(subItems[0].text)

            print([entity.text for entity in doc.ents])

            for index, city in df.iterrows() :
                for entity in doc.ents :
                    noun_type = None

                    #get noun type
                    for chunk in doc.noun_chunks :
                        if chunk.root.text.lower().find(text) != -1 :
                            noun_type = chunk.root.dep_
                            break

                    if entity.text.find(city['chinese']) >= 0:
                        if (city["city_ascii"] not in processedCity) and (city["country"] not in processedCity) :
                            processedCity.append(city["city_ascii"])
                            processedCity.append(city["country"])

                            seq = save_city(newsId, entity.start_char, city, seq, noun_type)

            for chunk in doc.ents:
                save_hash(newsId, chunk)

#getWeb('http://news.baidu.com/guoji')

getWeb('http://news.baidu.com/mil')

connection.commit()
cursor.close()
connection.close()