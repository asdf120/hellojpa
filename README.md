# hellojpa, JPA 프로그래밍 - 기본

## JPA에서 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑 (Object Relational Mapping)
  - **영속성 컨텍스트**
    - JPA를 이해하는데 가장 중요한 용어
    - "엔티티를 영구 저장하는 환경"
    - ```
      //영속성 컨텍스트를 통해서 entity를 영속화.
      EntityManager.persist(entity); 
      ```
      - DB에 저장하는것이 아닌 영속성 컨텍스트에 저장
    - 영속성 컨텍스트는 논리적인 개념
    - 눈에 보이지 않음.
    - 엔티티 매니저를 통해 영속성 컨텍스트에 접근.

## 엔티티의 생명 주기
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
  - ```
    //객체를 생성한 상태
    Member member = new Member();
    member.setId("member1");
    member.setUsername("회원1");
    
    //1차 캐시에 저장됨
    //key: Id
    em.persist(member);
    
    //1차 캐시에서 조회
    //DB조회 전에 1차캐시에서 먼저 해당 값 조회.
    Member findMember = em.find(Member.class, "member1");
    
    //1차캐시에 없으므로 DB에서 직접 조회.
    //조회 후 1차캐시에 저장
    //저장 후 클라이언트에게 데이터(findMember2) 반환.
    Member findMember2 = em.find(Member.class, "member2");
    ```
    
- 동일성(identity) 보장
  - ```
    Member a = em.find(Member.class, "member1");
    Member b = em.find(Member.class, "member1");
    
    System.out.println(a == b); //동일성 비교 true
    ```
  - 1차 캐시로 반복 가능한 읽기(REPEATABLE READ) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공

- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
  - ```
    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    
    //엔티티 매니저는 데이터 변경시 반드시 트랜잭션을 시작해야 한다.
    transaction.begin();
    
    em.persist(객체1);
    em.persist(객체2);
    //이 과정까지는 INSERT SQL을 데이터베이스에 보내지 않는다.

    transaction.commit(); //트랜잭션 커밋. 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.(flush)
    ```
- 변경 감지(dirty checking)
  - ```
    EntityManager em = emf.createEntityManager();
    EntityTransaction transaction = em.getTransaction();
    
    //영속 엔티티 조회
    Member memberA = em.find(Member.class, "memberA");
    
    //영속 엔티티 데이터 수정
    memberA.setUsername("hi");
    memberA.setAge(20);
    
    //em.update(), em.persist() 필요X
    
    transaction.commit(); //커밋  
    ```
  - 엔티티 삭제
    - ```
      //삭제 대상 엔티티 조회
      EntityManager em = emf.createEntityManager();
      EntityTransaction transaction = em.getTransaction();
      
      Member memberA = em.find(Member.class, "memberA");
      em.remove(memberA); //엔티티 삭제
      
      transaction.commit(); //커밋  
      ```
    
- 지연 로딩(lazy loading)

## 플러시
영속성 컨텍스트의 변경내용을 데이터베이스에 반영
- 영속성 컨텍스트를 비우지 않는다.
- 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면된다.

### 플러시 발생
1. 변경 감지 
2. 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
3. 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송 (등록, 수정, 삭제 쿼리)

### 영속성 컨텍스트를 플러시하는 방법
- em.flush() - 직접 호출
- 트랜잭션 커밋 - 플러시 자동 호출
- JPQL 쿼리 실행 - 플러시 자동 호출
  - JPQL 쿼리 실행시 플러시가 자동으로 호출되는 이유
  - ```
    em.persist(memberA);
    em.persist(memberB);
    em.persist(memberC);
    
    //중간에 JPQL 실행
    query = em.createQuery("select m from Member m", Member.class);
    List<Member> members = query.getResultList();
    ```
  - 위와 같은 코드에서 ```em.persist(Member);``` 만으로는 DB에 commit이 되지 않은 상태에서 Select문을 호출하게되면 DB에 저장된 값이 없기때문에 강제로 FLUSH를 호출하여 DB에 저장되도록 함.

## 준영속 상태
- 영속 -> 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

### 준영속 상태로 만드는 방법
- em.detach(entity): 특정 엔티티만 준영속 상태로 전환
- em.clear(): 영속성 컨텍스트 완전 초기화
- em.close(): 영속성 컨텍스트 종료

# 객체와 테이블 매핑

## 엔티티 매핑 소개
- 객체와 테이블 매핑: @Entity, @Table
  - @Entity
    - JPA가 관리, 엔티티라고 표현
    - JPA를 사용해서 테이블과 매핑할 클래스는 필수로 선언
    - 주의 사항
      - 기본 생성자 필수(파라미터가 없는 public, protected 생성자)
      - final 클래스, enum, interface, inner 클래스는 사용 X
      - 저장할 필드에 final 사용 X
  - @Table
    - 엔티티와 매핑할 테이블 지정
- 필드와 컬럼 매핑
  - @Column
    - 속성
      - name: 필드와 매핑할 테이블의 컬럼 이름. default: 객체의 필드 이름
      - insertable, updatable: 등록, 변경 가능 여부. default: TRUE
      - nullable(DDL): null값의 허용여부 설정. false 설정시, DDL 생성 시 not null 제약조건 생성
      - unique(DDL): @Table의 uniqueConstrains와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용
      - columnDefinition(DDL): 데이터베이스 컬럼 정보를 직접 설정
      - length: 문자 길이 제약 조건, String 타입에만 사용. default: 255
      - precision(DDL): BigDecimal 타입에서 사용. 소수점을 포함한 전체 자릿수. default: 19
      - scale(DDL): BigDecimal 타입에서 사용. 소수의 자릿수
  - @Enumated: enum 타입을 매핑할 때 사용
    - 속성
      - value
        - EnumType.ORDINAL(default): enum순서를 데이터베이스에 저장
        - EnumType.STRING: enum 이름을 데이터베이스에 저장
  - @Lob: 데이터베이스 BLOB, CLOB 타입과 매핑
    - 지정할 수 있는 속성이 없다.
    - 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
      - CLOB: String, char[], java.sql.CLOB
      - BLOB: byte[], java.sql.BLOB
  - @Transient: 매핑하지 않을 필드에 사용
    - 데이터베이스에 저장X, 조회X
    - 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용
    ``` 
    @Transient 
    private Integer temp;
    ```

- 기본 키 매핑: @Id
- 연관관계 매핑: @ManyToOne,@JoinColumn

## 데이터베이스 스키마 자동 생성
- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
- 생성된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용
- hibernate.hbm2ddl.auto
  - 속성
    - create: 기존테이블 삭제 후 다시 생성 (DROP + CRETAE)
    - create-drop: create와 같으나 종료시점에 테이블 DROP
    - update: 변경분만 반영(운영DB에서는 사용X)
    - validate: 엔티티와 테이블이 정상 매핑되었는지만 확인
    - none: 사용하지 않음
- DDL 생성 기능: @Column
  - ex)제약 조건 추가: @Column(nullable = false, length = 10) --> 회원 이름 필수, 10자 초과 X
  - DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.
