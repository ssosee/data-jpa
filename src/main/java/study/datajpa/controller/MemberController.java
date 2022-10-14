package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.domain.Member;
import study.datajpa.dto.MemberDto;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * <h1>도메인 클래스 컨버터</h1>
     * <p>HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환</p>
     * <p>도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음</p>
     * <br>
     * <p>조회용으로만 사용할 것을 권고</p>
     * <p>-> 트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.</p>
     */
    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        return map;

//        return memberRepository.findAll(pageable)
//                .map(m -> new MemberDto(m.getId(), m.getUsername(), null));

//        return memberRepository.findAll(pageable)
//                .map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for(int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i, 10));
        }
    }
}
