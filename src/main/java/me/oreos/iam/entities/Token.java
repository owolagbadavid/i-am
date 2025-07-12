package me.oreos.iam.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;

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
@Where(clause = "is_active = true AND deleted_on IS NULL")
@Entity(name = "tokens")
@AttributeOverride(
    name = "isActive",
    column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
)
public class Token extends MyBaseEntity<Integer> {
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(
      name = "expires_at",
      nullable = false
    )
    @Temporal(TemporalType.TIMESTAMP)
    @Type(
      type = "org.wakanda.framework.util.TimeBridgeForMillis"
    )
    private DateTime expiresAt;

    @JsonIgnore
    @Column(name = "ip_address", nullable = true)
    private String ipAddress;

    @JsonIgnore
    @Column(name = "device_info", nullable = true)
    private String deviceInfo;

    @JsonIgnore
    @Column(name = "login_location", nullable = true)
    private String loginLocation;

    @Column(name = "last_used_on", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Type(
      type = "org.wakanda.framework.util.TimeBridgeForMillis"
    )
    private DateTime lastUsedOn;
}
