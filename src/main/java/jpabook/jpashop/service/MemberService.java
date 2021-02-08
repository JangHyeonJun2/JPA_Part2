package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //기본적으로 readOnly가 먹힌다. 하지만 join메서드와 같이 다시 @Transactional를 써주게 되면 readOnly가 풀린다. 조회나 검색은 readOnly로 하면 최적화가 되지만
                                //join과 같은 데이터 변경이 일어나는 메서드에 readOnly를 사용하면 데이터가 변경이 일어나지 않는다.
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원검증(하지만 이렇게 검증을 해도 WAS가 수십개가 동시에 뜨면 이 검증도 통과 될 학률이 있다. 그래서 실무에서는 DB에서 member의 이름을 유니크로 해주는게 좋다.)
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findeMembers = memberRepository.findByName(member.getName());
        if (!findeMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //단건 회원 조회
    public Member findOneMember(Member member) {
        return memberRepository.findOne(member.getId());
    }

}
