package ru.twilson.tasktracker.utils;

import lombok.experimental.UtilityClass;
import ru.twilson.tasktracker.entity.Task;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TaskFormatterUtils {

    public String update(Task task, Task order) {
        StringBuilder result = new StringBuilder();
        result.append("📝 Задача была обновлена: **").append(task.getTitle()).append("**\n\n");

        boolean hasChanges = false;

        if (!task.getTitle().equals(order.getTitle())) {
            result.append("✏️ Название\n");
            result.append("`").append(task.getTitle()).append("` → `").append(order.getTitle()).append("`\n\n");
            hasChanges = true;
        }

        if (!task.getDescription().equals(order.getDescription())) {
            String oldDesc = task.getDescription();
            String newDesc = order.getDescription();
            if (oldDesc.length() > 50) oldDesc = oldDesc.substring(0, 47) + "...";
            if (newDesc.length() > 50) newDesc = newDesc.substring(0, 47) + "...";
            result.append("📄 Описание\n");
            result.append("`").append(oldDesc).append("` **→** `").append(newDesc).append("`\n\n");
            hasChanges = true;
        }

        if (!task.getPriority().equals(order.getPriority())) {
            result.append("🚨 Приоритет\n");
            result.append("`").append(task.getPriority()).append("` → `").append(order.getPriority()).append("`\n\n");
            hasChanges = true;
        }

        if (!task.getDueDate().equals(order.getDueDate())) {
            result.append("📅 Срок выполнения\n");
            result.append("`").append(formatDate(task.getDueDate())).append("` → `").append(formatDate(order.getDueDate())).append("`\n\n");
            hasChanges = true;
        }

        if (!task.getStatus().equals(order.getStatus())) {
            result.append("🔄 Статус\n");
            result.append("`").append(task.getStatus()).append("` → `").append(task.getStatus()).append("`\n\n");
            hasChanges = true;
        }

        if (!hasChanges) {
            return "## ℹ️ Изменений не обнаружено\nЗадача \"" + task.getTitle() + "\" осталась без изменений.";
        }

        return result.toString();
    }

    public static String formatTask(Task task) {
        return String.format(
                "📋 *%s*\n" +
                        "📝 %s\n" +
                        "📊 *Статус:* %s\n" +
                        "🚩 *Приоритет:* %s\n" +
                        "📅 *Создана:* %s\n" +
                        "⏰ *Срок выполнения:* %s\n" +
                        "🔄 *Обновлена:* %s\n" +
                        "✅ *Завершена:* %s",
                escapeMarkdown(task.getTitle()),
                escapeMarkdown(task.getDescription()),
                formatStatus(task.getStatus()),
                formatPriority(task.getPriority()),
                formatDate(task.getCreatedAt()),
                formatDate(task.getDueDate()),
                formatDate(task.getUpdatedAt()),
                formatCompletedAt(task.getCompletedAt())
        );
    }

    private static String formatCompletedAt(String completedAt) {
        if (completedAt == null) return "─";
        return formatDate(completedAt);
    }

    private static String formatStatus(String status) {
        if (status == null) return "⏳ Ожидание";
        return switch (status.toLowerCase()) {
            case "completed" -> "✅ Завершена";
            case "in_progress" -> "🔄 В процессе";
            case "pending" -> "⏳ Ожидание";
            default -> "❓ " + status;
        };
    }

    private static String formatPriority(String priority) {
        if (priority == null) return "⚪ Обычный";
        return switch (priority.toLowerCase()) {
            case "high" -> "🔴 Высокий";
            case "medium" -> "🟡 Средний";
            case "low" -> "🟢 Низкий";
            default -> "⚪ " + priority;
        };
    }

    private static String formatDate(String date) {
        if (date == null) return "─";
        try {
            Instant instant = Instant.parse(date);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Moscow"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return date;
        }
    }

    private static String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}
