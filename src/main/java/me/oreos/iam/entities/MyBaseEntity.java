package me.oreos.iam.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.wakanda.framework.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@MappedSuperclass
public class MyBaseEntity<T extends Serializable> extends BaseEntity<T> {
    @JsonIgnore
    @Column(
      name = "deleted_on",
      nullable = true
    )
    @Temporal(TemporalType.TIMESTAMP)
    @Type(
      type = "org.wakanda.framework.util.TimeBridgeForMillis"
    )
    private DateTime deletedOn; 
}
