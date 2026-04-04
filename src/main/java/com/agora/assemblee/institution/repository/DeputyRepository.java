package com.agora.assemblee.institution.repository;

import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.institution.model.Deputy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeputyRepository extends BaseRepository<Deputy> {

    List<Deputy> findAllByOrderByLastNameAscFirstNameAsc();

    List<Deputy> findByActiveTrueOrderByLastNameAscFirstNameAsc();

    List<Deputy> findByCurrentSessionIdOrderByLastNameAscFirstNameAsc(Long currentSessionId);

    List<Deputy> findByCurrentSessionIdAndActiveTrueOrderByLastNameAscFirstNameAsc(Long currentSessionId);

    boolean existsByDeputyNumberIgnoreCase(String deputyNumber);

    long countByActiveTrue();

    long countByParliamentaryGroupId(Long parliamentaryGroupId);

    @Query("""
            select d from Deputy d
            where lower(concat(coalesce(d.firstName, ''), ' ', coalesce(d.lastName, ''))) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(d.deputyNumber, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(d.constituency, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(d.province, '')) like lower(concat('%', :keyword, '%'))
            order by d.lastName asc, d.firstName asc
            """)
    List<Deputy> search(@Param("keyword") String keyword);

    @Query("""
            select d from Deputy d
            where d.currentSession.id = :sessionId
              and (
                   lower(concat(coalesce(d.firstName, ''), ' ', coalesce(d.lastName, ''))) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(d.deputyNumber, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(d.constituency, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(d.province, '')) like lower(concat('%', :keyword, '%'))
              )
            order by d.lastName asc, d.firstName asc
            """)
    List<Deputy> searchBySession(@Param("keyword") String keyword, @Param("sessionId") Long sessionId);
}