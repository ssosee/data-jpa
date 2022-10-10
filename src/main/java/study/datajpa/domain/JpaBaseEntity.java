package study.datajpa.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //속성만 상속
public class JpaBaseEntity {
    @Column(updatable = false) //createTime 은 변경하지 못하게 설정
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @PrePersist //persist 전에 이벤트가 발생
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate //update 전에 이벤트가 발생
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
