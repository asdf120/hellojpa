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

## 기본 키 매핑
### 기본 키 매핑 방법
- 직접 할당: @Id만 사용
- 자동 생성(@GeneratedValue)
  - IDENTITY: 데이터베이스에 위임
    - 주로 MySQL, PostreSQL, SQL Server, DB2에서 사용 (ex: MySQL의 AUTO_INCREMENT)
    - JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
    - AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행 한 이후에 ID 값을 알 수 있음
    - IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL 실행하고 DB에서 식별자를 조회
    
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE
    - @SequenceGenerator 필요

  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
    
  - AUTO: 방언에 따라 자동 지정, 기본값

- TABLE 전략 - 매핑
  - 키 생성 전용 테이블을 만들어서 데이터베이스 시퀀스를 흉내내는 전략
  - 장점: 모든 데이터베이스에 적용 가능
  - 단점: 각 테이블이 시퀀스 테이블을 의존하므로 DB락 문제 등, 성능 이슈 발생 가능성 높음.

### 권장하는 식별자 전략
- 기본 키 제약 조건: Not null, 유일, 변하면 안된다.
- 먼 미래까지 기본키 제약 조건을 만족하는 자연키는 찾기 어렵다. 대체키 사용 권장
- 권장: Long + 대체키 + 키 생성전략 사용


# 요구사항 분석

## 도메인 모델 분석
- 회원과 주문의 관계: 회원은 여러 번 주문할 수 있다. (1:N)
  - 회원(1) : 주문(N)
- 주문과 상품의 관계: 주문할 때 여러 상품을 선택할 수 있다. 반대로 같은 상품도 여러번 주문될 수 있다. 
주문상품이라는 모델을 만들어서 다대다 관계를 일대다, 다대일 관계로 풀어냄.
  - 주문(1) : 주문상품(N), 주문상품(N) : 상품(1)

## 데이터 중심 설계의 문제점
```java
public class Order{

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberId; 
```
- 위 방식은 객체 설계를 테이블 설계에 맞춘 방식
- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없으므로 UML도 잘못됨

# 연관관계 매핑

## 목표
- 객체와 테이블 연관관계의 차이를 이해
- 객체의 참조와 테이블의 외래 키를 매핑
- 용어 이해
  - 방향(Direction): 단방향, 양방향
  - 다중성(Multiply): 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M) 이해
  - 연관관계의 주인(Owner): 객체 양방향 연관관계는 관리 주인이 필요

### 예제 시나리오
- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다. (N:1)

```java
public class Order{

    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    //객체를 테이블에 맞춰 테이블 연관관계의 Id로만 필드를 구성한경우
    @Column(name = "MEMBER_ID")
    private Long memberId;
```
객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력관계를 만들 수 없다.
- 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다.
- **객체는 참조를 사용** 해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 위 설명과 같은 큰 차이가 있다.

## 양방향 연관관계

### 연관관계의 주인과 mappedBy
객체와 테이블간에 연관관계를 맺는 차이를 이해해야 한다.

객체와 테이블이 관계를 맺는 차이
- 객체 연관관계 = 2개
  - 회원 -> 팀 연관관계 1개(단방향)
  - 팀 -> 회원 연관관계 1개(단방향)
- 테이블 연관관계 = 1개
  - 회원 <-> 팀의 연관관계 1개(양방향)

객체의 양방향 관계
- 객체의 양방향 관계는 양방향 관계가 아니라 서로 다른 단방향 관계 2개다.
- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
- A -> B (a.getB()) // class A { B b;}
- B -> A (b.getA()) // class B { A a;}

테이블의 양방향 연관관계
- 테이블은 외래 키 하나로 두 테이블의 연관관계를 관리
- member.team_id 외래키 하나로 양방향 연관관계를 가짐 (양쪽으로 조인 가능)
```
  SELECT *
  FROM member m
  JOIN team t ON m.team_id = t.team_id
  
  SELECT *
  FROM team t
  JOIN member m ON t.team_id = m.team_id
```

### 연관관계의 주인
양방향 매핑 규칙
- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
- 연관관계의 주인만이 외래 키를 관리(등록, 수정)
- 주인이 아닌쪽은 읽기만 가능
- 주인은 mappedBy 속성 사용X
- 주인이 아니면 mappedBy 속성으로 주인 지정

누구를 주인으로?
- 외래 키가 있는 곳을 주인으로 정한다.

양방향 매핑시 연관관계의 주인에 값을 입력해야 한다. (순수한 객체 관계를 고려하면 항상 양쪽다 값을 입력해야 한다.)
```java
Team team = new Team();
team.setName("teamA");
em.persist(team);

Member member = new Member();
member.setName("member1");

team.getMembers().add(member);
//연관관계의 주인에 값 설정
member.setTeam(team);
em.persist(member);
```

### 양방향 연관관계 주의 - 실습
- **순수 객체 상태를 고려해서 항상 양쪽에 값을 설정**
- 연관관계 편의 메소드를 생성하자
```java
class Member {
    // ...
  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }    
}

```
- 양방향 매핑시에 무한 루프를 조심하자
  - ex): toString(), lombok, JSON 생성 라이브러리

### 양방향 매핑 정리
- 단방향 매핑만으로도 이미 연관관계 매핑은 완료
- 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가된 것
- JPQL에서 역방향으로 탐색할 일이 많다.
- 단방향 매핑을 잘 하고 양방향은 필요할 때 추가해도 된다. (테이블에 영향을 주지 않음)

### 연관관계의 주인을 정하는 기준
- 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안된다.
- **연관관계의 주인은 외래 키의 위치를 기준으로 정해야 한다.**

# 다양한 연관관계 매핑

## 연관관계 매핑시 고려사항 3가지
- 다중성
  - 다대일: @ManyToOne
  - 일대다: @OneToMany
  - 일대일: @OneToOne
  - 다대다: @ManyToMany
- 단방향, 양방향
  - 테이블
    - 외래 키 하나로 양쪽 조인 가능
    - 실제로는 방향이라는 개념은 없음
  - 객체
    - 참조용 필드가 있는 쪽으로만 참조 가능
    - 한쪽만 참조하면 단방향
    - 양쪽이 서로 참조하면 양방향
- 연관관계의 주인

### 다대일 (N:1)
다대일 단방향 정리
- 가장 많이 사용하는 연관관계
- 다대일의 반대는 일대다

다대일 양방향 정리
- 외래키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발

### 일대다 (1:N)
일대다 단방향 정리
- 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인
- 테이블 일대다 관계는 항상 다(N) 쪽에 외래 키가 있다.
- 객체와 테이블의 차이 때문에 반대편 테이블의 외래 키를 관리하는 특이한 구조
- @JoinColumn을 꼭 사용해야 한다. 그렇지 않으면 조인 테이블 방식(중간에 테이블을 하나 추가)을 사용한다.
- 단점
  - 엔티티가 관리하는 외래 키가 다른 테이블에 있다.
  - 연관관계 관리를 위해 추가로 UPDATE SQL 실행
- 일대다 단방향 매핑보다는 **다대일 양방향 매핑 사용**을 추천

일대다 양방향 정리
- 공식적으로 존재X
- @JoinColumn(insertable=false, updatable=false)
- 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법
- **다대일 양방향을 사용**