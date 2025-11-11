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

    public void Delete(User user)
    {
        Users.Remove(user);
    }
}