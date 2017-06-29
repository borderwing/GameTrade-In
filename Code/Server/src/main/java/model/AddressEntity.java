package model;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "addresses", catalog = "")
public class AddressEntity {

    private int addressId;
    private String receiver;
    private String phone;
    private String region;
    private String address;
    // FK user in address table
    private UserEntity user;


    @Id
    @Column(name = "addressID", nullable = false)
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Basic
    @Column(name = "receiver", nullable = true, length = 255)
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Basic
    @Column(name = "phone", nullable = true, length = 127)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "region", nullable = true, length = 255)
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Basic
    @Column(name = "address", nullable = true, length = 255)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressEntity that = (AddressEntity) o;

        if (addressId != that.addressId) return false;
        if (receiver != null ? !receiver.equals(that.receiver) : that.receiver != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = addressId;
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity _user) {
        user = _user;
    }

}
