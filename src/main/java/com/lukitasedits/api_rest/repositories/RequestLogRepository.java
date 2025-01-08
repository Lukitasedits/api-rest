package com.lukitasedits.api_rest.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lukitasedits.api_rest.models.RequestLog;

@Repository
public interface RequestLogRepository extends CrudRepository<RequestLog, Long>{

    @Query("SELECT r FROM RequestLogs r OFFSET = :pageSize*:page LIMIT = :pageSize")
    public ArrayList<RequestLog> getRequestLogs(int pageSize, int page);
}
