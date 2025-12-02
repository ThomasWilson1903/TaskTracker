using System;
using System.Collections.Generic;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class UserRepository
{
    private readonly IToBackSender _context;
    
    public ICollection<User> Users { get; } = new List<User>();

    public UserRepository(IToBackSender context)
    {
        _context = context;
    }

    public async System.Threading.Tasks.Task Create(User user)
    {
        Users.Add(user);
        await _context.Post("/user", user).ConfigureAwait(false);
    }

    public async System.Threading.Tasks.Task<User?> Get(Guid id)
    {
        var result = await _context.GetData<User?>($"/user?user_id={id}").ConfigureAwait(false);

        if (result is not null)
        {
            if (!Users.Contains(result))
                Users.Add(result);
        }

        return result;
    }

    public void Delete(User user)
    {
        Users.Remove(user);
    }
}