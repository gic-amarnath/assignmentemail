package com.assignmentforemail.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.assignmentforemail.pojo.RootMailConfig;

@Repository
public interface EmailRepository extends JpaRepository<RootMailConfig, Integer> {
	//	@Query("select a from RootMailConfig a where a.creationDate <= creationDate")
	//    List<RootMailConfig> findByEmailDate(@Param("emailDate") LocalDate date);

	@Query(value="select * from email_data a WHERE a.creation_date  >= ?1",nativeQuery = true)
	List<RootMailConfig> findByEmailDate(@Param ("emailDate") LocalDate date);

	@Query("SELECT u FROM RootMailConfig u WHERE " + "lower(u.subject) LIKE lower(CONCAT('%', :bySubject, '%'))")
	List<RootMailConfig> findBySubject(@Param ("bySubject") String subject);

	//	@Query(value="select u.* from{h-schema} email_data u where u.email_to  like %?1% ",nativeQuery = true)
	//	List<RootMailConfig> findByEmailId(@Param ("byEmail") String byEmail);

}
