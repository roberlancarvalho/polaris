package com.roberlan.polaris.infrastructure.messaging;

import com.roberlan.polaris.core.domain.model.Task;
import com.roberlan.polaris.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationListener {
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_TASK_EVENTS)
    public void handleTaskCreated(Task task) {
        System.out.println("=================================================");
        System.out.println("[MENSAGERIA] Nova Tarefa Recebida!");
        System.out.println("Título: " + task.getTitle());
        System.out.println("Responsável: " + (task.getAssignedTo() != null ? task.getAssignedTo() : "Ninguém"));
        System.out.println("Ação: Enviando e-mail de notificação...");
        System.out.println("=================================================");
    }
}