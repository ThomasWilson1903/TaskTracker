using System;

namespace TaskTracker.Models;

public class User
{
    public Guid Id { get; set; }

    public string Username { get; set; }

    public string Password { get; set; }

    public User()
    {
        Id = Guid.NewGuid();
        Username = string.Empty;
        Password = string.Empty;
    }
}