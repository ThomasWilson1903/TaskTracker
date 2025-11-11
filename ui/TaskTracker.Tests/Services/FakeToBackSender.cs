using System.Collections.ObjectModel;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Tests.Services;

public class FakeToBackSender : IToBackSender
{
    public Collection<User> Users = new Collection<User>();
    public Collection<TaskTracker.Models.Task> Tasks = new Collection<TaskTracker.Models.Task>();

    public Task<T?> Post<T>(string path, T data)
    {
        if (data is null)
            throw new ArgumentNullException(nameof(data));

        if (data is User user)
            Users.Add(user);

        if (data is TaskTracker.Models.Task task)
            Tasks.Add(task);

        return System.Threading.Tasks.Task.FromResult(default(T));
    }

    public async Task<T?> GetData<T>(string path) =>
        typeof(T) switch
        {
            var t when t == typeof(User) => await System.Threading.Tasks.Task.FromResult((T)(object)Users.ToList()).ConfigureAwait(false),
            var t when t == typeof(TaskTracker.Models.Task) => await System.Threading.Tasks.Task.FromResult((T)(object)Tasks.ToList()).ConfigureAwait(false),
            _ => await System.Threading.Tasks.Task.FromResult(default(T)).ConfigureAwait(false)
        };
}