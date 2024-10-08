package com.gongkademy.domain.community.common.repository;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.common.entity.pick.Pick;
import com.gongkademy.domain.community.common.entity.pick.PickType;
import com.gongkademy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {

    // 보드와 멤버와 픽타입 불러오기
    Optional<Pick> findByBoardAndMemberAndPickType(Board board, Member member, PickType pickType);

    Optional<Pick> findByBoardArticleIdAndMemberAndPickType(Long articleId, Member member, PickType pickType);

    List<Pick> findAllByMemberAndPickType(Member member, PickType pickType);
}
