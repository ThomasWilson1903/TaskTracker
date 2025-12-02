using System;

namespace TaskTracker.Models;

public class User
{
    public Guid Id { get; set; }

    public User()
    {
        Id = Guid.NewGuid();
    }
}