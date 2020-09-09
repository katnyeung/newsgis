package org.news.gis.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class NewsHistory {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long newsHistoryId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "news_id", referencedColumnName = "newsId")
	News news;

	@JsonIgnore
	Long ups;

	@JsonIgnore
	Long numComments;

	@JsonIgnore
	Date createdate;

	public Long getNewsHistoryId() {
		return newsHistoryId;
	}

	public void setNewsHistoryId(Long newsHistoryId) {
		this.newsHistoryId = newsHistoryId;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public Long getUps() {
		return ups;
	}

	public void setUps(Long ups) {
		this.ups = ups;
	}

	public Long getNumComments() {
		return numComments;
	}

	public void setNumComments(Long numComments) {
		this.numComments = numComments;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

}
