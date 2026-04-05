package com.agora.assemblee.plenary.repository;

import com.agora.assemblee.common.enums.PlenarySessionStatus;
import com.agora.assemblee.common.repository.BaseRepository;
import com.agora.assemblee.plenary.model.PlenarySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlenarySessionRepository extends BaseRepository<PlenarySession> {

    Page<PlenarySession> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<PlenarySession> findByPlenaryStatus(PlenarySessionStatus plenaryStatus, Pageable pageable);

    Page<PlenarySession> findByTitleContainingIgnoreCaseAndPlenaryStatus(
            String keyword,
            PlenarySessionStatus plenaryStatus,
            Pageable pageable
    );
}