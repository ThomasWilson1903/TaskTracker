using System;
using System.Collections.Generic;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class TaskRepository
{
    private readonly IRequestProvider _context;
    private const string Path = "/tasks";

    public TaskRepository(IRequestProvider context)
    {
        _context = context;
    }

    public async System.Threading.Tasks.Task CreateAsync(Models.Task task)
    {
        if (task is null)
            return;

        var data = new
        {
            UserId = task.UserId,
            Title = task.Title,
            Description = task.Description,
            Priority = task.Priority,
            DueDate = task.DueDate
        };
        await _context.PostAsync<object, Models.Task>(Path, data).ConfigureAwait(false);
    }

    public async System.Threading.Tasks.Task<IEnumerable<Models.Task>?> GetAllByUserIdAsync(Guid userId)
    {
        var path = $"{Path}?user_id={userId.ToString()}";
        var tasks = await _context
            .GetAsync<IEnumerable<Models.Task>>(path)
            .ConfigureAwait(false);

        return tasks;
    }

    public async System.Threading.Tasks.Task<Models.Task?> GetByIdAsync(Guid taskId)
    {
        var path = $"{Path}/{taskId.ToString()}";
        var task = await _context.GetAsync<Models.Task>(path).ConfigureAwait(false);

        return task;
    }

    public async System.Threading.Tasks.Task DeleteAsync(Guid taskId)
    {
        var path = $"{Path}/{taskId.ToString()}";
        await _context.DeleteAsync(path).ConfigureAwait(false);
    }
}