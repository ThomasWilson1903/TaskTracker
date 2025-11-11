using System;

namespace TaskTracker.Models;

public class Task
{
    public Guid Id { get; set; }

    public string Title { get; set; }

    public string Description { get; set; }

    public string? Status { get; set; }

    public string Priority { get; set; }

    public DateTime DueDate { get; set; }

    public DateTime? CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }

    public DateTime? CompletedAt { get; set; }

    public Guid UserId { get; set; }

    public Task(
        Guid id,
        string title,
        string description,
        string? status,
        string priority,
        DateTime dueDate,
        DateTime? createdAt,
        DateTime? updatedAt,
        DateTime? completedAt,
        Guid userId)
    {
        Id = id;
        Title = title;
        Description = description;
        Status = status;
        Priority = priority;
        DueDate = dueDate;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        CompletedAt = completedAt;
        UserId = userId;
    }
}