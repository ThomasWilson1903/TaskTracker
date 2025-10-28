using System.Collections.ObjectModel;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Tests.Services;

public class FakeToBackSender : IToBackSender
{
    public Collection<User> Users = new Collection<User>();

    public Task<T?> Post<T>(string path, T data)
    {
        if (data is null)
            throw new ArgumentNullException(nameof(data));

        if (data is User user)
            Users.Add(user);

        return Task.FromResult(default(T));
    }

    public async Task<T?> GetData<T>(string path) =>
        await Task.FromResult((T)(typeof(T) == typeof(User) ? (object)Users.ToList() : new List<T>())).ConfigureAwait(false);
}