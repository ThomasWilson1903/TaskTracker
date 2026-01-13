using TaskTracker.Models;

namespace TaskTracker.Services;

public interface ISettingsService
{
    User CurrentUser { get; set; }

    string Token { get; set; }
}
