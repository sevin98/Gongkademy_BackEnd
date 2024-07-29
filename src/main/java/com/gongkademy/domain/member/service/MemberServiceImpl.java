package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.security.util.JWTUtil;
import com.gongkademy.infra.s3.service.FileCateg;
import com.gongkademy.infra.s3.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.gongkademy.domain.member.entity.MemberRole.*;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final String DELETE_NICKNAME = "탈퇴회원";
    private final S3FileService s3FileService;

    /**
     * 주어진 회원 ID로 회원 정보를 가져옵니다.
     * @param id 회원 ID
     * @return 회원 정보 DTO
     */
    @Override
    public MemberInfoDTO getMemberInfo(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        return entityToMemberInfoDTO(member);
    }

    @Override
    public void validateAuthority(Long id, MemberRole role) {
        Optional.ofNullable(memberRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID)))
                .filter(member -> member.getMemberRoleList().contains(role))
                .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));
    }

    /**
     * @param id 회원 ID
     * @param memberSignUpDTO 회원가입 정보
     * @return 회원 ID
     */
    @Override
    public void joinMember(long id, MemberSignUpDTO memberSignUpDTO) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        member.addRole(USER);
        member.signup(memberSignUpDTO);

        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        jwtUtil.setRefreshToken(member.getId(), refreshToken);
    }

    /**
     * @param id 회원 ID
     * @param memberUpdateDTO 회원수정 정보
     * @return 회원 ID
     */
    @Override
    public void modifyMember(long id, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        // 프로필 사진 변경
        MultipartFile profileImage = memberUpdateDTO.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            // 조건을 기본 이미지로 맞출 수 있음
            if (member.getProfilePath() != null) {
                s3FileService.deleteFile(member.getProfilePath());
            }
            String profileImagePath = s3FileService.uploadFile(profileImage, FileCateg.PROFILE);
            member.setProfilePath(profileImagePath);
        }

        if (memberUpdateDTO.getNewNickname() != member.getNickname()) {
            member.setNickname(memberUpdateDTO.getNewNickname());
        }

        if (memberUpdateDTO.getAgreeMarketing() != member.getAgreeMarketing()) {
            member.setAgreeMarketing(memberUpdateDTO.getAgreeMarketing());
        }
        memberRepository.save(member);
    }

    /**
     * 실제 삭제가 아닌 soft-delete를 구현
     * @param id memberId
     * @return memberId
     */
    @Override
    public void deleteMember(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        member.deleteMember(DELETE_NICKNAME + member.getId());
    }

    @Override
    public void changeNotificationEnabledStatus(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        member.changeIsNotificationEnabled();
    }


}
