package com.supportportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBERS_OF_ATTEMPTS = 5;
    private static final int ATTEMPTS_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService(){
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });

    }

    public void evictUserFromLoginAttemptCache(String username){
        try{
            loginAttemptCache.invalidate(username);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addUserToLoggingAttemptCache(String username){
        int attempts = 0;
        try{
            attempts = ATTEMPTS_INCREMENT + loginAttemptCache.get(username);
            loginAttemptCache.put(username, attempts);
        }catch(ExecutionException e){
            e.printStackTrace();
        }
    }

    public boolean hasExceededMaxAttempts(String username){
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBERS_OF_ATTEMPTS;
        }catch(ExecutionException e){
            e.printStackTrace();
        }
        return false;
    }
}
