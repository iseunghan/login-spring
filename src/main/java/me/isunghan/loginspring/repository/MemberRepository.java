package me.isunghan.loginspring.repository;

import me.isunghan.loginspring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByName(String name);
    Optional<Member> findMemberByUsername(String username);
    Optional<Member> findMemberByPassword(String password);
}
