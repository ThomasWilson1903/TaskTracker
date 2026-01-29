using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using TaskTracker.Database;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Repositories;

public class UserRepository
{
#if DEBUG
    private readonly AppDbContext _context;
#else
    private readonly IRequestProvider _context;
#endif

    public ICollection<User> Users { get; } = new List<User>();

#if DEBUG
    public UserRepository(AppDbContext context)
    {
        _context = context;
    }
#else
    public UserRepository(IRequestProvider context)
    {
        _context = context;
    }
#endif

    public async System.Threading.Tasks.Task RegisterAsync(User user)
    {
        Users.Add(user);
#if DEBUG
        await _context.AddAsync(user).ConfigureAwait(false);
        await _context.SaveChangesAsync().ConfigureAwait(false);
#else
        await _context.PostAsync<User, User?>("/register", user).ConfigureAwait(false);
#endif
    }

    public async System.Threading.Tasks.Task<User?> LoginAsync(User user)
    {
#if DEBUG
        var result = await _context.Users.FirstOrDefaultAsync(u => u.Username == user.Username && u.Password == user.Password).ConfigureAwait(false);
#else
        var result = await _context.PostAsync<User, User?>("/login", user).ConfigureAwait(false);
#endif
        if (result is User u)
        {
            if (!Users.Contains(u))
                Users.Add(u);
        }

        return result;
    }
}