using System.Collections.ObjectModel;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Tests.Services;

public class FakeToBackSender : IRequestProvider
{
    public Collection<User> Users { get; } = new Collection<User>();
    public Collection<TaskTracker.Models.Task> Tasks { get; } = new Collection<TaskTracker.Models.Task>();

    public Task<TResult?> PostAsync<TRequest, TResult>(string path, TRequest data, string token = "")
    {
        if (data is null)
            throw new ArgumentNullException(nameof(data));

        if (data is User user)
            Users.Add(user);

        if (data is TaskTracker.Models.Task task)
            Tasks.Add(task);

        return System.Threading.Tasks.Task.FromResult(default(TResult));
    }

    public async Task<T?> GetAsync<T>(string path, string token = "") =>
        typeof(T) switch
        {
            var t when t == typeof(User) => await System.Threading.Tasks.Task.FromResult((T)(object)Users.ToList().FirstOrDefault()).ConfigureAwait(false),
            var t when t == typeof(TaskTracker.Models.Task) => await System.Threading.Tasks.Task.FromResult((T)(object)Tasks.ToList().FirstOrDefault()).ConfigureAwait(false),
            _ => await System.Threading.Tasks.Task.FromResult(default(T)).ConfigureAwait(false)
        };

    public System.Threading.Tasks.Task DeleteAsync(string path, string token = "")
    {
        throw new NotImplementedException();
    }
}