package com.west2xianyu.service;

import java.util.Date;

public interface MailService {

    void sendEmail(String email,String yzm,String function);

    void sendFrozeEmail(String id, String username, String email, String reason, Date frozenDate, Date openDate);

    void sendReopenEmail(String id,String adminId,String email,String username);

}
