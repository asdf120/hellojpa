package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("teamA");
            member.setAge(30);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            // INNER 조인
//            String query = "SELECT m FROM Member m JOIN m.team t";

            // OUTER 조인
//            String query = "SELECT m FROM Member m LEFT JOIN m.team t";

            // 세타 조인
//            String query = "SELECT m FROM Member m, Team t WHERE m.username = t.name";

            // 조인대상 필터링
//            String query = "SELECT m FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'";
            
            // 연관관계 없는 외부조인
            String query = "SELECT m FROM Member m LEFT JOIN m.team t ON m.username = t.name";

            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            System.out.println("result = " + result.size());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
