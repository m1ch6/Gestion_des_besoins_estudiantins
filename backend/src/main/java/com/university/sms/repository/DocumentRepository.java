package com.university.sms.repository;

import com.university.sms.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends BaseRepository<Document> {

    List<Document> findByMiniproject(Miniproject miniproject);

    List<Document> findByThesis(Thesis thesis);
}
