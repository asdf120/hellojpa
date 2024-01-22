# hellojpa, JPA 프로그래밍 - 기본

## JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑 (Object Relational Mapping)
  - **영속성 컨텍스트**
    - JPA를 이해하는데 가장 중요한 용어
    - "엔티티를 영구 저장하는 환경"
    - ```java
      //영속성 컨텍스트를 통해서 entity를 영속화.
      EntityManager.persist(entity); 
      ```
      - DB에 저장하는것이 아닌 영속성 컨텍스트에 저장
    - 영속성 컨텍스트는 논리적인 개념
    - 눈에 보이지 않음.
    - 엔티티 매니저를 통해 영속성 컨텍스트에 접근.

### 엔티티의 생명 주기
- 비영속 (new/transient)
  - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
  - ```
    //객체를 생성한 상태
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");
    ```
- 영속 (managed)
  - 영속성 컨텍스트에 관리 되는 상태.
  - ```
      EntityManager em = emf.createEntityManager();
      em.getTransaction().begin();
      
      //객체를 저장한 상태(영속)
      em.persist(member);
    ```
- 준영속 (detached)
  - 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제 (removed)
  - 삭제된 상태

## 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 변경 감지(dirty checking)
- 지연 로딩(lazy loading)