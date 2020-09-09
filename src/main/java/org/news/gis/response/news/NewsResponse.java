package org.news.gis.response.news;

import java.util.List;
import java.util.Set;

import org.news.gis.entity.GeoLocation;
import org.news.gis.entity.News;

public class NewsResponse {
	GeoLocation center;

	List<News> newsList;

	Set<GeoLocation> toList;

	public GeoLocation getCenter() {
		return center;
	}

	public void setCenter(GeoLocation center) {
		this.center = center;
	}

	public List<News> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<News> newsList) {
		this.newsList = newsList;
	}

	public Set<GeoLocation> getToList() {
		return toList;
	}

	public void setToList(Set<GeoLocation> toList) {
		this.toList = toList;
	}

}
