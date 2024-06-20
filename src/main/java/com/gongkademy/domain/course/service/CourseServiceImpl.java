package com.gongkademy.domain.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gongkademy.domain.course.dto.request.CourseCommentRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseLikeRequestDTO;
import com.gongkademy.domain.course.dto.request.CourseRequestDTO;
import com.gongkademy.domain.course.dto.response.CourseCommentResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseContentsResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseLikeResponseDTO;
import com.gongkademy.domain.course.dto.response.CourseResponseDTO;
import com.gongkademy.domain.course.dto.response.NoticeResponseDTO;
import com.gongkademy.domain.course.entity.Course;
import com.gongkademy.domain.course.entity.CourseComment;
import com.gongkademy.domain.course.entity.CourseLike;
import com.gongkademy.domain.course.entity.CourseLikeCateg;
import com.gongkademy.domain.course.entity.CourseReview;
import com.gongkademy.domain.course.entity.Lecture;
import com.gongkademy.domain.course.entity.Notice;
import com.gongkademy.domain.course.entity.RegistCourse;
import com.gongkademy.domain.course.entity.RegistLecture;
import com.gongkademy.domain.course.entity.Scrap;
import com.gongkademy.domain.course.repository.CourseCommentRepository;
import com.gongkademy.domain.course.repository.CourseLikeRepository;
import com.gongkademy.domain.course.repository.CourseRepository;
import com.gongkademy.domain.course.repository.CourseReviewRepository;
import com.gongkademy.domain.course.repository.LectureRepository;
import com.gongkademy.domain.course.repository.NoticeRepository;
import com.gongkademy.domain.course.repository.RegistCourseRepository;
import com.gongkademy.domain.course.repository.RegistLectureRepository;
import com.gongkademy.domain.course.repository.ScrapRepository;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
	
	private final RegistCourseRepository registCourseRepository;
	private final RegistLectureRepository registLectureRepository;
	private final ScrapRepository scrapRepository;
	private final MemberRepository memberRepository;
	private final CourseRepository courseRepository;
	private final NoticeRepository noticeRepository;
	private final LectureRepository lectureRepository;
	private final CourseLikeRepository courseLikeRepository;
	private final CourseReviewRepository courseReviewRepository;
	private final CourseCommentRepository courseCommentRepository;

	@Override
	public List<CourseResponseDTO> getAllCourses(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<Course> courses = courseRepository.findAll();
		for(Course course : courses) {
            CourseResponseDTO dto = this.convertToDTO(course);

            Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, course.getId());
			dto.setIsRegistered(isRegistered);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}
	
	@Override
	public List<CourseResponseDTO> getRegistCoursesNoComplete(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<RegistCourse> registCourses = registCourseRepository.findAllByMemberIdAndComplete(memberId, false);
		
		for(RegistCourse registCourse : registCourses) {
			Course course = courseRepository.findById(registCourse.getCourse().getId())
					.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));

            CourseResponseDTO dto = this.convertToDTO(course);
			dto.setIsRegistered(true);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}

	@Override
	public List<CourseResponseDTO> getRegistCoursesComplete(Long memberId) {
		List<CourseResponseDTO> courseResponseDTOs = new ArrayList<>();
		
		List<RegistCourse> registCourses = registCourseRepository.findAllByMemberIdAndComplete(memberId, true);
		
		for(RegistCourse registCourse : registCourses) {
			Course course = courseRepository.findById(registCourse.getCourse().getId())
					.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));

            CourseResponseDTO dto = this.convertToDTO(course);
			dto.setIsRegistered(true);
            courseResponseDTOs.add(dto);
		}
		return courseResponseDTOs;
	}


	@Override
	public List<CourseContentsResponseDTO> getCourseContents(CourseRequestDTO courseRequestDTO) {
		Long memberId = courseRequestDTO.getMemberId();
		Long courseId = courseRequestDTO.getCourseId();
		
		List<Lecture> lectures = lectureRepository.findByCourseId(courseId);
		List<CourseContentsResponseDTO> courseContentsDTOs = new ArrayList<>();
		
		for(Lecture lecture : lectures) {
			CourseContentsResponseDTO dto = this.convertToDToContents(lecture);
		
			dto.setMemberId(memberId);
			
			// 수강하고 있는 강좌면 완강여부 확인
            Boolean isRegistered = registCourseRepository.existsByMemberIdAndCourseId(memberId, courseId);
			if(isRegistered) {
				Boolean isCompleted = registLectureRepository.existsByMemberIdAndLectureIdAndComplete(memberId, lecture.getId(), true);
				dto.setIsCompleted(isCompleted);
			}
			// 아니면 다 false
			else dto.setIsCompleted(false);
			
			courseContentsDTOs.add(dto);
		}
		
		return courseContentsDTOs;
	}


	@Override
	public CourseResponseDTO registCourse(CourseRequestDTO courseRequestDTO) {
		RegistCourse registCourse = this.converToEntityRegistCourse(courseRequestDTO);
		
		registCourseRepository.save(registCourse);
		
		// 수강생 수 update
		Optional<Course> course = courseRepository.findById(courseRequestDTO.getCourseId());
		course.get().updateLectureCount();
		
		addRegistLectures(registCourse);
		
		CourseResponseDTO courseResponseDTO = this.convertToDTO(course.get());
		return courseResponseDTO;
	}

	@Override
	public CourseResponseDTO scrapCourse(CourseRequestDTO courseRequestDTO) {
		Scrap scrap = this.convertToEntityScrap(courseRequestDTO);
		scrapRepository.save(scrap);
		
		Optional<Course> course = courseRepository.findById(courseRequestDTO.getCourseId());
		CourseResponseDTO courseResponseDTO = this.convertToDTO(course.get());
		return courseResponseDTO;
	}

	@Override
	public void deleteRegistCourse(Long registCourseId) {
		RegistCourse registCourse = registCourseRepository.findById(registCourseId)
				.orElseThrow(() -> new IllegalArgumentException("수강 강좌 찾을 수 없음"));
		
		registCourseRepository.delete(registCourse);
	}

	@Override
	public CourseResponseDTO getCourseDetail(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		CourseResponseDTO courseResponseDTO = this.convertToDTO(course);
		return courseResponseDTO;
	}
	
	@Override
	public Page<NoticeResponseDTO> getCourseNotices(Long courseId, int pageNum, int pageSize) {
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		
		Page<Notice> notices = noticeRepository.findAllByCourseId(courseId, pageable);
		Page<NoticeResponseDTO> noticeResponseDtos = notices.map(m -> NoticeResponseDTO.builder()
                .id(m.getId())
                .createdTime(m.getCreatedTime())
                .content(m.getContent())
                .courseCommentCount(m.getCourseCommentCount())
                .build());
		return noticeResponseDtos;
	}
	
	// 수강 강좌에 대한 수강 강의 생성
	private void addRegistLectures(RegistCourse registCourse) {
        List<Lecture> lectures = lectureRepository.findByCourseId(registCourse.getCourse().getId());
        
        for (Lecture lecture : lectures) {
            RegistLecture registLecture = new RegistLecture();
            registLecture.setLecture(lecture);
            registLecture.setRegistCourse(registCourse);
            registLecture.setMember(registCourse.getMember());
            
            registCourse.addRegistLecture(registLecture);
        }
        registLectureRepository.saveAll(registCourse.getRegistLectures());
    }
	
	@Override
	public CourseLikeResponseDTO like(CourseLikeRequestDTO courseLikeRequestDTO) {
		if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
			if (courseLikeRepository.existsByMemberIdAndReviewId(courseLikeRequestDTO.getMemberId(), courseLikeRequestDTO.getCourseReviewId())) {
				new IllegalArgumentException("이미 좋아요를 누른 수강평입니다.");
			} else {
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO);
		        CourseLike saveLike = courseLikeRepository.save(like);
		        return convertToDTOCourseLike(saveLike);
			}
		} else if(courseLikeRequestDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
			if (courseLikeRepository.existsByMemberIdAndCourseCommentId(courseLikeRequestDTO.getMemberId(), courseLikeRequestDTO.getCourseCommentId())) {
				new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
			} else {
		        CourseLike like = convertToEntityCourseLike(courseLikeRequestDTO);
		        CourseLike saveLike = courseLikeRepository.save(like);
		        return convertToDTOCourseLike(saveLike);
			}
		}
		return null;
	}
	
	@Override
	public void deleteLike(Long id) {
		if(courseLikeRepository.existsByCourseLikeId(id)) {
			courseLikeRepository.deleteById(id);
		} else {
			new IllegalArgumentException("좋아요를 누른 적이 없습니다.");
		}
	}

	private CourseResponseDTO convertToDTO(Course course) {
		CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
		courseResponseDTO.setCourseId(course.getId());
		courseResponseDTO.setTotalCourseTime(course.getTotalCourseTime());
		courseResponseDTO.setAvgRating(course.getAvgRating());
		courseResponseDTO.setReviewCount(course.getReviewCount());
		courseResponseDTO.setCourseId(course.getId());

		return courseResponseDTO;
	}
	
	private CourseContentsResponseDTO convertToDToContents(Lecture lecture) {
		CourseContentsResponseDTO courseContentsResponseDTO = new CourseContentsResponseDTO();
		courseContentsResponseDTO.setLectureId(lecture.getId());
		courseContentsResponseDTO.setLectureOrder(lecture.getLectureOrder());
		courseContentsResponseDTO.setTime(lecture.getTime());
		courseContentsResponseDTO.setTitle(lecture.getTitle());
		return courseContentsResponseDTO;
	}

	private RegistCourse converToEntityRegistCourse(CourseRequestDTO courseRequestDTO) {
		Course course = courseRepository.findById(courseRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(courseRequestDTO.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
		RegistCourse registCourse = new RegistCourse();
		registCourse.setCourse(course);
		registCourse.setMember(member);
		return registCourse;
	}
	
	private Scrap convertToEntityScrap(CourseRequestDTO courseRequestDTO) {
		Course course = courseRepository.findById(courseRequestDTO.getCourseId())
				.orElseThrow(() -> new IllegalArgumentException("강좌 찾을 수 없음"));
		
		Member member = memberRepository.findById(courseRequestDTO.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("회원 찾을 수 없음"));
		
		Scrap scrap = new Scrap();
		scrap.setCourse(course);
		scrap.setMember(member);
		return scrap;
	}
	
    private CourseLike convertToEntityCourseLike(CourseLikeRequestDTO courseLikeDTO) {
    	CourseLike like = new CourseLike();
    	
    	like.setLikeCateg(courseLikeDTO.getLikeCateg());
    	
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.REVIEW) {
    		Optional<CourseReview> reviewOptional = courseReviewRepository.findById(courseLikeDTO.getCourseReviewId());
    		if (reviewOptional.isPresent()) {
    			like.setCourseReview(reviewOptional.get());
    		} else {
    			throw new IllegalStateException("리뷰 찾을 수 없음");
    		}
    	}
        
    	if(courseLikeDTO.getLikeCateg()== CourseLikeCateg.COMMENT) {
    		Optional<CourseComment> commentOptional = courseCommentRepository.findById(courseLikeDTO.getCourseCommentId());
    		if (commentOptional.isPresent()) {
    			like.setCourseComment(commentOptional.get());
    		} else {
    			throw new IllegalStateException("댓글 찾을 수 없음");
    		}
    	}
        
        Optional<Member> memberOptional = memberRepository.findById(courseLikeDTO.getMemberId());
        if (memberOptional.isPresent()) {
        	like.setMember(memberOptional.get());
        } else {
            throw new IllegalStateException("사용자 찾을 수 없음");
        }
        
        return like;
    }

    private CourseLikeResponseDTO convertToDTOCourseLike(CourseLike courseLike) {
    	CourseLikeResponseDTO courseLikeResponseDTO = new CourseLikeResponseDTO();
    	courseLikeResponseDTO.setCourseLikeId(courseLike.getId());
    	courseLikeResponseDTO.setLikeCateg(courseLike.getLikeCateg());
    	courseLikeResponseDTO.setCourseReviewId(courseLike.getCourseReview().getId());
    	courseLikeResponseDTO.setCourseCommentId(courseLike.getCourseComment().getId());
    	courseLikeResponseDTO.setMemberId(courseLike.getMember().getId());
        return courseLikeResponseDTO;
    }

}
