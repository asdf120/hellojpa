package jpql;

import jpql.dto.MemberDTO;

import javax.persistence.*;
import java.util.List;

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

            em.flush();
            em.clear();

            // 엔티티 프로젝션
//            List<Member> result = em.createQuery("SELECT m FROM Member m", Member.class)
//                            .getResultList();
//            Member findMember = result.get(0);
//            findMember.setAge(20);

            // 조인 엔티티 프로젝션
//            List<Team> result = em.createQuery("SELECT t FROM Member m JOIN m.team t", Team.class)
//                    .getResultList();
            
            // 임베디드 타입 프로젝션
//            em.createQuery("SELECT o.address from Order o", Address.class)
//                    .getResultList();

            // 스칼라 프로젝션 - Object[] 타입으로 조회
//            List resultList = em.createQuery("SELECT DISTINCT m.username, m.age from Member m")
//                    .getResultList();
//            Object o = resultList.get(0);
//            Object[] result = (Object[]) o;
//            System.out.println("username = " + result[0]);
//            System.out.println("age = " + result[1]);

            // 스칼라 프로젝션 - new 명령어로 조회
            List<MemberDTO> resultList = em.createQuery("SELECT new jpql.dto.MemberDTO(m.username, m.age) FROM Member m", MemberDTO.class)
                    .getResultList();

            for (MemberDTO memberDTO : resultList) {
                System.out.println("memberDTO username= " + memberDTO.getUsername());
                System.out.println("memberDTO age= " + memberDTO.getAge());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
