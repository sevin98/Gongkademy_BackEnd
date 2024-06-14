package com.gongkademy.domain.community.repository;

import com.gongkademy.domain.board.entity.pick.Pick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {
}
