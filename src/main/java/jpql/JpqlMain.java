package jpql;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
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
            member.setUsername("관리자");
            member.setAge(30);
            member.changeTeam(team);
            member.setType(MemberType.ADMIN);
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
//            String query = "SELECT m FROM Member m LEFT JOIN m.team t ON m.username = t.name";
//            List<Member> result = em.createQuery(query, Member.class)
//                    .getResultList();

//            System.out.println("result = " + result.size());

            // 타입 표현
//            String query = "SELECT m.username, 'HELLO', TRUE FROM Member m" +
//                    " where m.type = :userType ";
//            List<Object[]> result = em.createQuery(query)
//                    .setParameter("userType", MemberType.ADMIN)
//                    .getResultList();
//
//            for (Object[] objects : result) {
//                System.out.println("objects[0] = " + objects[0]);
//                System.out.println("objects[1] = " + objects[1]);
//                System.out.println("objects[2] = " + objects[2]);
//            }

            // 기본 CASE식
//            String query = "SELECT" +
//                    " CASE WHEN m.age <= 10 THEN '학생요금'" +
//                    "       WHEN m.age >= 60 THEN '경로요금'" +
//                    "       ELSE '일반요금'" +
//                    " END" +
//                    " FROM Member m";
//
//            List<String> resultList = em.createQuery(query, String.class)
//                    .getResultList();
//            for (String s : resultList) {
//                System.out.println("s = " + s);
//            }

            // 조건식 - coalesce
//            String query = "SELECT COALESCE(m.username, '이름 없는 회원') FROM Member m ";
//            List<String> resultList = em.createQuery(query, String.class)
//                    .getResultList();
//
//            for (String s : resultList) {
//                System.out.println("s = " + s);
//            }

            // 조건식 - NULLIF
//            String query = "SELECT NULLIF(m.username, '관리자') FROM Member m ";
//            List<String> resultList = em.createQuery(query, String.class)
//                    .getResultList();
//
//            for (String s : resultList) {
//                System.out.println("s = " + s);
//            }

            // JPQL 함수
//            String query = "SELECT CONCAT('a','b') FROM Member m";

            // LOCATE - 찾고자하는 문자열 시작 위치 반환
//            String query = "SELECT LOCATE('ae', 'abcdefg') FROM Member m";


            // 사용자 정의 함수 호출
//            Member member2 = new Member();
//            member2.setUsername("meber2");
//            em.persist(member2);
//
//            String query = "SELECT FUNCTION('group_concat', m.username) FROM Member m";
//            List<String> resultList = em.createQuery(query, String.class)
//                    .getResultList();

            // 경로 표현식
//            String query = "SELECT m.team From Member m";
            String query = "SELECT t.members.size From Team t";

            Integer resultList = em.createQuery(query, Integer.class)
                    .getSingleResult();

            System.out.println("resultList = " + resultList);
//            for (Object s : resultList) {
//                System.out.println("s = " + s);
//            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
