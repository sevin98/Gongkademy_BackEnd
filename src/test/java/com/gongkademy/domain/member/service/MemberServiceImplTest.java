package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.security.util.JWTUtil;
import com.gongkademy.infra.s3.service.S3FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

//    @Mock
//    private JWTUtil jwtUtil;

    @InjectMocks
    private MemberServiceImpl memberService;
//
//    private Member member;
//
//    @BeforeEach
//    public void setup() {
//        member = Member.builder()
//                .id(1L)
//                .email("test@gmail.com")
//                .name("tnwls")
//                .build();
//    }
//
//    @Test
//    @DisplayName("회원 정보 조회")
//    public void getMemberInfo() {
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//
//        MemberInfoDTO memberInfoDTO = memberService.getMemberInfo(1L);
//
//        assertNotNull(memberInfoDTO);
//        assertEquals(member.getName(), memberInfoDTO.getName());
//    }
//
//    @Test
//    @DisplayName("회원 가입")
//    public void joinMember() {
//        MemberSignUpDTO memberSignUpDTO = MemberSignUpDTO.builder()
//                .nickname("nickname")
//                .birthday("20000221")
//                .agreeMarketing(false).build();
//
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(jwtUtil.createRefreshToken(1L)).thenReturn("refreshToken");
//
//        Long memberId = memberService.joinMember(1L, memberSignUpDTO);
//
//        assertNotNull(memberId);
//        assertEquals(memberId, member.getId());
//        verify(memberRepository).findById(1L);
//        verify(jwtUtil).createRefreshToken(1L);
//        verify(jwtUtil).setRefreshToken(1L, "refreshToken");
//    }
//
//    @Test
//    @DisplayName("회원 정보 수정")
//    public void modifyMember() {
//        MemberUpdateDTO memberUpdateDTO = MemberUpdateDTO.builder()
//                .email("test@gmail.com")
//                .newNickname("tnwls")
//                .agreeMarketing(true).build();
//
//        // spy를 사용하여 실제 Member 객체를 감시
//        Member spyMember = spy(member);
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(spyMember));
//
//        Long memberId = memberService.modifyMember(1L, memberUpdateDTO);
//
//        assertNotNull(memberId);
//        assertEquals(memberId, spyMember.getId());
//        // nickname이 수정되었는지 확인
//        assertEquals(spyMember.getNickname(), "tnwls");
//        verify(memberRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("회원 삭제")
//    public void deleteMember() {
//        doNothing().when(memberRepository).deleteById(1L);
//
//        memberService.deleteMember(1L);
//
//        verify(memberRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("알림 on/off 기능 변경")
//    public void changeNotificationEnabledStatus() {
//        // spy를 사용하여 실제 Member 객체를 감시
//        Member spyMember = spy(member);
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(spyMember));
//
//        Long memberId = memberService.changeNotificationEnabledStatus(1L);
//
//        // 알림 기능 필드값이 반대 값임을 확인
//        verify(spyMember).changeIsNotificationEnabled();
//        assertNotNull(memberId);
//        assertEquals(spyMember.isNotificationEnabled(), !member.isNotificationEnabled());
//        verify(memberRepository).findById(1L);
//    }


    @Mock
    private S3FileService s3FileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void modifyMember_withProfileImage() {
        // Given
        Long memberId = 1L;
        String newNickname = "newNickname";
        Boolean agreeMarketing = true;
        String profileImageUrl = "http://test-url/profile-images/test-file.jpg";

        MultipartFile profileImage = mock(MultipartFile.class);
        when(profileImage.isEmpty()).thenReturn(false);
        when(s3FileService.uploadProfileFile(profileImage, "profile-images")).thenReturn(profileImageUrl);

        MemberUpdateDTO memberUpdateDTO = new MemberUpdateDTO();
        memberUpdateDTO.setNewNickname(newNickname);
        memberUpdateDTO.setAgreeMarketing(true);
        memberUpdateDTO.setProfileImage(profileImage);

        Member member = new Member();
        member.setId(memberId);
        member.setNickname("oldNickname");
        member.setProfilePath("http://test-url/profile-images/old-file.jpg");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // When
        memberService.modifyMember(memberId, memberUpdateDTO);

        // Then
        verify(memberRepository, times(1)).findById(memberId);
        verify(s3FileService, times(1)).uploadProfileFile(profileImage, "profile-images");
        verify(s3FileService, times(1)).deleteFile("http://test-url/profile-images/old-file.jpg");

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository, times(1)).save(memberArgumentCaptor.capture());

        Member updatedMember = memberArgumentCaptor.getValue();
        assertEquals(newNickname, updatedMember.getNickname());
        assertEquals(true, updatedMember.getAgreeMarketing());
        assertEquals(profileImageUrl, updatedMember.getProfilePath());
    }


}