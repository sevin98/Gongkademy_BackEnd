package com.gongkademy.domain.community.common.entity.pick;

import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PickType pickType;

    public Pick(Board board, Member member, PickType pickType) {
        this.board = board;
        this.member = member;
        this.pickType = pickType;
    }
}
