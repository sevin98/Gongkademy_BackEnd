package com.gongkademy.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.gongkademy.domain.course.entity.CommentCateg;
import com.gongkademy.domain.course.entity.CourseComment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourseCommentRepositoryImpl implements CourseCommentRepository {
	
    private final EntityManager em;

    @Override
	public CourseComment save(CourseComment courseComment) {
    	em.persist(courseComment);
		return courseComment;
	}
    
	@Override
	public Optional<CourseComment> findById(Long id) {
		CourseComment comment = em.find(CourseComment.class, id);
		return Optional.ofNullable(comment);
	}

	@Override
	public void deleteById(Long id) {
		em.remove(findById(id).get());		
	}

	@Override
	public List<CourseComment> findAll(CommentCateg categ, Long id) {
	    String jpql = "SELECT c FROM CourseComment c WHERE c.categ = :categ";

	    if (categ == CommentCateg.REVIEW) {
	        jpql += " AND c.course_review_id = :id";
	    } else if (categ == CommentCateg.NOTICE) {
	        jpql += " AND c.notice_id = :id";
	    }
	    
	    TypedQuery<CourseComment> query = em.createQuery(jpql, CourseComment.class);
	    query.setParameter("categ", categ);
	    query.setParameter("id", id);
	    
	    return query.getResultList();
	}

}
