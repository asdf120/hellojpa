package jpql;

import javax.persistence.*;

public class JpqlMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(30);
            em.persist(member);

            Member result = em.createQuery("SELECT m FROM Member m where m.username = :username", Member.class)
                            .setParameter("username", "member1")
                                    .getSingleResult();

            System.out.println("singleResult = " + result.getAge());

            TypedQuery<String> query2 = em.createQuery("SELECT m.username FROM Member m", String.class);
            Query query3 = em.createQuery("SELECT m.username, m.age FROM Member m");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
