package com.gongkademy.domain.community.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {
    private String savedFolder;
    private String originalImage;
    private String saveImage;
}
