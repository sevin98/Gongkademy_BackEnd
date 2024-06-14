package com.gongkademy.domain.community.entity.board;

import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String title;

    private String content;

    private LocalDateTime createTime = LocalDateTime.now();

    private Long likeCount;

    private Long hit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getBoards().add(this);
    }
}
