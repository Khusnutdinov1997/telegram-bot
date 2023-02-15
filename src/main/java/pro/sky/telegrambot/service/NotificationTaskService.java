package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);

    private static final String PATTERN = "([0-9.:\\s]{16})(\\s)([\\W+]+)";

    private static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }


    public NotificationTask schedule(NotificationTask task, Long chatId) {
        task.setChatId(chatId);

        NotificationTask storedTask = notificationTaskRepository.save(task);
        logger.info("NotificationTask has been stored successfully: " + storedTask);
        return storedTask;
    }

    public Optional<NotificationTask> parse(String notificationBotMessage) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(notificationBotMessage);

        NotificationTask result = null;
        try {
            if (matcher.find()) {
                LocalDateTime notificationDate = LocalDateTime.parse(matcher.group(1), DATA_TIME_FORMATTER);
                String notification = matcher.group(3);
                result = new NotificationTask(notification, notificationDate);
            }
        } catch (Exception e) {
            logger.error("Failed to parse notificationBotMessage: " + notificationBotMessage, e);
        }

        return Optional.ofNullable(result);
    }

    public void notifyAllScheduleTasks(Consumer<NotificationTask> notifier) {
        logger.info("Trigger sending of scheduled notifications");
        Collection<NotificationTask> notifications = notificationTaskRepository.getScheduledNotifications();
        logger.info("Found {} notifications, processing...", notifications.size());
        notifications.forEach(task -> {
            notifier.accept(task);
            task.markAsSent();
        });
        notificationTaskRepository.saveAll(notifications);
        logger.info("Finish to processing scheduled notifications");
    }

}
