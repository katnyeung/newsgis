package org.news.gis.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class HashTag {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long hashTagId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "news_id", referencedColumnName = "newsId")
	News news;

	String name;

	String type;
	
	public Long getHashTagId() {
		return hashTagId;
	}

	public void setHashTagId(Long hashTagId) {
		this.hashTagId = hashTagId;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
