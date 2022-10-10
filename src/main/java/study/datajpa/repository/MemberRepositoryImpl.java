package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 리포지토리 인터페이스 이름 + Impl
 * 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
