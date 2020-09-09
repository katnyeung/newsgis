package org.news.gis.DAO;

import java.util.Set;

import org.news.gis.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsDAO extends JpaRepository<News, Long> {

	// UNIX_TIMESTAMP(nh.createdate) < UNIX_TIMESTAMP(now() - :seconds) AND
	@Query("SELECT DISTINCT n FROM News n JOIN FETCH n.newsHistoryList nh LEFT JOIN FETCH n.hashTagList h JOIN FETCH n.geoLocationList g WHERE n.status = 'A' AND (UNIX_TIMESTAMP(now()) - :from) < UNIX_TIMESTAMP(n.newsDate) AND UNIX_TIMESTAMP(n.newsDate) < (UNIX_TIMESTAMP(now()) - :to)  ORDER BY n.newsDate ASC")
	Set<News> getNewsBySecondsBefore(@Param("from") long from, @Param("to") long to);
}
