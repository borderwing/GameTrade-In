package com.bankrupted.tradein;

import com.bankrupted.tradein.model.CustomerEntity;
import com.bankrupted.tradein.model.RoleEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.repository.CustomerRepository;
import com.bankrupted.tradein.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.bankrupted.tradein.repository.RoleRepository;
import com.bankrupted.tradein.repository.UserRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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
    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args){
        SpringApplication.run(TradeInApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        RoleEntity customerRole = initializeRole(RoleRepository.customerRoleName);
        RoleEntity adminRole = initializeRole(RoleRepository.adminRoleName);
        RoleEntity verifierRole = initializeRole(RoleRepository.verifierRoleName);

        Collection<RoleEntity> adminRoles = new ArrayList<RoleEntity>();
        adminRoles.add(customerRole);
        adminRoles.add(adminRole);
        adminRoles.add(verifierRole);
        Collection<RoleEntity> customerRoles = new ArrayList<>();
        customerRoles.add(customerRole);


        addAdmin("super", "super", adminRoles);
        addCustomer("lykavin", "lykavin",
                    "lykavin@hotmail.com", "18621588356", 100,
                    customerRoles);
        addCustomer("borderwing", "borderwing",
                "borderwing@hotmail.com", "13213544314", 100,
                customerRoles);

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

    private UserEntity addCustomer
            (String username, String password,
             String email, String phone, Integer rating,
             Collection<RoleEntity> roles){
        UserEntity user = userRepository.findByUsername(username);
        if(user != null) {
            return user;
        }
        CustomerEntity customer = new CustomerEntity();
        customer.setUsername(username);
        customer.setPassword(SecurityUtility.passwordEncoder().encode(password));
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setRating(rating);

        // add default customer role
        customer.setRoles(roles);

        customer = customerRepository.saveAndFlush(customer);
        return customer;
    }

    private UserEntity addAdmin
            (String username, String password, Collection<RoleEntity> roles){
        UserEntity user = userRepository.findByUsername(username);
        if(user != null) {
            return user;
        }
        user = new UserEntity();
        user.setUsername(username);
        user.setPassword(SecurityUtility.passwordEncoder().encode(password));
        user.setRoles(roles);

        user = userRepository.saveAndFlush(user);
        return user;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}
