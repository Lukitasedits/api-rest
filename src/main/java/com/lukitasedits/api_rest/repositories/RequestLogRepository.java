package com.lukitasedits.api_rest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lukitasedits.api_rest.entities.RequestLog;

import java.util.Optional;


@Repository
public interface RequestLogRepository extends PagingAndSortingRepository<RequestLog, Long> {

    @Query("SELECT r FROM RequestLog r")
    Page<RequestLog> getRequestLogs(Pageable pageable);

    RequestLog save(RequestLog requestLog);
}