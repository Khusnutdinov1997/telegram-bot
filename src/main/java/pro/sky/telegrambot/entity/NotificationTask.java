package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {

    public enum NotificationStatus {
        SCHEDULED,

        SENT,
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String notificationMessage;

    private LocalDateTime notificationDateTime;

    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    public NotificationTask() {
    }

    public NotificationTask(String notificationMessage, LocalDateTime notificationDateTime) {
        this.notificationMessage = notificationMessage;
        this.notificationDateTime = notificationDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationMessage='" + notificationMessage + '\'' +
                ", notificationDateTime=" + notificationDateTime +
                ", sentDate=" + sentDate +
                '}';
    }
}
