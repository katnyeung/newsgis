package org.news.gis.controller;

import java.util.Map;
import java.util.Set;

import org.news.gis.entity.News;
import org.news.gis.response.CommonResponse;
import org.news.gis.response.news.NewsResponse;
import org.news.gis.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	NewsService newsService;
	
	@GetMapping("/{hours}/")
	public ResponseEntity<CommonResponse> getNewsBefore(@PathVariable("hours") int hours){
		Set<News> newsList = newsService.getNewsByHoursBefore(hours);

		newsService.processNewsList(newsList, hours);
		
		Map<String, NewsResponse> newsResponseList = newsService.convertNewsToResponse(newsList);
		
		return ResponseEntity.ok().body(CommonResponse.success(newsResponseList));
	}
	
}
