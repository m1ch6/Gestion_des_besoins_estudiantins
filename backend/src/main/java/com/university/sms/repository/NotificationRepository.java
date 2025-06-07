package com.university.sms.repository;

import com.university.sms.entity.Notification;
import com.university.sms.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends BaseRepository<Notification> {

    List<Notification> findByRecipient(User recipient);

    List<Notification> findByRecipientAndReadFalse(User recipient);
}
