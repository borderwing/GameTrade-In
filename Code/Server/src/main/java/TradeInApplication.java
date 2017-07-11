import model.RoleEntity;
import model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import repository.RoleRepository;
import repository.UserRepository;
import utility.SecurityUtility;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by lykav on 7/11/2017.
 */
@SpringBootApplication
public class TradeInApplication implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args){
        SpringApplication.run(TradeInApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        RoleEntity customerRole = initializeRole(RoleRepository.customerRoleName);
        RoleEntity adminRole = initializeRole(RoleRepository.adminRoleName);
        RoleEntity verifierRole = initializeRole(RoleRepository.verifierRoleName);

        UserEntity superUser = userRepository.findByUsername("super");
        if(superUser == null){
            // create a super user if could not find any super user
            superUser = new UserEntity();
            superUser.setUsername("super");
            superUser.setPassword(SecurityUtility.passwordEncoder().encode("super"));

            Collection<RoleEntity> roles = new ArrayList<RoleEntity>();
            roles.add(customerRole);
            roles.add(adminRole);
            roles.add(verifierRole);

            userRepository.saveAndFlush(superUser);
        }

    }

    private RoleEntity initializeRole(final String roleName){
        RoleEntity targetRole = roleRepository.findByName(roleName);
        if(targetRole == null){
            targetRole = new RoleEntity();
            targetRole.setName(roleName);
            targetRole = roleRepository.saveAndFlush(targetRole);
        }
        return targetRole;
    }
}
