package com.infomedia.hikvisiondemo.util;


import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class Waiter {

    private int completeCount = 0;

    private final Object monitor = new Object();

    private final List<Process> processList;

    private ExceptionHandler exceptionHandler;

    private boolean cancel;

    private boolean cancelOnException;

    private Exception exception;

    public Waiter(Process... processList) {
        this.processList = Arrays.asList(processList);
    }

    @SneakyThrows
    public void start(){
        completeCount = 0;
        cancel = false;
        exception = null;

        for(Process p : processList){
            new Thread(() -> {
                try {
                    p.run();
                }catch (Exception e){
                    exception = e;
                    if(exceptionHandler!=null){
                        exceptionHandler.onException(e);
                    }
                    if(cancelOnException){
                        cancel();
                    }
                }
                completeCount++;
                if(checkCompletion()){
                    synchronized (monitor){
                        monitor.notifyAll();
                    }
                }
            }).start();
        }

        synchronized (monitor){
            while (!checkCompletion()&&!cancel){
                monitor.wait();
            }
        }
    }

    public void cancel() {
        this.cancel = true;
        synchronized (monitor){
            monitor.notifyAll();
        }
    }

    public void setCancelOnException(boolean cancelOnException) {
        this.cancelOnException = cancelOnException;
    }

    private boolean checkCompletion(){
        return completeCount>=processList.size();
    }

    public interface Process{
        void run() throws Exception;
    }

    public interface ExceptionHandler{
        void onException(Exception e);
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public Exception getLastException() {
        return exception;
    }
}
