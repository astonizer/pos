package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractPojo {

    @Version
    private Integer version;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = ZonedDateTime.now();
    }

}
