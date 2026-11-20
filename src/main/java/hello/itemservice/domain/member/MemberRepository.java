package hello.itemservice.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static Long sequence = 0L;

    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save: member={}",member);
        Member savedMember = store.put(member.getId(), member);
        return savedMember;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Member> findByLoginId(String loginId) {
        List<Member> allMember = findAll();
        for (Member member : allMember) {
            if (member.getLoginId().equals(loginId)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    public void clearStore() {
        store.clear();
    }
}
