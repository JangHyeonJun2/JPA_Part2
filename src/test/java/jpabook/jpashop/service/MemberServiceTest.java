package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setName("Jang");


        //when
        Long savedId = memberService.join(member);


        //then
        Assertions.assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }

    @Test()
    public void 중복_회원_예외(){
        //given
        Member member = new Member();
        member.setName("jang");

        Member member1 = new Member();
        member1.setName("jang");

        //when
        memberService.join(member);
//        memberService.join(member1);// 에외가 발생해야한다.
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> memberService.join(member1));
        Assertions.assertThat(illegalStateException.getMessage()).isEqualTo("이미 존재하는 회원입니다.");


        //then
//        Assertions.fail("여기까지 내려오면 안된다.");
    }
}