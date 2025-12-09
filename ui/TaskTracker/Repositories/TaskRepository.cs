using System;
using System.Collections.Generic;
using System.Linq;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class TaskRepository
{
    private readonly IToBackSender _context;
    private const string Path = "/tasks";

    public TaskRepository(IToBackSender context)
    {
        _context = context;
    }

    public async System.Threading.Tasks.Task Create(Models.Task task)
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
        await _context.Post(Path, data).ConfigureAwait(false);
    }

    public async System.Threading.Tasks.Task<IEnumerable<Models.Task>?> GetAllByUserId(Guid userId)
    {
        var path = $"{Path}?user_id={userId.ToString()}";
        var tasks = await _context
            .GetData<IEnumerable<Models.Task>>(path)
            .ConfigureAwait(false);

        return tasks;
    }

    public async System.Threading.Tasks.Task<Models.Task?> GetById(Guid taskId)
    {
        var path = $"{Path}?taskId={taskId.ToString()}";
        var task = await _context.GetData<Models.Task>(path).ConfigureAwait(false);

        return task;
    }

    public async System.Threading.Tasks.Task Delete(Guid taskId)
    {
        var path = $"{Path}?taskId={taskId.ToString()}";
        //await _context.Delete()
    }
}