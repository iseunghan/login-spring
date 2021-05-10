package me.isunghan.loginspring.service;

import javassist.NotFoundException;
import me.isunghan.loginspring.domain.Member;
import me.isunghan.loginspring.domain.MemberDto;
import me.isunghan.loginspring.repository.MemberRepository;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public Member findOne(Long id) throws NotFoundException {
        return memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member join(MemberDto memberDto) {
        Optional<Member> optional = memberRepository.findMemberByUsername(memberDto.getUsername());
        if (optional.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        String encodePW = bCryptPasswordEncoder.encode(memberDto.getPassword());
        memberDto.setPassword(encodePW);
        Member member = modelMapper.map(memberDto, Member.class);
        System.out.println(memberDto.getName() + ", " + memberDto.getUsername() + ", " + memberDto.getPassword());
        return memberRepository.save(member);
    }


    public Member updateOne(Long id, MemberDto memberDto) throws NotFoundException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));

        if (Strings.isNotBlank(memberDto.getUsername())) {
            Optional<Member> optional = memberRepository.findMemberByUsername(memberDto.getUsername());
            if (optional.isPresent()) {
                throw new IllegalStateException("이미 존재하는 아이디입니다.");
            }
            member.setUsername(memberDto.getUsername());
        }
        if (Strings.isNotBlank(memberDto.getPassword())) {
            member.setPassword(memberDto.getPassword());
        }
        return memberRepository.save(member);
    }

    public Long deleteOne(Long id) throws NotFoundException {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));
        memberRepository.delete(member);
        return id;
    }
}
