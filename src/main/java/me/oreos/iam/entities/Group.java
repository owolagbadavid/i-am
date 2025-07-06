package me.oreos.iam.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Entity(name = "groups")
@AttributeOverride(
    name = "isActive",
    column = @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
)
public class Group extends MyBaseEntity<Integer> {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", nullable = true)
    private String description;

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "group")
    private List<UserGroup> userGroups = new ArrayList<>();

    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "group")
    private List<Resource> resources = new ArrayList<>();
}
