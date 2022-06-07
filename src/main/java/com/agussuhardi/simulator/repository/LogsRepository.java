package com.agussuhardi.simulator.repository;

import com.agussuhardi.simulator.entity.Logs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogsRepository extends PagingAndSortingRepository<Logs, String> {

  @Query(
      value =
          "select l from Logs l where (?1 is null or l.projectName = ?1) and l.createdAt>=?2 AND l.createdAt<=?3 order by l.createdAt desc ")
  List<Logs> findAllBy(String projectName, LocalDateTime startDate, LocalDateTime endDate);
}
