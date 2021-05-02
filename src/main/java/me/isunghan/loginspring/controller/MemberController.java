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
    public ResponseEntity findOne(@PathVariable Long id) {
        Member member = null;
        try {
            member = memberService.findOne(id);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @GetMapping
    public ResponseEntity findAll() {
        List<Member> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/login/{username}")
    public ResponseEntity login(@PathVariable String username) {
        Member member = null;
        try {
            member = memberService.findByUsername(username);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping
    public ResponseEntity join(@RequestBody MemberDto memberDto) {
        Member member = memberService.join(memberDto);
        URI uri = linkTo(MemberController.class).slash(member.getId()).withSelfRel().toUri();
        return ResponseEntity.created(uri).body(member);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateOne(@PathVariable Long id, @RequestBody MemberDto memberDto) {
        Member member = null;
        try {
            member = memberService.updateOne(id, memberDto);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteOne(@PathVariable Long id) throws NotFoundException {
        Long deleteId = memberService.deleteOne(id);
        return ResponseEntity.ok(deleteId);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity DuplicationException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
