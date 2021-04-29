package me.isunghan.loginspring.service;

import javassist.NotFoundException;
import me.isunghan.loginspring.domain.Member;
import me.isunghan.loginspring.domain.MemberDto;
import me.isunghan.loginspring.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    @DisplayName("하나의 Member 조회")
    void findOne() throws NotFoundException {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("lsh");
        memberDto.setUsername("user1");
        memberDto.setPassword("1234");
        Member member = modelMapper.map(memberDto, Member.class);
        Member saveMember = memberRepository.save(member);

        // When
        Member findMember = memberService.findOne(saveMember.getId());

        // Then
        assertEquals(findMember.getName(), saveMember.getName());
        assertEquals(findMember.getUsername(), saveMember.getUsername());
        assertEquals(findMember.getPassword(), saveMember.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 Member 조회시 Exception")
    void findOne_404() throws NotFoundException {
        // Given

        // When
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> memberService.findOne(100L));

        // Then
        assertEquals(nfe.getMessage(), "존재하지 않는 아이디입니다.");
    }

    @Test
    @DisplayName("모든 Member 조회")
    void findAll() {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("LSH");
        memberDto.setUsername("user2");
        memberDto.setPassword("1234");
        MemberDto memberDto2 = new MemberDto();
        memberDto2.setName("KIM");
        memberDto2.setUsername("userKim2");
        memberDto2.setPassword("1234");
        memberService.join(memberDto);
        memberService.join(memberDto2);

        // When
        List<Member> members = memberService.findAll();

        // Then
        assertEquals(members.size(), 2);
        assertEquals(members.get(0).getUsername(), memberDto.getUsername());
        assertEquals(members.get(0).getPassword(), memberDto.getPassword());
        assertEquals(members.get(0).getName(), memberDto.getName());
        assertEquals(members.get(1).getUsername(), memberDto2.getUsername());
        assertEquals(members.get(1).getPassword(), memberDto2.getPassword());
        assertEquals(members.get(1).getName(), memberDto2.getName());
    }

    @Test
    @DisplayName("Member 회원가입")
    void join() {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("LSH");
        memberDto.setUsername("user3");
        memberDto.setPassword("1234");

        // When
        Member member = memberService.join(memberDto);

        // Then
        assertEquals(member.getUsername(), memberDto.getUsername());
        assertEquals(member.getPassword(), memberDto.getPassword());
        assertEquals(member.getName(), memberDto.getName());
    }

    @Test
    @DisplayName("Member 회원가입 중복된 회원 Exception")
    void join_400() {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("LSH");
        memberDto.setUsername("user4");
        memberDto.setPassword("1234");
        memberService.join(memberDto);

        // When
        IllegalStateException ise = assertThrows(IllegalStateException.class, () -> memberService.join(memberDto));

        // Then
        assertEquals(ise.getMessage(), "이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("Member id,pw 수정")
    void updateOne() throws NotFoundException {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("name");
        memberDto.setUsername("user5");
        memberDto.setPassword("1234");
        Member member = memberService.join(memberDto);

        // When
        MemberDto memberDto1 = new MemberDto();
        memberDto1.setUsername("newUser5");
        memberDto1.setPassword("new1234");
        Member updateMember = memberService.updateOne(member.getId(), memberDto1);

        // Then
        assertEquals(updateMember.getUsername(), memberDto1.getUsername());
        assertEquals(updateMember.getPassword(), memberDto1.getPassword());
    }

    @Test
    @DisplayName("Member id,pw 수정시 중복아이디 exception")
    void updateOne_400() throws NotFoundException {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("name");
        memberDto.setUsername("user6");
        memberDto.setPassword("1234");
        Member member = memberService.join(memberDto);

        // When
        MemberDto memberDto1 = new MemberDto();
        memberDto1.setUsername("user6");    // duplicated
        memberDto1.setPassword("new1234");
        IllegalStateException ise = assertThrows(IllegalStateException.class, () -> memberService.updateOne(member.getId(), memberDto1));

        // Then
        assertEquals(ise.getMessage(), "이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("하나의 member 삭제")
    void deleteOne() throws NotFoundException {
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("name");
        memberDto.setUsername("user7");
        memberDto.setPassword("1234");
        Member member = memberService.join(memberDto);

        // When
        Long deleteId = memberService.deleteOne(member.getId());

        // Then
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> memberService.findOne(deleteId));
        assertEquals(nfe.getMessage(), "존재하지 않는 아이디입니다.");
    }
}