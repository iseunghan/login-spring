package me.isunghan.loginspring.controller;

import javassist.NotFoundException;
import me.isunghan.loginspring.domain.Member;
import me.isunghan.loginspring.domain.MemberDto;
import me.isunghan.loginspring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping(value = "/{id}")
    public ResponseEntity findOne(@PathVariable Long id) throws NotFoundException {
        Member member = memberService.findOne(id);
        return ResponseEntity.ok(member);
    }

    @GetMapping
    public ResponseEntity findAll() {
        List<Member> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }

    @PostMapping
    public ResponseEntity join(@RequestBody MemberDto memberDto) {
        Member member = memberService.join(memberDto);
        URI uri = linkTo(MemberController.class).slash(member.getId()).withSelfRel().toUri();
        return ResponseEntity.created(uri).body(member);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateOne(@PathVariable Long id) throws NotFoundException {
        Member member = memberService.updateOne(id);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteOne(@PathVariable Long id) throws NotFoundException {
        Long deleteId = memberService.deleteOne(id);
        return ResponseEntity.ok(deleteId);
    }
}
