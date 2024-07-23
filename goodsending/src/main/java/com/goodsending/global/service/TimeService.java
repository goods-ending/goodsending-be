package com.goodsending.global.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class TimeService {

  public LocalDateTime getCurrentTime() {
    return LocalDateTime.now();
  }

}
