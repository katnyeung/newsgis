package org.news.gis.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.news.gis.DAO.NewsDAO;
import org.news.gis.entity.GeoLocation;
import org.news.gis.entity.News;
import org.news.gis.entity.NewsHistory;
import org.news.gis.response.news.NewsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	NewsDAO newsDAO;

	public Set<News> getNewsByHoursBefore(int hours) {
		Integer to = hours * 60 * 60;
		Integer from = to + (6 * 60 * 60);

		Set<News> newsList = newsDAO.getNewsBySecondsBefore(from, to);

		return newsList;
	}

	public Map<String, NewsResponse> convertNewsToResponse(Set<News> processingNewsList) {
		// do grouping
		Map<String, NewsResponse> nrMap = new HashMap<String, NewsResponse>();

		for (News news : processingNewsList) {
			String rndColor = getRandomColor(news.getUniqueId());
			
			GeoLocation center = getCenterLocation(news);
			String key = "";
			
			if(center == null) {
				//default location in center
				key = "12.7260:214.9804";
				
				center = new GeoLocation();
				center.setLat(12.7260);
				center.setLng(214.9804);
				center.setSeq(0);
				center.setCity("Unknown");
				
			}else {
				key = String.format("%.4f", center.getLat()) + ":" + String.format("%.4f", center.getLng());
			}
			
			NewsResponse newsResponse = nrMap.get(key);

			if (newsResponse == null) {
				newsResponse = new NewsResponse();
				Set<GeoLocation> centerToList = new HashSet<GeoLocation>();
				List<News> centerNewsList = new ArrayList<News>();

				newsResponse.setCenter(center);
				newsResponse.setToList(centerToList);
				newsResponse.setNewsList(centerNewsList);

				nrMap.put(key, newsResponse);
			}
			
			// find duplicate
			// ignore center
			for (GeoLocation newsLocation : news.getGeoLocationList()) {
				boolean found = false;
				
				if (newsLocation.getCity().equals(center.getCity())) {
					found = true;
				}
				
				for (GeoLocation savedLocation : newsResponse.getToList()) {
					if (newsLocation.getCity().equals(savedLocation.getCity())) {
						found = true;
					}
				}

				if (!found) {
					newsLocation.setColor(rndColor);
					newsResponse.getToList().add(newsLocation);
				}
			}
			
			news.setGeoLocationList(null);
			news.setColor(rndColor);
			newsResponse.getNewsList().add(news);

		}
		
		// back fill news color
		for(Entry<String, NewsResponse> entry : nrMap.entrySet()) {
			NewsResponse nr = entry.getValue();
			nr.getNewsList().sort(Comparator.comparing(News::getNewsDate).reversed());
			
			for(News news : nr.getNewsList()) {
				boolean haveDestination = false;
				for(GeoLocation loc : nr.getToList()) {
					if(loc.getColor().equals(news.getColor())) {
						haveDestination = true;
					}
				}
				
				if(!haveDestination) {
					news.setColor("003366");
				}
			}
		}

		return nrMap;

	}

	private String getRandomColor(String str) {
		int hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + ((hash << 5) - hash);
		}
		String colour = "#";
		for (int i = 0; i < 3; i++) {
			int value = (hash >> (i * 8)) & 0xFF;
			colour += ("00" + Integer.toHexString(value));
		}
		return colour.substring(3, 9).toUpperCase();
	}

	public GeoLocation getCenterLocation(News news) {
		GeoLocation center = null;

		Set<GeoLocation> geoLocationSet = news.getGeoLocationList();
		for (GeoLocation geoLocation : geoLocationSet) {
			if (geoLocation.getNounType() != null && (geoLocation.getNounType().equals("nsubj") || geoLocation.getNounType().equals("ROOT"))) {
				center = geoLocation;
			}
		}

		if (center == null) {
			if (news.getGeoLocationList().iterator().hasNext()) {
				center = news.getGeoLocationList().iterator().next();
			}
		}

		return center;
	}

	public void processNewsList(Set<News> newsList, int hours) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR_OF_DAY, -1 * hours);
		Long currentTime = now.getTimeInMillis();

		Calendar back24hr = Calendar.getInstance();
		back24hr.add(Calendar.HOUR_OF_DAY, -24 - hours);
		Long back24hrTime = back24hr.getTimeInMillis();

		for (News news : newsList) {
			Set<NewsHistory> newsHistoryList = news.getNewsHistoryList();

			for (NewsHistory nh : newsHistoryList) {
				if (nh.getUps() > 0) {
					news.setUps(nh.getUps());
					news.setNumComments(nh.getNumComments());
					break;
				}
			}
		}

		LongSummaryStatistics stats = newsList.stream().filter(entry -> entry.getUps() != null).mapToLong(News::getUps).summaryStatistics();

		// calculate the scale
		double sd = 0;
		for (News news : newsList) {
			if (news.getUps() != null)
				sd += Math.pow(news.getUps() - stats.getAverage(), 2);
		}
		sd = Math.sqrt(sd / (stats.getCount() - 1));

		for (News news : newsList) {
			if (news.getUps() != null) {
				double z = (news.getUps() - stats.getAverage()) / sd;
				news.setScale((1 + erf(z)) / 2);
			} else {
				news.setScale(0.2);
			}

			// calculate the opacity
			double opacity = (currentTime * 1.0 - news.getNewsDate().getTime() * 1.0) / (currentTime * 1.0 - back24hrTime * 1.0);

			if (1 - opacity < 0) {
				news.setOpacity(0.0);
			} else {
				news.setOpacity(1 - opacity);
			}

		}

	}

	public static double erf(double z) {
		double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

		// use Horner's method
		double ans = 1 - t * Math.exp(-z * z - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 + t * (0.17087277))))))))));
		if (z >= 0)
			return ans;
		else
			return -ans;
	}

}
