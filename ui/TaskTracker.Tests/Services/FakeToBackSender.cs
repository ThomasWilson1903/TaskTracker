using System.Collections.ObjectModel;
using TaskTracker.Models;
using TaskTracker.Services;

namespace TaskTracker.Tests.Services;

public class FakeToBackSender : IToBackSender
{
    public Collection<User> Users = new Collection<User>();

    public Task SendData<T>(string path, T data)
    {
        if (data is null)
            throw new ArgumentNullException(nameof(data));

        if (data is User user)
            Users.Add(user);

        return Task.CompletedTask;
    }

    public async Task<IEnumerable<T>?> GetDatas<T>(string path) =>
        await Task.FromResult((List<T>)(typeof(T) == typeof(User) ? (object)Users.ToList() : new List<T>())).ConfigureAwait(false);
}