using TaskTracker.Models;

namespace TaskTracker.Services;

public interface IIdentityService
{
    System.Threading.Tasks.Task LogIn(User user);

    System.Threading.Tasks.Task Register(User user);
}
