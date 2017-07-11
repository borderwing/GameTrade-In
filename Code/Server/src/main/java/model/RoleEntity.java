package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by lykav on 7/11/2017.
 */
@Entity
@Table(name = "roles", catalog = "")
public class RoleEntity {

    private int roleId;

    private String name;

    @JsonIgnore
    private Collection<UserEntity> users;

    @Id
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "roleName", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    public Collection<UserEntity> getUsers() {
        return users;
    }
    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }
}
