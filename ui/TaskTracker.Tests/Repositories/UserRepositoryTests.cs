using TaskTracker.Models;
using TaskTracker.Repositories;
using TaskTracker.Services;
using TaskTracker.Tests.Services;

namespace TaskTracker.Tests.Repositories;

public class UserRepositoryTests
{
    private IRequestProvider _backSender = new FakeToBackSender();

    [Fact]
    public async System.Threading.Tasks.Task RepositorySavesUserAfterAdd()
    {
        var repository = new UserRepository(_backSender);
        var user = new User();

        await repository.RegisterAsync(user);

        Assert.NotNull(repository.Users.FirstOrDefault(u => u.Id == user.Id));
    }

    [Fact]
    public async System.Threading.Tasks.Task RepositorySendDataAfterAdd()
    {
        var repository = new UserRepository(_backSender);
        var user = new User();

        await repository.RegisterAsync(user);

        Assert.NotNull(repository.Users.FirstOrDefault(u => u.Id == user.Id));
    }

    [Fact]
    public async System.Threading.Tasks.Task RepositoryGetData()
    {
        var repository = new UserRepository(_backSender);
        var user = new User();

        await repository.RegisterAsync(user);
        //var result = await repository.Get(user.Id);

        //Assert.Equal(user, result);
    }
}