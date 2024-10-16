import com.sparta.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EntityTest {

    EntityManagerFactory emf;
    EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("memo");
        em = emf.createEntityManager();
    }

    @Test
    @DisplayName("1차 캐시")
    void test1() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Member member = new Member(2L, "username1", "content");

            em.persist(member);

            //여기서 SELECT 쿼리가 발생 하는가?
            Member findMember = em.find(Member.class, 1L);

            // 2L member insert 이후, persistence update로 수정한 이후
            //여기서 SELECT 쿼리가 발생 하는가?
            Member findMember2 = em.find(Member.class, 2L);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("영속 엔티티의 동일성 보장")
    void test2() {

        EntityTransaction et = em.getTransaction();

        et.begin();

        try {

            Member a = em.find(Member.class, 1L);
            Member b = em.find(Member.class, 1L);

            System.out.println(a==b);

            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("트랜잭션을 지원하는 쓰기지연")
    void test3() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            Member memberA = new Member(1L,"username1","content1");
            Member memberB = new Member(2L,"username2","content2");
            em.persist(memberA);
            em.persist(memberB);
            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    @Test
    @DisplayName("변경감지 (Dirty Checking)")
    void test4() {
        EntityTransaction et = em.getTransaction();

        et.begin();

        try {
            // 영속 엔티티 조회
            Member memberA = em.find(Member.class, 1L);
            // 영속 엔티티 데이터 수정
            memberA.setUsername("hi");
            memberA.setContents("bye");
            //em.update(memberA); //이런 코드가 있어야 하지 않을까?
            et.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            et.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}