package repository;

import model.AddressEntity;
import model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lykav on 2017/6/30.
 */
public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {


    @Query("select p from AddressEntity p where p.user=:user and p.addressId=:addressId")
    AddressEntity findByUserAndId(@Param("user")UserEntity user,@Param("addressId")int addressId);


    @Modifying
    @Transactional
    @Query("update AddressEntity p set p.address=:address,p.phone=:phone,p.receiver=:receiver,p.region=:region where p.addressId=:addressId")
    int updateAddress(@Param("address")String address,@Param("phone")String phone,@Param("receiver")String receiver,
                      @Param("region")String region, @Param("addressId")int addressId);
}
