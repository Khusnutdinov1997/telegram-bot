package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final String START_CMD = "/start";

    private final TelegramBot telegramBot;

    private final NotificationTaskService notificationTaskService;
    ;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void notifiScheduledTask() {
        notificationTaskService.notifyAllScheduleTasks(this::sendMessage);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                // Process your updates here
                Message message = update.message();
                if (message.text().startsWith(START_CMD)) {
                    logger.info(START_CMD + " command has been received");
                    sendMessage(extractChatId(message), "Добро пожаловать в бот-планировщик! Для создания задачи отпрвьте сообщнеие в формате: 01.01.2023 20:00 Сделать домашнюю работу");
                } else {
                    notificationTaskService.parse(message.text()).ifPresentOrElse(
                            task -> scheduledNotification(extractChatId(message),task),
                            () -> sendMessage(extractChatId(message), "Неверно указан формат задачи или команды")
                    );
                }
            });
        } catch (Exception e) {
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void scheduledNotification(Long chatId, NotificationTask task) {
        notificationTaskService.schedule(task, chatId);
        sendMessage(chatId, "Задача успешно создана!");

    }

    private void sendMessage(Long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        telegramBot.execute(sendMessage);
    }

    private void sendMessage(NotificationTask task) {
        sendMessage(task.getChatId(), task.getNotificationMessage());
    }

    private Long extractChatId(Message message) {
        return message.chat().id();
    }

}
