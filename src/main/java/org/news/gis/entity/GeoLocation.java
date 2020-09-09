package org.news.gis.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class GeoLocation {

	@Id
	@JsonProperty("id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long geoLocationId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "news_id", referencedColumnName = "newsId")
	News news;

	String city;
	String country;

	Double lat;
	Double lng;

	int seq;

	@JsonIgnore
	int textPosition;

	@JsonProperty("type")
	String nounType;

	@Transient
	String color;

	public Long getGeoLocationId() {
		return geoLocationId;
	}

	public void setGeoLocationId(Long geoLocationId) {
		this.geoLocationId = geoLocationId;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getTextPosition() {
		return textPosition;
	}

	public void setTextPosition(int textPosition) {
		this.textPosition = textPosition;
	}

	public String getNounType() {
		return nounType;
	}

	public void setNounType(String nounType) {
		this.nounType = nounType;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
