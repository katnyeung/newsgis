import spacy
import urllib.request, json 
import jmespath
import pandas as pd
import numpy as np
import mysql.connector
import datetime
from datetime import timezone

nlp = spacy.load("en_core_web_sm")

cities = pd.read_csv("worldcities.csv")
presidents = pd.read_csv("president.csv")
countryAdj = pd.read_csv("country_adjective.csv")

connection = mysql.connector.connect(host='localhost', database='newsgis', user='chung', password='1428')
cursor = connection.cursor()

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
    
def process_one_entity(newsId, entity, seq, processedWord, noun_chunks):
    city = None
    text = entity.text.lower()
    noun_type = None
    
    #get noun type
    for chunk in noun_chunks :
        if chunk.root.text.lower().find(text) != -1 :
            noun_type = chunk.root.dep_
            break
        
    # replace adjective to country
    if countryAdj["adjective"].str.lower().eq(text).any() :
        text = countryAdj[countryAdj["adjective"].str.lower().eq(text)].iloc[0]["country"].lower()

    # replace president name to country
    for i,president in presidents.iterrows() :
        if president["name"].lower() == text :
            print(" - match president", president["country"])
            text = president["country"].lower()
               
    if cities["city_ascii"].str.lower().eq(text).any() :
        city = cities[cities["city_ascii"].str.lower().eq(text)].iloc[0]
    else :
        if cities["country"].str.lower().eq(text).any() :
            if not np.isnan(cities[cities["country"].str.lower().eq(text)]["population"].idxmax()) :
                city = cities.loc[cities[cities["country"].str.lower().eq(text)]["population"].idxmax()]
        else :
            if cities["admin_name"].str.lower().eq(text).any() :
                if not np.isnan(cities[cities["admin_name"].str.lower().eq(text)]["population"].idxmax()) :
                    city = cities.loc[cities[cities["admin_name"].str.lower().eq(text)]["population"].idxmax()]
                
    if city is not None :
        print(" -> ", city["city_ascii"])
        if (city["city_ascii"] not in processedWord) and (city["country"] not in processedWord) :
            seq = save_city(newsId, entity.start_char, city, seq, noun_type)
            processedWord.append(city["city_ascii"])
            processedWord.append(city["country"])
            
    return seq

def save_news_history(newsId, ups, comments):
    data = (newsId, ups, comments,)
    cursor.execute("INSERT INTO news_history (news_id, ups, num_comments, createdate) VALUES (%s,%s,%s,now())", data)

def save_reddit(rs):
    cursor.execute("SELECT news_id FROM news WHERE unique_id = %s" , (rs[0],))
    record = cursor.fetchone()
    if cursor.rowcount == 0 :
        data = (rs[0], rs[1], rs[2], 'A', rs[5], datetime.datetime.fromtimestamp(rs[6],tz=timezone.utc), 'reddit',)
        cursor.execute("INSERT INTO news (unique_id, title, url, status, domain, news_date, source, createdate, lastupdatedate) VALUES (%s,%s,%s,%s,%s,%s,%s,now(),now())", data)
        newsId = cursor.lastrowid
        
        save_news_history(newsId, rs[3], rs[4],)
        
        return newsId
    else:    
        # insert / update history
        data = (record[0],)
        cursor.execute("SELECT nh.news_history_id FROM news_history nh WHERE nh.news_id = %s AND UNIX_TIMESTAMP(now()) < (UNIX_TIMESTAMP(nh.createdate) + (10 * 60))", data)
        cursor.fetchone()
        if cursor.rowcount == 0 :
            save_news_history(record[0], rs[3], rs[4],)

        return None
        
with urllib.request.urlopen("https://www.reddit.com/r/worldnews/.json?limit=100") as url:
    data = json.loads(url.read().decode())
    reddit_data = jmespath.search("data.children[*].data.[id,title,url,ups,num_comments,domain,created]", data)
    for rs in reddit_data:
        
        newsId = save_reddit(rs)
        
        if newsId is not None:
            print(rs[1])
            doc = nlp(rs[1])
            processedWord = []
            seq = 1
            print([entity.text for entity in doc.ents])
            print([entity.text for entity in doc.noun_chunks])
            
            for entity in doc.ents:
                seq = process_one_entity(newsId, entity, seq, processedWord, doc.noun_chunks)

            for chunk in doc.noun_chunks:
                save_hash(newsId, chunk)

connection.commit()
cursor.close()
connection.close()