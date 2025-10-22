using TaskTracker.Models;
using TaskTracker.Repositories;
using TaskTracker.Tests.Services;

namespace TaskTracker.Tests.Repositories;

public class UserRepositoryTests
{
    private FakeToBackSender _backSender = new FakeToBackSender();

    [Fact]
    public async Task RepositorySavesUserAfterAdd()
    {
        var repository = new UserRepository(_backSender);
        var user = new User();

        await repository.Create(user);

        Assert.NotNull(repository.Users.FirstOrDefault(u => u.Id == user.Id));
    }

    [Fact]
    public async Task RepositorySendDataAfterAdd()
    {
        var repository = new UserRepository(_backSender);
        var user = new User();

        await repository.Create(user);

        Assert.NotNull(_backSender.Users.FirstOrDefault(u => u.Id == user.Id));
    }
}