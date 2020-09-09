package org.news.gis.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "news", indexes = { @Index(name = "news_range", columnList = "status,createdate") })
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	Long newsId;

	@JsonProperty("id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String uniqueId;

	@JsonProperty("url")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String url;

	@JsonProperty("title")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String title;

	@JsonIgnore
	Date createdate;

	@JsonIgnore
	Date lastupdatedate;

	@JsonIgnore
	String status;

	@Transient
	Double scale;

	@Transient
	Long ups;

	@Transient
	Long numComments;

	@Transient
	Double opacity;

	@JsonIgnore
	String domain;

	String source;

	@Transient
	String color;

	@JsonProperty("date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+8")
	Date newsDate;

	@JsonProperty("hash")
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
	@OrderBy("name ASC")
	Set<HashTag> hashTagList;

	@JsonIgnore
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
	@OrderBy("newsHistoryId DESC")
	Set<NewsHistory> newsHistoryList;

	@JsonProperty("position")
	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
	@OrderBy("seq ASC")
	Set<GeoLocation> geoLocationList;

	public Long getNewsId() {
		return newsId;
	}

	public void setNewsId(Long newsId) {
		this.newsId = newsId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Date getLastupdatedate() {
		return lastupdatedate;
	}

	public void setLastupdatedate(Date lastupdatedate) {
		this.lastupdatedate = lastupdatedate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getNewsDate() {
		return newsDate;
	}

	public void setNewsDate(Date newsDate) {
		this.newsDate = newsDate;
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

	public Set<HashTag> getHashTagList() {
		return hashTagList;
	}

	public void setHashTagList(Set<HashTag> hashTagList) {
		this.hashTagList = hashTagList;
	}

	public Set<NewsHistory> getNewsHistoryList() {
		return newsHistoryList;
	}

	public void setNewsHistoryList(Set<NewsHistory> newsHistoryList) {
		this.newsHistoryList = newsHistoryList;
	}

	public Set<GeoLocation> getGeoLocationList() {
		return geoLocationList;
	}

	public void setGeoLocationList(Set<GeoLocation> geoLocationList) {
		this.geoLocationList = geoLocationList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
