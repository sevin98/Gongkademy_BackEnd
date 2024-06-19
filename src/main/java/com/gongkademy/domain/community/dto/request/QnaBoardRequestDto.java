package com.gongkademy.domain.community.dto.request;


import com.gongkademy.domain.community.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaBoardRequestDto {

    private BoardType boardType;
    private Long memberId;
    private String title;
    private String content;

    private String lectureTitle;
    private String courseTitle;
}
