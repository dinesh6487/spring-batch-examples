package com.main.java.ngt.mts2.processor;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.main.java.ngt.mts2.model.User;

@Component
public class UserProcessor implements ItemProcessor<User, User> {

  private String threadName;

  public String getThreadName() {
    return threadName;
  }

  public void setThreadName(String threadName) {
    this.threadName = threadName;
  }

  @Override
  public User process(User item) throws Exception {
    System.out.println(threadName + " processing : "
        + item.getId() + " : " + item.getUsername());
    return item;
  }
}
