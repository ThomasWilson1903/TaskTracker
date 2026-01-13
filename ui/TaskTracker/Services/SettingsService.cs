using TaskTracker.Models;

namespace TaskTracker.Services;

public class SettingsService : ISettingsService
{
    public User CurrentUser { get; set; }

    public string Token { get; set; }
}
