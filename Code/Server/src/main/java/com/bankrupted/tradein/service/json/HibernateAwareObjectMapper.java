package com.bankrupted.tradein.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Created by lykav on 2017/6/29.
 */
public class HibernateAwareObjectMapper extends ObjectMapper {
    public HibernateAwareObjectMapper(){
        Hibernate4Module hm = new Hibernate4Module();
        registerModule(hm);
    }
}
