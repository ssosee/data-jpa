package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    public void memberTest() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(member.getId());

        assertThat(savedMember).isEqualTo(findMember.get());
        assertThat(findMember.get().getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.get().getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void nameQuery() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m2);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 20);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m2);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String username : usernameList) {
            System.out.println("username = "+username);
        }
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("BBB", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto="+dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("BBB", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("dasgdsag");
        System.out.println("aaa = " + aaa);
        Optional<Member> aaa1 = memberRepository.findOptionalByUsername("asdgasdg");
        System.out.println("aaa1 = " + aaa1);
        Member aaa2 = memberRepository.findMemberByUsername("asdgasdga");
        System.out.println("aaa2 = " + aaa2);
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); //???????????? 0?????? ??????
        //???????????? ??????????????? ???????????? DTO??? ????????????
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent(); //????????? ?????????

        assertThat(content.size()).isEqualTo(3); //????????? ????????? ???
        assertThat(page.getTotalElements()).isEqualTo(5); //?????? ????????? ???
        assertThat(page.getNumber()).isEqualTo(0); //????????? ??????
        assertThat(page.getTotalPages()).isEqualTo(2); //?????? ????????? ??????
        assertThat(page.isFirst()).isTrue(); //????????? ?????????????
        assertThat(page.hasNext()).isTrue(); //?????? ???????????? ??????????
    }

//    @Test
//    public void slicing() {
//        //given
//        memberRepository.save(new Member("member1", 10));
//        memberRepository.save(new Member("member2", 10));
//        memberRepository.save(new Member("member3", 10));
//        memberRepository.save(new Member("member4", 10));
//        memberRepository.save(new Member("member5", 10));
//
//        int age = 10;
//        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
//        //when
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest); //???????????? 0?????? ??????
//
//        //then
//        List<Member> content = page.getContent(); //????????? ?????????
//
//        assertThat(content.size()).isEqualTo(3); //????????? ????????? ???
//        assertThat(page.getNumber()).isEqualTo(0); //????????? ??????
//        assertThat(page.isFirst()).isTrue(); //????????? ?????????????
//        assertThat(page.hasNext()).isTrue(); //?????? ???????????? ??????????
//    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        /**
         * ????????? ????????? ???????????? ??????
         * ????????? ??????????????? ???????????? ???????????? ?????????,
         * ????????? ??????????????? ?????? ???????????? ????????? DB??? ????????? ????????? ????????? ??? ??????.
         *
         * 1. ????????? ??????????????? ???????????? ?????? ???????????? ?????? ????????? ?????? ??????
         * 2. ??????????????? ????????? ??????????????? ???????????? ????????? ?????? ?????? ?????? ????????? ??????????????? ????????? ??????.
         */
        int resultCount = memberRepository.bulkAgePlus(20); //???????????? ??????
//        em.flush(); //???????????? ?????? ???????????? ?????? DB??? ??????
//        em.clear(); //????????? ???????????? ?????????


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    /**
     * EntityGraph
     * ????????? ??????????????? SQL ????????? ???????????? ?????? (fetch join)
     *
     * member -> team ???????????? ??????
     * ????????? team ??? ????????? ???????????? ?????? ????????? ????????????.(N+1 ?????? ??????)
     */
    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member = " + member.getTeam().getClass());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); //commit ?????? ????????? ?????????
        em.clear();

        //when
        /**
         * JPA ?????? ??????(SQL ????????? ????????? JPA ??????????????? ???????????? ??????)
         */
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush(); //Update Query ?????? X
    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        /**
         * select
         *         member0_.member_id as member_i1_0_,
         *         member0_.age as age2_0_,
         *         member0_.team_id as team_id4_0_,
         *         member0_.username as username3_0_
         *     from
         *         member member0_
         *     where
         *         member0_.username=? for update
         */
        List<Member> findMember = memberRepository.findLockByUsername("member1");
        for (Member member : findMember) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    /**
     * ?????? ?????? ??????..
     */
    @Test
    public void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    /**
     * ?????? ????????? ???????????? ??????
     * ????????? ?????? ????????? ??????
     * ????????? ???????????? RDB?????? NOSQL??? ???????????? ?????? ????????? ?????? ????????? ?????? ??????.
     * ????????? ????????? JPA JpaRepository ?????????????????? ?????? ??????
     *
     * ??????????????? Querydsl ?????? ??????
     */
    @Test
    public void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        /**
         * Probe: ????????? ???????????? ?????? ?????? ????????? ??????
         * ExampleMatcher: ?????? ????????? ??????????????? ????????? ?????? ??????, ????????? ??????
         */
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");
//        for (UsernameOnly usernameOnly : result) {
//            System.out.println("usernameOnly = " + usernameOnly);
//        }

//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
//        for (UsernameOnlyDto usernameOnlyDto : result) {
//            System.out.println("usernameOnlyDto = " + usernameOnlyDto);
//        }

        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections);

        }
    }

    @Test
    void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        Member result = memberRepository.findByNativeQuery("m1");
//        System.out.println("result = " + result);

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
        System.out.println("result = " + result);
    }
}