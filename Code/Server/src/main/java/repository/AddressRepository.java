package repository;

import model.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by lykav on 2017/6/30.
 */
public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {

}
