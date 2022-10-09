package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    //애플리케이션 로딩 시점에서 컴파일 에러 발견 가능
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name)" +
            " from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional
    Page<Member> findByAge(int age, Pageable pageable);

    //카운트 쿼리를 다음과 같이 분리할 수 있다.
    @Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    /**
     * executeUpdate()을 실행하기 때문에 해당 애노테이션이 있어야 함.
     * 생략시 -> org.hibernate.hql.internal.QueryExecutionRequestException: Not supported for DML operations
     */
    @Modifying(clearAutomatically = true) //em.clear();
    @Query("update Member m set m.age = m.age + 1" +
            " where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) //연관관계가 걸려있는 Member 클래스의 Team team 을 적으면 된다.
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE) //다른 트랜잭션에서 읽기도 쓰기도 못함. (배타적 잠금)
    List<Member> findLockByUsername(String username);
}
