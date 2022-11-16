package com.infomedia.hikvisiondemo.service;

import com.infomedia.hikvisiondemo.util.Waiter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TestService {


    @PostConstruct
    private void init(){

      /*  Waiter waiter = new Waiter(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        waiter.start();*/
    }
}
