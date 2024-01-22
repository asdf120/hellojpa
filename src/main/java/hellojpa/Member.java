package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity //JPA를 사용할때 필수, jpa측에서 해당 어노테이션을 통해 객체를 관리.
public class Member extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

//    @Column(name = "TEAM_ID")
//    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Team team;

//    @OneToOne
//    @JoinColumn(name = "LOCKER_ID")
//    private Locker locker;

    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

//    public Locker getLocker() {
//        return locker;
//    }

    public List<MemberProduct> getMemberProducts() {
        return memberProducts;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

//    public void setLocker(Locker locker) {
//        this.locker = locker;
//    }

    public void setMemberProducts(List<MemberProduct> memberProducts) {
        this.memberProducts = memberProducts;
    }
}


