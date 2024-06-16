//package com.gongkademy.domain.member.repository;
//
//import com.gongkademy.domain.member.entity.Member;
//import jakarta.persistence.EntityManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.jpa.repository.EntityGraph;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//@Slf4j
//@Repository
//public class MemberRepositoryImpl implements MemberRepository {
//
//    private final EntityManager em;
//
//    public MemberRepositoryImpl(EntityManager em) {
//        this.em = em;
//    }
//
//    @Override
//    public Member save(Member member) {
//        em.persist(member);
//        return member;
//    }
//    @Override
//    @EntityGraph(attributePaths = "memberRoleList")
//    public Optional<Member> findById(Long id){
//        Member member = em.find(Member.class,id);
//        return Optional.ofNullable(member);
//    }
//
//    @Override
//    @EntityGraph(attributePaths = "memberRoleList")
//    public Optional<Member> findByNickname(String nickname) {
//        List<Member> findMember = em.createQuery("select m from Member m where m.nickname= :nickname",Member.class)
//                .setParameter("nickname",nickname)
//                .getResultList();
//        log.info("findMember:{}",findMember);
//        return findMember.stream().findAny();
//    }
//
//    @Override
//    @EntityGraph(attributePaths = "memberRoleList") //결과 불러올 때 자동 조인
//    public Optional<Member> findByEmail(String email) {
//        List<Member> findMember = em.createQuery("select m from Member m where m.email= :email",Member.class)
//                .setParameter("email",email)
//                .getResultList();
//        return findMember.stream().findAny();
//    }
//
//
//    @Override
//    public void deleteMember(Long id) {
//        em.remove(findById(id).get());
//    }
//}
