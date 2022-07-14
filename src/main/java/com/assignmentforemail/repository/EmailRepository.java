package com.assignmentforemail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignmentforemail.pojo.RootMailConfig;

@Repository
public interface EmailRepository extends JpaRepository<RootMailConfig, Integer> {

}
