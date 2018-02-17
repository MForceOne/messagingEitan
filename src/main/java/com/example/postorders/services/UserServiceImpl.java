package com.example.postorders.services;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserServiceImpl implements UsersService {

    private Map<String,String> usersMap = getUsersMap();

    private ConcurrentHashMap getUsersMap() {
        ConcurrentHashMap<String,String> result = new ConcurrentHashMap<>();
        result.put("eitan","123");
        return  result;
    }

    @Override
    public boolean isAuthorisedToUploadOrders(String user, String password) {
        return usersMap.containsKey(user) && usersMap.get(user).equals(password);
    }
}
