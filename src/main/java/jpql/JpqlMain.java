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
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("회원1");
            member.setAge(30);
            member.changeTeam(teamA);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            //  엔티티 페치 조인
//            String query = "SELECT m FROM Member m JOIN FETCH m.team";

            // 컬렉션 페치 조인
//            String query = "SELECT DISTINCT t FROM Team t JOIN t.members";
//
//            List<Team> resultList = em.createQuery(query, Team.class)
//                    .getResultList();
//
//            for (Team team : resultList) {
//                System.out.println("team = " + team.getName() + " | members=" + team.getMembers().size());
//                for(Member m : team.getMembers()){
//                    System.out.println("m = " + m);
//                }
//            }

            // 페이징
//            String query = "SELECT t From Team t";
//
//            List<Team> resultList = em.createQuery(query, Team.class)
//                    .setFirstResult(0)
//                    .setMaxResults(1)
//                    .getResultList();
//
//            for (Team team : resultList) {
//                System.out.println("team = " + team.getName() + " | members=" + team.getMembers().size());
//                for(Member m : team.getMembers()){
//                    System.out.println("m = " + m);
//                }
//            }

            // 엔팉치 직접 사용 - 기본 키 값
//            String query = "select m from Member m where m =:member";
//            Member findMember = em.createQuery(query, Member.class)
//                    .setParameter("member", member)
//                    .getSingleResult();
//
//            System.out.println("findMember = " + findMember);
//
            // 엔티티 직접 사용 - 외래 키 값
//            query = "select m from Member m where m.team = : team";
//            List<Member> members = em.createQuery(query, Member.class)
//                    .setParameter("team", teamA)
//                    .getResultList();
//
//            for (Member m : members) {
//                System.out.println("mmember = " + m);
//            }

            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            // INNER 조인
//            String query = "SELECT m FROM Member m JOIN m.teamA t";

            // OUTER 조인
//            String query = "SELECT m FROM Member m LEFT JOIN m.teamA t";

            // 세타 조인
//            String query = "SELECT m FROM Member m, Team t WHERE m.username = t.name";

            // 조인대상 필터링
//            String query = "SELECT m FROM Member m LEFT JOIN m.teamA t ON t.name = 'teamA'";

            // 연관관계 없는 외부조인
//            String query = "SELECT m FROM Member m LEFT JOIN m.teamA t ON m.username = t.name";
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
//
//
//            String query = "SELECT FUNCTION('group_concat', m.username) FROM Member m";
//            List<String> resultList = em.createQuery(query, String.class)
//                    .getResultList();

            // 경로 표현식
//            String query = "SELECT m.teamA From Member m";
//            String query = "SELECT t.members.size From Team t";

//            Integer resultList = em.createQuery(query, Integer.class)
//                    .getSingleResult();

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
