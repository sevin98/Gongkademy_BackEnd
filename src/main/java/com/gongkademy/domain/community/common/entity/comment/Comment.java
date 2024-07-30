package com.gongkademy.domain.community.common.entity.comment;


import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Board board;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    private String nickname;

    @Builder.Default
    private Long likeCount = 0L;

    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    // 연관 관계 메서드
    public void addChildComment(Comment child) {
        children.add(child);
        child.setParent(this);
    }

    // 부모 댓글인지 확인하는 메서드
    public boolean isParent() {
        return this.parent == null;
    }
}
