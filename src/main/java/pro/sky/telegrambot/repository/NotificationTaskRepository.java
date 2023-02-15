package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.entity.NotificationTask;


import java.util.Collection;


public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query("FROM NotificationTask WHERE notificationDateTime <= CURRENT_TIMESTAMP AND status = 'SCHEDULED'")
    Collection<NotificationTask> getScheduledNotifications();
}
