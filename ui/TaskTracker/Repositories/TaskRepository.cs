using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using TaskTracker.Database;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class TaskRepository
{
#if DEBUG
    private readonly AppDbContext _context;
#else
    private readonly IRequestProvider _context;
#endif
    private const string Path = "/tasks";

#if DEBUG
    public TaskRepository(AppDbContext context)
    {
        _context = context;
    }
#else
    public TaskRepository(IRequestProvider context)
    {
        _context = context;
    }
#endif

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
#if DEBUG
        await _context.AddAsync(new Task(Guid.NewGuid(), data.Title, data.Description, null, data.Priority, data.DueDate, null, null, null, data.UserId)).ConfigureAwait(false);
        await _context.SaveChangesAsync().ConfigureAwait(false);
#else
        await _context.PostAsync<object, Models.Task>(Path, data).ConfigureAwait(false);
#endif
    }

    public async System.Threading.Tasks.Task<IEnumerable<Models.Task>?> GetAllByUserIdAsync(Guid userId)
    {
        var path = $"{Path}?user_id={userId.ToString()}";
#if DEBUG
        var tasks = await _context.Tasks
            .Where(t => t.UserId == userId)
            .ToListAsync()
            .ConfigureAwait(false);
#else
        var tasks = await _context
            .GetAsync<IEnumerable<Models.Task>>(path)
            .ConfigureAwait(false);
#endif
        return tasks;
    }

    public async System.Threading.Tasks.Task<Models.Task?> GetByIdAsync(Guid taskId)
    {
        var path = $"{Path}/{taskId.ToString()}";
#if DEBUG
        var task = await _context.Tasks
            .FirstOrDefaultAsync(t => t.Id == taskId)
            .ConfigureAwait(false);
#else
        var task = await _context.GetAsync<Models.Task>(path).ConfigureAwait(false);
#endif

        return task;
    }

    public async System.Threading.Tasks.Task DeleteAsync(Guid taskId)
    {
        var path = $"{Path}/{taskId.ToString()}";
#if DEBUG
        var task = await _context.Tasks
            .FirstOrDefaultAsync(t => t.Id == taskId)
            .ConfigureAwait(false);
        if (task is null)
            return;
        
        _context.Remove<Task>(task);
        await _context.SaveChangesAsync().ConfigureAwait(false);
#else
        await _context.DeleteAsync(path).ConfigureAwait(false);
#endif
    }
}