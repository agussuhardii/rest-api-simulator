package com.agussuhardi.simulator.repository;

import com.agussuhardi.simulator.entity.Rest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

public interface RestRepository extends JpaRepository<Rest, String> {

  @Query(value = "select r from Rest r where r.pathUrl=?1 and r.method=?2 and r.projectName=?3")
  Optional<Rest> findByPathUrl(String pathUrl, RequestMethod method, String projectName);

  @Query(
          value =
                  "select r from Rest r where (?1 is null or r.projectName = ?1) order by r.createdAt desc ")
  List<Rest> findAllByProjectName(String projectName);

  Optional<Rest> findByIdAndPathUrlAndMethod(String id, String pathUrl, RequestMethod method);
}
