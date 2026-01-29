using Microsoft.EntityFrameworkCore;

namespace TaskTracker.Database;

public class AppDbContext : DbContext
{
    public DbSet<Models.User> Users { get; set; }

    public DbSet<Models.Task> Tasks { get; set; }

    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
        Database.EnsureCreated();
    }
}
