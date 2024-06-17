package com.gongkademy.domain.community.entity.board;

import com.gongkademy.domain.community.dto.request.QnaBoardRequestDto;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class QnaBoard extends ImageBoard {

    private String lectureTitle;
    private String courseTitle;

    public void update(QnaBoardRequestDto qnaBoardRequestDto) {
        this.setTitle(qnaBoardRequestDto.getTitle());
        this.setContent(qnaBoardRequestDto.getContent());
        this.setLectureTitle(qnaBoardRequestDto.getLectureTitle());
        this.setCourseTitle(qnaBoardRequestDto.getCourseTitle());
    }
}
