using System.Collections.Generic;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class UserRepository
{
    private readonly IRequestProvider _context;
    
    public ICollection<User> Users { get; } = new List<User>();

    public UserRepository(IRequestProvider context)
    {
        _context = context;
    }

    public async System.Threading.Tasks.Task RegisterAsync(User user)
    {
        Users.Add(user);
        await _context.PostAsync<User, User?>("/register", user).ConfigureAwait(false);
    }

    public async System.Threading.Tasks.Task<User?> LoginAsync(User user)
    {
        var result = await _context.PostAsync<User, User?>("/login", user).ConfigureAwait(false);

        if (result is User u)
        {
            if (!Users.Contains(u))
                Users.Add(u);
        }

        return result;
    }

    public void Delete(User user)
    {
        Users.Remove(user);
    }
}